package com.example.librarymanager.service.impl;

import com.example.librarymanager.constant.ErrorMessage;
import com.example.librarymanager.constant.EventConstants;
import com.example.librarymanager.constant.SortByDataConstant;
import com.example.librarymanager.constant.SuccessMessage;
import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.pagination.PaginationResponseDto;
import com.example.librarymanager.domain.dto.pagination.PagingMeta;
import com.example.librarymanager.domain.dto.request.BookSetRequestDto;
import com.example.librarymanager.domain.dto.response.bookset.BookSetResponseDto;
import com.example.librarymanager.domain.entity.BookSet;
import com.example.librarymanager.domain.mapper.BookSetMapper;
import com.example.librarymanager.domain.specification.EntitySpecification;
import com.example.librarymanager.exception.BadRequestException;
import com.example.librarymanager.exception.ConflictException;
import com.example.librarymanager.exception.NotFoundException;
import com.example.librarymanager.repository.BookSetRepository;
import com.example.librarymanager.service.BookSetService;
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
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookSetServiceImpl implements BookSetService {

    private static final String TAG = "Quản lý bộ sách";

    private final BookSetRepository bookSetRepository;

    private final MessageSource messageSource;

    private final BookSetMapper bookSetMapper;

    private final LogService logService;

    @Override
    public void initBookSetsFromCSv(String bookSetsCsvPath) {
        if (bookSetRepository.count() > 0) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(bookSetsCsvPath))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");
                if (values.length < 1) continue;

                BookSet bookSet = new BookSet();
                bookSet.setName(values[0]);

                if (!bookSetRepository.existsByName(bookSet.getName())) {
                    bookSetRepository.save(bookSet);
                }
            }
        } catch (IOException e) {
            log.error("Error while initializing book sets from CSV: {}", e.getMessage(), e);
        }
    }

    @Override
    public CommonResponseDto save(BookSetRequestDto requestDto, String userId) {
        if (bookSetRepository.existsByName(requestDto.getName())) {
            throw new ConflictException(ErrorMessage.BookSet.ERR_DUPLICATE_NAME);
        }

        BookSet bookSet = bookSetMapper.toBookSet(requestDto);

        bookSet.setActiveFlag(true);
        bookSetRepository.save(bookSet);

        logService.createLog(TAG, EventConstants.ADD, "Tạo bộ sách mới: " + bookSet.getName(), userId);

        String message = messageSource.getMessage(SuccessMessage.CREATE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message, new BookSetResponseDto(bookSet));
    }

    @Override
    public CommonResponseDto update(Long id, BookSetRequestDto requestDto, String userId) {
        BookSet bookSet = findById(id);

        if (!Objects.equals(bookSet.getName(), requestDto.getName()) && bookSetRepository.existsByName(requestDto.getName())) {
            throw new ConflictException(ErrorMessage.BookSet.ERR_DUPLICATE_NAME);
        }

        bookSet.setName(requestDto.getName());

        bookSetRepository.save(bookSet);

        logService.createLog(TAG, EventConstants.EDIT, "Cập nhật bộ sách id: " + bookSet.getId() + ", tên mới: " + bookSet.getName(), userId);

        String message = messageSource.getMessage(SuccessMessage.UPDATE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message, new BookSetResponseDto(bookSet));
    }

    @Override
    public CommonResponseDto delete(Long id, String userId) {
        BookSet bookSet = findById(id);

        if (!bookSet.getBookDefinitions().isEmpty()) {
            throw new BadRequestException(ErrorMessage.BookSet.ERR_HAS_LINKED_BOOKS);
        }

        bookSetRepository.delete(bookSet);

        logService.createLog(TAG, EventConstants.DELETE, "Xóa bộ sách: " + bookSet.getName(), userId);

        String message = messageSource.getMessage(SuccessMessage.DELETE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message);
    }

    @Override
    public PaginationResponseDto<BookSetResponseDto> findAll(PaginationFullRequestDto requestDto) {
        Pageable pageable = PaginationUtil.buildPageable(requestDto, SortByDataConstant.BOOK_SET);

        Page<BookSet> page = bookSetRepository.findAll(
                EntitySpecification.filterBookSets(requestDto.getKeyword(), requestDto.getSearchBy(), requestDto.getActiveFlag()),
                pageable);

        List<BookSetResponseDto> items = page.getContent().stream()
                .map(BookSetResponseDto::new)
                .toList();

        PagingMeta pagingMeta = PaginationUtil.buildPagingMeta(requestDto, SortByDataConstant.BOOK_SET, page);

        PaginationResponseDto<BookSetResponseDto> responseDto = new PaginationResponseDto<>();
        responseDto.setItems(items);
        responseDto.setMeta(pagingMeta);

        return responseDto;
    }

    @Override
    public BookSet findById(Long id) {
        return bookSetRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.BookSet.ERR_NOT_FOUND_ID, id));
    }

    @Override
    public CommonResponseDto toggleActiveStatus(Long id, String userId) {
        BookSet bookSet = findById(id);

        bookSet.setActiveFlag(!bookSet.getActiveFlag());

        bookSetRepository.save(bookSet);

        logService.createLog(TAG, EventConstants.EDIT, "Thay đổi trạng thái bộ sách: " + bookSet.getName(), userId);

        String message = messageSource.getMessage(SuccessMessage.UPDATE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message, bookSet.getActiveFlag());
    }
}
