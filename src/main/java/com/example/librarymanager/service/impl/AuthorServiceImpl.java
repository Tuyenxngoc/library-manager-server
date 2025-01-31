package com.example.librarymanager.service.impl;

import com.example.librarymanager.constant.*;
import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.pagination.PaginationResponseDto;
import com.example.librarymanager.domain.dto.pagination.PagingMeta;
import com.example.librarymanager.domain.dto.request.AuthorRequestDto;
import com.example.librarymanager.domain.entity.Author;
import com.example.librarymanager.domain.mapper.AuthorMapper;
import com.example.librarymanager.domain.specification.EntitySpecification;
import com.example.librarymanager.exception.ConflictException;
import com.example.librarymanager.exception.NotFoundException;
import com.example.librarymanager.repository.AuthorRepository;
import com.example.librarymanager.service.AuthorService;
import com.example.librarymanager.service.LogService;
import com.example.librarymanager.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private static final String TAG = "Quản lý tác giả";

    private final LogService logService;

    private final AuthorRepository authorRepository;

    private final AuthorMapper authorMapper;

    private final MessageSource messageSource;

    @Override
    public void initAuthorsFromCsv(String authorsCsvPath) {
        if (authorRepository.count() > 0) {
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(authorsCsvPath))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");
                if (values.length < 10) continue;

                Author author = new Author();
                author.setFullName(values[0]);
                author.setCode(values[1]);
                author.setPenName(values[2].isEmpty() ? null : values[2]);
                author.setGender(Gender.valueOf(values[3]));
                author.setDateOfBirth(LocalDate.parse(values[4]));
                if (!values[5].isEmpty()) {
                    author.setDateOfDeath(LocalDate.parse(values[5]));
                }
                author.setTitle(values[6]);
                author.setResidence(values[7]);
                author.setAddress(values[8].isEmpty() ? null : values[8]);
                author.setNotes(values[9].isEmpty() ? null : values[9]);

                if (!authorRepository.existsByCode(author.getCode())) {
                    authorRepository.save(author);
                }
            }
        } catch (IOException e) {
            log.error("Error while saving author: {}", e.getMessage(), e);
        }
    }

    @Override
    public CommonResponseDto save(AuthorRequestDto requestDto, String userId) {
        Author author = authorMapper.toAuthor(requestDto);

        if (authorRepository.existsByCode(author.getCode())) {
            throw new ConflictException(ErrorMessage.Author.ERR_DUPLICATE_CODE);
        }

        author.setActiveFlag(true);
        authorRepository.save(author);

        logService.createLog(TAG, EventConstants.ADD, "Thêm tác giả mới Id: " + author.getId(), userId);

        String message = messageSource.getMessage(SuccessMessage.CREATE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message, author);
    }

    @Override
    public CommonResponseDto update(Long id, AuthorRequestDto requestDto, String userId) {
        Author author = findById(id);

        if (!Objects.equals(author.getCode(), requestDto.getCode())
                && authorRepository.existsByCode(requestDto.getCode())) {
            throw new ConflictException(ErrorMessage.Author.ERR_DUPLICATE_CODE);
        }

        author.setFullName(requestDto.getFullName());
        author.setCode(requestDto.getCode());
        author.setPenName(requestDto.getPenName());
        author.setGender(requestDto.getGender());
        author.setDateOfBirth(requestDto.getDateOfBirth());
        author.setDateOfDeath(requestDto.getDateOfDeath());
        author.setTitle(requestDto.getTitle());
        author.setResidence(requestDto.getResidence());
        author.setAddress(requestDto.getAddress());
        author.setNotes(requestDto.getNotes());

        authorRepository.save(author);

        logService.createLog(TAG, EventConstants.EDIT, "Cập nhật tác giả Id: " + author.getId(), userId);

        String message = messageSource.getMessage(SuccessMessage.UPDATE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message, author);
    }

    @Override
    public CommonResponseDto delete(Long id, String userId) {
        Author author = findById(id);

        if (!author.getBookAuthors().isEmpty()) {
            throw new ConflictException(ErrorMessage.Author.ERR_HAS_LINKED_BOOKS);
        }

        authorRepository.delete(author);

        logService.createLog(TAG, EventConstants.DELETE, "Xóa tác giả Id: " + author.getId(), userId);

        String message = messageSource.getMessage(SuccessMessage.DELETE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message);
    }

    @Override
    public PaginationResponseDto<Author> findAll(PaginationFullRequestDto requestDto) {
        Pageable pageable = PaginationUtil.buildPageable(requestDto, SortByDataConstant.AUTHOR);

        Page<Author> page = authorRepository.findAll(
                EntitySpecification.filterAuthors(requestDto.getKeyword(), requestDto.getSearchBy(), requestDto.getActiveFlag()),
                pageable);

        PagingMeta pagingMeta = PaginationUtil.buildPagingMeta(requestDto, SortByDataConstant.AUTHOR, page);

        PaginationResponseDto<Author> responseDto = new PaginationResponseDto<>();
        responseDto.setItems(page.getContent());
        responseDto.setMeta(pagingMeta);

        return responseDto;
    }

    @Override
    public Author findById(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Author.ERR_NOT_FOUND_ID));
    }

    @Override
    public CommonResponseDto toggleActiveStatus(Long id, String userId) {
        Author author = findById(id);

        author.setActiveFlag(!author.getActiveFlag());

        authorRepository.save(author);

        logService.createLog(TAG, EventConstants.EDIT, "Thay đổi trạng thái tác giả Id: " + author.getId() + ", trạng thái: " + author.getActiveFlag(), userId);

        String message = messageSource.getMessage(SuccessMessage.UPDATE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message, author.getActiveFlag());
    }

}