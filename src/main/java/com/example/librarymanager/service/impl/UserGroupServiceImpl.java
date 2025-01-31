package com.example.librarymanager.service.impl;

import com.example.librarymanager.constant.*;
import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.pagination.PaginationResponseDto;
import com.example.librarymanager.domain.dto.pagination.PagingMeta;
import com.example.librarymanager.domain.dto.request.UserGroupRequestDto;
import com.example.librarymanager.domain.dto.response.UserGroupResponseDto;
import com.example.librarymanager.domain.entity.Role;
import com.example.librarymanager.domain.entity.UserGroup;
import com.example.librarymanager.domain.entity.UserGroupRole;
import com.example.librarymanager.domain.mapper.UserGroupMapper;
import com.example.librarymanager.domain.specification.EntitySpecification;
import com.example.librarymanager.exception.BadRequestException;
import com.example.librarymanager.exception.ConflictException;
import com.example.librarymanager.exception.NotFoundException;
import com.example.librarymanager.repository.RoleRepository;
import com.example.librarymanager.repository.UserGroupRepository;
import com.example.librarymanager.repository.UserGroupRoleRepository;
import com.example.librarymanager.service.LogService;
import com.example.librarymanager.service.UserGroupService;
import com.example.librarymanager.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserGroupServiceImpl implements UserGroupService {

    private static final String TAG = "Quản lý nhóm người dùng";

    private final UserGroupRepository userGroupRepository;

    private final RoleRepository roleRepository;

    private final UserGroupRoleRepository userGroupRoleRepository;

    private final MessageSource messageSource;

    private final UserGroupMapper userGroupMapper;

    private final LogService logService;

    private UserGroup getEntity(Long id) {
        return userGroupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.UserGroup.ERR_NOT_FOUND_ID, id));
    }

    @Override
    public UserGroup initUserGroup() {
        if (userGroupRepository.count() == 0) {
            UserGroup userGroup = new UserGroup();
            userGroup.setName("Quản trị viên");
            userGroup.setCode("ADMIN");
            userGroup.getUserGroupRoles().addAll(
                    roleRepository.findAll().stream()
                            .filter(role -> !role.getCode().equals(RoleConstant.ROLE_READER))
                            .map(role -> new UserGroupRole(role, userGroup))
                            .collect(Collectors.toSet())
            );

            userGroupRepository.save(userGroup);
            log.info("Initializing user groups: Admin");

            return userGroup;
        }
        return null;
    }

    @Override
    public CommonResponseDto save(UserGroupRequestDto requestDto, String userId) {
        if (userGroupRepository.existsByCode(requestDto.getCode())) {
            throw new ConflictException(ErrorMessage.UserGroup.ERR_DUPLICATE_CODE, requestDto.getCode());
        }

        UserGroup userGroup = userGroupMapper.toUserGroup(requestDto);

        for (byte roleId : requestDto.getRoleIds()) {
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new NotFoundException(ErrorMessage.Role.ERR_NOT_FOUND_ID, roleId));

            UserGroupRole userGroupRole = new UserGroupRole(role, userGroup);
            userGroup.getUserGroupRoles().add(userGroupRole);
        }

        userGroup.setActiveFlag(true);
        userGroupRepository.save(userGroup);

        logService.createLog(TAG, EventConstants.ADD, "Tạo nhóm người dùng mới: " + userGroup.getName(), userId);

        String message = messageSource.getMessage(SuccessMessage.CREATE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message, new UserGroupResponseDto(userGroup));
    }

    @Override
    public CommonResponseDto update(Long id, UserGroupRequestDto requestDto, String userId) {
        UserGroup userGroup = getEntity(id);

        // Check for duplicate code
        if (!Objects.equals(userGroup.getCode(), requestDto.getCode()) && userGroupRepository.existsByCode(requestDto.getCode())) {
            throw new ConflictException(ErrorMessage.UserGroup.ERR_DUPLICATE_CODE, requestDto.getCode());
        }

        // Get current roles as a Set of Byte
        Set<Byte> currentRoles = userGroup.getUserGroupRoles().stream()
                .map(userGroupRole -> userGroupRole.getRole().getId())
                .collect(Collectors.toSet());

        // Prepare new role set
        Set<Byte> newRoles = requestDto.getRoleIds();

        // Determine roles to add and remove
        Set<Byte> rolesToAdd = new HashSet<>(newRoles);
        rolesToAdd.removeAll(currentRoles); // Roles that need to be added

        Set<Byte> rolesToRemove = new HashSet<>(currentRoles);
        rolesToRemove.removeAll(newRoles); // Roles that need to be removed

        // If there are roles to add or remove, proceed with updating
        if (!rolesToAdd.isEmpty() || !rolesToRemove.isEmpty()) {
            // Batch fetch roles for the new roles to add
            Map<Byte, Role> roleMap = roleRepository.findAllById(rolesToAdd).stream()
                    .collect(Collectors.toMap(Role::getId, Function.identity()));

            // Add new roles
            for (byte roleId : rolesToAdd) {
                Role role = roleMap.get(roleId);
                if (role != null) {
                    userGroup.getUserGroupRoles().add(new UserGroupRole(role, userGroup));
                }
            }

            // Remove old roles
            userGroup.getUserGroupRoles().removeIf(userGroupRole -> rolesToRemove.contains(userGroupRole.getRole().getId()));
        }

        userGroup.setCode(requestDto.getCode());
        userGroup.setName(requestDto.getName());
        userGroup.setNotes(requestDto.getNotes());
        userGroupRepository.save(userGroup);

        logService.createLog(TAG, EventConstants.EDIT, "Cập nhật nhóm người dùng id: " + userGroup.getId() + ", tên mới: " + userGroup.getName(), userId);

        String message = messageSource.getMessage(SuccessMessage.UPDATE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message, new UserGroupResponseDto(userGroup));
    }

    @Override
    public CommonResponseDto delete(Long id, String userId) {
        UserGroup userGroup = getEntity(id);

        // Nếu nhóm người dùng có người dùng liên kết, không cho phép xóa
        if (!userGroup.getUsers().isEmpty()) {
            throw new BadRequestException(ErrorMessage.UserGroup.ERR_HAS_LINKED_USERS);
        }

        userGroupRepository.delete(userGroup);

        logService.createLog(TAG, EventConstants.DELETE, "Xóa nhóm người dùng: " + userGroup.getName(), userId);

        String message = messageSource.getMessage(SuccessMessage.DELETE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message);
    }

    @Override
    public PaginationResponseDto<UserGroupResponseDto> findAll(PaginationFullRequestDto requestDto) {
        Pageable pageable = PaginationUtil.buildPageable(requestDto, SortByDataConstant.USER_GROUP);

        Page<UserGroup> page = userGroupRepository.findAll(
                EntitySpecification.filterUserGroups(requestDto.getKeyword(), requestDto.getSearchBy(), requestDto.getActiveFlag()),
                pageable);

        List<UserGroupResponseDto> items = page.getContent().stream()
                .map(UserGroupResponseDto::new)
                .toList();

        PagingMeta pagingMeta = PaginationUtil.buildPagingMeta(requestDto, SortByDataConstant.USER_GROUP, page);

        PaginationResponseDto<UserGroupResponseDto> responseDto = new PaginationResponseDto<>();
        responseDto.setItems(items);
        responseDto.setMeta(pagingMeta);

        return responseDto;
    }

    @Override
    public UserGroupResponseDto findById(Long id) {
        UserGroup userGroup = getEntity(id);
        return new UserGroupResponseDto(userGroup);
    }

    @Override
    public CommonResponseDto toggleActiveStatus(Long id, String userId) {
        UserGroup userGroup = getEntity(id);

        userGroup.setActiveFlag(!userGroup.getActiveFlag());
        userGroupRepository.save(userGroup);

        logService.createLog(TAG, EventConstants.EDIT, "Thay đổi trạng thái nhóm người dùng: " + userGroup.getName(), userId);

        String message = messageSource.getMessage(SuccessMessage.UPDATE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message, userGroup.getActiveFlag());
    }

}
