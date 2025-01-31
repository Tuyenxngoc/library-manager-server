package com.example.librarymanager.service.impl;

import com.example.librarymanager.constant.*;
import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.filter.TimeFilter;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.pagination.PaginationResponseDto;
import com.example.librarymanager.domain.dto.pagination.PagingMeta;
import com.example.librarymanager.domain.dto.request.BookReturnRequestDto;
import com.example.librarymanager.domain.dto.response.bookborrow.BookBorrowResponseDto;
import com.example.librarymanager.domain.entity.Book;
import com.example.librarymanager.domain.entity.BookBorrow;
import com.example.librarymanager.domain.entity.BorrowReceipt;
import com.example.librarymanager.domain.specification.EntitySpecification;
import com.example.librarymanager.exception.BadRequestException;
import com.example.librarymanager.exception.NotFoundException;
import com.example.librarymanager.repository.BookBorrowRepository;
import com.example.librarymanager.repository.BookRepository;
import com.example.librarymanager.repository.BorrowReceiptRepository;
import com.example.librarymanager.service.BookBorrowService;
import com.example.librarymanager.service.BorrowReceiptService;
import com.example.librarymanager.service.LogService;
import com.example.librarymanager.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookBorrowServiceImpl implements BookBorrowService {

    private static final String TAG = "Quản lý phiếu mượn";

    private final MessageSource messageSource;

    private final LogService logService;

    private final BookRepository bookRepository;

    private final BookBorrowRepository bookBorrowRepository;

    private final BorrowReceiptRepository borrowReceiptRepository;

    private final BorrowReceiptService borrowReceiptService;

    private BookBorrow getEntity(Long id) {
        return bookBorrowRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.BookBorrow.ERR_NOT_FOUND_ID, id));
    }

    @Override
    public PaginationResponseDto<BookBorrowResponseDto> findAll(PaginationFullRequestDto requestDto, TimeFilter timeFilter, List<BookBorrowStatus> status) {
        Pageable pageable = PaginationUtil.buildPageable(requestDto, SortByDataConstant.BOOK_BORROW);

        Specification<BookBorrow> spec =
                EntitySpecification.filterBookBorrows(status)
                        .and(EntitySpecification.filterBookBorrows(timeFilter))
                        .and(EntitySpecification.filterBookBorrows(requestDto.getKeyword(), requestDto.getSearchBy()));
        Page<BookBorrow> page = bookBorrowRepository.findAll(spec, pageable);

        List<BookBorrowResponseDto> items = page.getContent().stream()
                .map(BookBorrowResponseDto::new)
                .toList();

        PagingMeta pagingMeta = PaginationUtil.buildPagingMeta(requestDto, SortByDataConstant.BOOK_BORROW, page);

        PaginationResponseDto<BookBorrowResponseDto> responseDto = new PaginationResponseDto<>();
        responseDto.setItems(items);
        responseDto.setMeta(pagingMeta);

        return responseDto;
    }

    @Override
    @Transactional
    public CommonResponseDto returnBooks(List<BookReturnRequestDto> requestDtos, String userId) {
        boolean isReturned = false;
        StringBuilder logMessage = new StringBuilder();
        for (BookReturnRequestDto requestDto : requestDtos) {
            BookBorrow bookBorrow = getEntity(requestDto.getBookBorrowId());
            if (!bookBorrow.getStatus().equals(BookBorrowStatus.NOT_RETURNED)) {
                continue;
            }

            bookBorrow.setReturnDate(LocalDate.now());
            bookBorrow.setStatus(BookBorrowStatus.RETURNED);

            Book book = bookBorrow.getBook();
            book.setBookCondition(BookCondition.AVAILABLE);
            if (requestDto.getBookStatus() != null) {
                book.setBookStatus(requestDto.getBookStatus());
            }

            BorrowReceipt borrowReceipt = bookBorrow.getBorrowReceipt();
            borrowReceiptService.updateBorrowStatus(borrowReceipt);

            logMessage.append(book.getBookCode()).append(", ");

            bookRepository.save(book);
            bookBorrowRepository.save(bookBorrow);
            borrowReceiptRepository.save(borrowReceipt);

            isReturned = true;
        }

        if (!isReturned) {
            throw new BadRequestException(ErrorMessage.BookBorrow.ERR_NOT_FOUND_IDS);
        }

        if (logMessage.length() > 2) {
            logMessage.setLength(logMessage.length() - 2);
        }

        logService.createLog(TAG, EventConstants.EDIT, "Trả sách mã: " + logMessage, userId);

        String message = messageSource.getMessage(SuccessMessage.UPDATE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message);
    }

    @Override
    @Transactional
    public CommonResponseDto reportLostBooksByIds(Set<Long> ids, String userId) {
        boolean isLostReported = false;
        StringBuilder logMessage = new StringBuilder();
        for (Long id : ids) {
            BookBorrow bookBorrow = getEntity(id);
            if (!bookBorrow.getStatus().equals(BookBorrowStatus.NOT_RETURNED)) {
                continue;
            }

            bookBorrow.setStatus(BookBorrowStatus.LOST);

            Book book = bookBorrow.getBook();
            book.setBookCondition(BookCondition.LOST);

            BorrowReceipt borrowReceipt = bookBorrow.getBorrowReceipt();
            borrowReceiptService.updateBorrowStatus(borrowReceipt);

            logMessage.append(book.getBookCode()).append(", ");

            bookRepository.save(book);
            bookBorrowRepository.save(bookBorrow);
            borrowReceiptRepository.save(borrowReceipt);

            isLostReported = true;
        }

        if (!isLostReported) {
            throw new BadRequestException(ErrorMessage.BookBorrow.ERR_NOT_FOUND_IDS);
        }

        if (logMessage.length() > 2) {
            logMessage.setLength(logMessage.length() - 2);
        }

        logService.createLog(TAG, EventConstants.EDIT, "Báo mất sách với mã: " + logMessage, userId);

        String message = messageSource.getMessage(SuccessMessage.UPDATE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message);
    }
}
