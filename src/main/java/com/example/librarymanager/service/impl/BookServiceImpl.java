package com.example.librarymanager.service.impl;

import com.example.librarymanager.constant.*;
import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.pagination.PaginationResponseDto;
import com.example.librarymanager.domain.dto.pagination.PagingMeta;
import com.example.librarymanager.domain.dto.response.book.BookResponseDto;
import com.example.librarymanager.domain.entity.Book;
import com.example.librarymanager.domain.specification.EntitySpecification;
import com.example.librarymanager.exception.NotFoundException;
import com.example.librarymanager.repository.BookRepository;
import com.example.librarymanager.service.BookService;
import com.example.librarymanager.service.LogService;
import com.example.librarymanager.service.PdfService;
import com.example.librarymanager.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private static final String TAG = "Quản lý sách";

    private final LogService logService;

    private final MessageSource messageSource;

    private final BookRepository bookRepository;

    private final PdfService pdfService;

    private Book getEntity(long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Book.ERR_NOT_FOUND_ID, id));
    }

    @Override
    public CommonResponseDto updateStatus(Long id, BookStatus status, String userId) {
        Book book = getEntity(id);
        book.setBookStatus(status);
        bookRepository.save(book);

        logService.createLog(TAG, EventConstants.EDIT, "Cập nhật trạng thái sách mã: " + book.getBookCode(), userId);

        String message = messageSource.getMessage(SuccessMessage.UPDATE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message, new BookResponseDto(book));
    }

    @Override
    public PaginationResponseDto<BookResponseDto> findAll(PaginationFullRequestDto requestDto, BookCondition bookCondition) {
        Pageable pageable = PaginationUtil.buildPageable(requestDto, SortByDataConstant.BOOK);

        Page<Book> page = bookRepository.findAll(
                EntitySpecification.filterBooks(requestDto.getKeyword(), requestDto.getSearchBy(), bookCondition),
                pageable);

        List<BookResponseDto> items = page.getContent().stream()
                .map(BookResponseDto::new)
                .toList();

        PagingMeta pagingMeta = PaginationUtil.buildPagingMeta(requestDto, SortByDataConstant.BOOK, page);

        PaginationResponseDto<BookResponseDto> responseDto = new PaginationResponseDto<>();
        responseDto.setItems(items);
        responseDto.setMeta(pagingMeta);

        return responseDto;
    }

    @Override
    public List<BookResponseDto> findByIds(Set<Long> ids) {
        return bookRepository.findBooksByIds(ids);
    }

    @Override
    public List<BookResponseDto> findByCodes(Set<String> codes) {
        return bookRepository.findBooksByCodes(codes);
    }

    @Override
    public BookResponseDto findById(Long id) {
        return new BookResponseDto(getEntity(id));
    }

    @Override
    public byte[] getBooksPdfContent(Set<Long> ids) {
        List<Book> books = bookRepository.findAllById(ids);
        return pdfService.createPdfFromBooks(books);
    }

    @Override
    public byte[] getBooksLabelType1PdfContent(Set<Long> ids) {
        List<Book> books = bookRepository.findAllById(ids);
        return pdfService.createLabelType1Pdf(books);
    }

    @Override
    public byte[] getBooksLabelType2PdfContent(Set<Long> ids) {
        List<Book> books = bookRepository.findAllById(ids);
        return pdfService.createLabelType2Pdf(books);
    }

    @Override
    public byte[] generateBookListPdf() {
        List<Book> books = bookRepository.findAll();
        return pdfService.createBookListPdf(books);
    }
}
