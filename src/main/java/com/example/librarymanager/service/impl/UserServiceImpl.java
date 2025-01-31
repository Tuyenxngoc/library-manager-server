package com.example.librarymanager.service.impl;

import com.example.librarymanager.config.properties.AdminInfo;
import com.example.librarymanager.constant.*;
import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.pagination.PaginationResponseDto;
import com.example.librarymanager.domain.dto.pagination.PagingMeta;
import com.example.librarymanager.domain.dto.request.UserRequestDto;
import com.example.librarymanager.domain.dto.response.UserResponseDto;
import com.example.librarymanager.domain.dto.response.auth.CurrentUserLoginResponseDto;
import com.example.librarymanager.domain.entity.Reader;
import com.example.librarymanager.domain.entity.User;
import com.example.librarymanager.domain.entity.UserGroup;
import com.example.librarymanager.domain.mapper.UserMapper;
import com.example.librarymanager.domain.specification.EntitySpecification;
import com.example.librarymanager.exception.BadRequestException;
import com.example.librarymanager.exception.ConflictException;
import com.example.librarymanager.exception.ForbiddenException;
import com.example.librarymanager.exception.NotFoundException;
import com.example.librarymanager.repository.ReaderRepository;
import com.example.librarymanager.repository.UserGroupRepository;
import com.example.librarymanager.repository.UserRepository;
import com.example.librarymanager.security.CustomUserDetails;
import com.example.librarymanager.service.LogService;
import com.example.librarymanager.service.UserService;
import com.example.librarymanager.util.PaginationUtil;
import com.example.librarymanager.util.UploadFileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final String TAG = "Quản lý người dùng";

    private final MessageSource messageSource;

    private final UserRepository userRepository;

    private final UserGroupRepository userGroupRepository;

    private final PasswordEncoder passwordEncoder;

    private final ReaderRepository readerRepository;

    private final UserMapper userMapper;

    private final LogService logService;

    private final UploadFileUtil uploadFileUtil;

    private User getEntity(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_ID, id));
    }

    public void validatePassword(String password) {
        if (!password.matches(CommonConstant.REGEXP_PASSWORD)) {
            throw new BadRequestException(ErrorMessage.INVALID_FORMAT_PASSWORD);
        }
    }

    @Override
    public void initAdmin(AdminInfo adminInfo, UserGroup userGroup) {
        if (userRepository.count() == 0) {
            try {
                User user = new User();
                user.setUsername(adminInfo.getUsername());
                user.setEmail(adminInfo.getEmail());
                user.setPhoneNumber(adminInfo.getPhoneNumber());
                user.setFullName(adminInfo.getName());
                user.setPassword(passwordEncoder.encode(adminInfo.getPassword()));
                user.setUserGroup(userGroup);
                user.setStatus(AccountStatus.ACTIVATED);
                userRepository.save(user);

                log.info("Create admin user successfully.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public CurrentUserLoginResponseDto getCurrentUser(CustomUserDetails userDetails) {
        if (userDetails.getUserId() != null) {
            User user = userRepository.findById(userDetails.getUserId())
                    .orElseThrow(() -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_ID, userDetails.getUserId()));

            return CurrentUserLoginResponseDto.create(user);
        } else if (userDetails.getCardNumber() != null) {
            Reader reader = readerRepository.findByCardNumber(userDetails.getCardNumber())
                    .orElseThrow(() -> new NotFoundException(ErrorMessage.Reader.ERR_NOT_FOUND_CARD_NUMBER, userDetails.getCardNumber()));

            return CurrentUserLoginResponseDto.create(reader);
        }
        return null;
    }

    @Override
    public CommonResponseDto save(UserRequestDto requestDto, String userId) {
        //Kiểm tra mật khẩu
        String password = requestDto.getPassword();
        if (password == null || password.isEmpty()) {
            throw new BadRequestException(ErrorMessage.INVALID_NOT_BLANK_FIELD);
        } else {
            validatePassword(password);
        }

        // Kiểm tra trùng lặp username hoặc email
        if (userRepository.existsByUsername(requestDto.getUsername())) {
            throw new ConflictException(ErrorMessage.Auth.ERR_DUPLICATE_USERNAME, requestDto.getUsername());
        }
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new ConflictException(ErrorMessage.Auth.ERR_DUPLICATE_EMAIL, requestDto.getEmail());
        }

        // Lấy thông tin nhóm người dùng
        UserGroup userGroup = userGroupRepository.findById(requestDto.getUserGroupId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.UserGroup.ERR_NOT_FOUND_ID, requestDto.getUserGroupId()));

        // Tạo mới đối tượng User
        User user = userMapper.toUser(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setUserGroup(userGroup);

        userRepository.save(user);

        logService.createLog(TAG, EventConstants.ADD, "Tạo người dùng mới: " + user.getUsername(), userId);

        String message = messageSource.getMessage(SuccessMessage.CREATE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message, new UserResponseDto(user));
    }

    @Override
    public CommonResponseDto update(String id, UserRequestDto requestDto, String userId) {
        User user = getEntity(id);

        // Kiểm tra trùng lặp username hoặc email nếu có thay đổi
        if (!Objects.equals(user.getUsername(), requestDto.getUsername()) && userRepository.existsByUsername(requestDto.getUsername())) {
            throw new ConflictException(ErrorMessage.Auth.ERR_DUPLICATE_USERNAME, requestDto.getUsername());
        }
        if (!Objects.equals(user.getEmail(), requestDto.getEmail()) && userRepository.existsByEmail(requestDto.getEmail())) {
            throw new ConflictException(ErrorMessage.Auth.ERR_DUPLICATE_EMAIL, requestDto.getEmail());
        }

        //Nếu mật khẩu khác null thì cập nhật lại mật khẩu
        String password = requestDto.getPassword();
        if (password != null && !password.isEmpty()) {
            validatePassword(password);
            user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        }

        // Cập nhật nhóm người dùng nếu có
        if (!Objects.equals(user.getUserGroup().getId(), requestDto.getUserGroupId())) {
            UserGroup userGroup = userGroupRepository.findById(requestDto.getUserGroupId())
                    .orElseThrow(() -> new NotFoundException(ErrorMessage.UserGroup.ERR_NOT_FOUND_ID, requestDto.getUserGroupId()));
            user.setUserGroup(userGroup);
        }

        // Cập nhật thông tin user
        user.setUsername(requestDto.getUsername());
        user.setExpiryDate(requestDto.getExpiryDate());
        user.setStatus(requestDto.getStatus());
        user.setFullName(requestDto.getFullName());
        user.setPosition(requestDto.getPosition());
        user.setEmail(requestDto.getEmail());
        user.setPhoneNumber(requestDto.getPhoneNumber());
        user.setAddress(requestDto.getAddress());
        user.setNote(requestDto.getNote());
        if (requestDto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        }

        // Lưu thay đổi
        userRepository.save(user);

        logService.createLog(TAG, EventConstants.EDIT, "Cập nhật người dùng id: " + user.getId() + ", tên mới: " + user.getUsername(), userId);

        String message = messageSource.getMessage(SuccessMessage.UPDATE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message, new UserResponseDto(user));
    }

    @Override
    public CommonResponseDto delete(String id, String userId) {
        User user = getEntity(id);
        User currentUser = getEntity(userId);

        if (id.equals(userId)) {
            throw new ForbiddenException(ErrorMessage.ERR_FORBIDDEN_UPDATE_DELETE);
        }

        if (user.getUserGroup().getId().equals(currentUser.getUserGroup().getId())) {
            throw new ForbiddenException(ErrorMessage.ERR_FORBIDDEN_UPDATE_DELETE);
        }

        // Xóa người dùng
        userRepository.delete(user);

        logService.createLog(TAG, EventConstants.DELETE, "Xóa người dùng: " + user.getUsername(), userId);

        String message = messageSource.getMessage(SuccessMessage.DELETE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message);
    }

    @Override
    public PaginationResponseDto<UserResponseDto> findAll(PaginationFullRequestDto requestDto) {
        Pageable pageable = PaginationUtil.buildPageable(requestDto, SortByDataConstant.USER);

        Page<User> page = userRepository.findAll(
                EntitySpecification.filterUsers(requestDto.getKeyword(), requestDto.getSearchBy()),
                pageable);

        List<UserResponseDto> items = page.getContent().stream()
                .map(UserResponseDto::new)
                .collect(Collectors.toList());

        PagingMeta pagingMeta = PaginationUtil.buildPagingMeta(requestDto, SortByDataConstant.USER, page);

        PaginationResponseDto<UserResponseDto> responseDto = new PaginationResponseDto<>();
        responseDto.setItems(items);
        responseDto.setMeta(pagingMeta);

        return responseDto;
    }

    @Override
    public UserResponseDto findById(String id) {
        User user = getEntity(id);
        return new UserResponseDto(user);
    }

    @Override
    public List<String> uploadImages(List<MultipartFile> files, String userId) {
        if (files == null || files.isEmpty()) {
            throw new BadRequestException(ErrorMessage.INVALID_FILE_REQUIRED);
        }

        List<String> uploadedFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new BadRequestException(ErrorMessage.INVALID_FILE_TYPE);
            }

            String newUrl = uploadFileUtil.uploadFile(file);
            uploadedFiles.add(newUrl);
        }

        return uploadedFiles;
    }
}
