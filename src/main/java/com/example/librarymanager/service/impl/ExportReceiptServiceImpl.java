package com.example.librarymanager.service.impl;

import com.example.librarymanager.constant.*;
import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.pagination.PaginationResponseDto;
import com.example.librarymanager.domain.dto.pagination.PagingMeta;
import com.example.librarymanager.domain.dto.request.ExportReceiptRequestDto;
import com.example.librarymanager.domain.dto.response.ExportReceiptResponseDto;
import com.example.librarymanager.domain.entity.Book;
import com.example.librarymanager.domain.entity.ExportReceipt;
import com.example.librarymanager.domain.mapper.ExportReceiptMapper;
import com.example.librarymanager.domain.specification.EntitySpecification;
import com.example.librarymanager.exception.BadRequestException;
import com.example.librarymanager.exception.ConflictException;
import com.example.librarymanager.exception.NotFoundException;
import com.example.librarymanager.repository.BookRepository;
import com.example.librarymanager.repository.ExportReceiptRepository;
import com.example.librarymanager.service.ExportReceiptService;
import com.example.librarymanager.service.LogService;
import com.example.librarymanager.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExportReceiptServiceImpl implements ExportReceiptService {

    private static final String TAG = "Xuất sách";

    private final ExportReceiptRepository exportReceiptRepository;

    private final ExportReceiptMapper exportReceiptMapper;

    private final MessageSource messageSource;

    private final LogService logService;

    private final BookRepository bookRepository;

    private ExportReceipt getEntity(Long id) {
        return exportReceiptRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.ImportReceipt.ERR_NOT_FOUND_ID, id));
    }

    private void getBook(ExportReceipt exportReceipt, Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Book.ERR_NOT_FOUND_ID, bookId));
        if (book.getExportReceipt() != null) {
            throw new ConflictException(ErrorMessage.Book.ERR_HAS_LINKED_EXPORT_RECEPTION, book.getBookCode());
        }
        if (book.getBookCondition().equals(BookCondition.ON_LOAN)) {
            throw new BadRequestException(ErrorMessage.Book.ERR_BOOK_ALREADY_ON_LOAN, book.getBookCode());
        }

        book.setExportReceipt(exportReceipt);
        exportReceipt.getBooks().add(book);
    }

    @Override
    public String generateReceiptNumber() {
        long currentCount = exportReceiptRepository.count();
        long nextNumber = currentCount + 1;
        return String.format("PX%05d", nextNumber);
    }

    @Override
    public CommonResponseDto save(ExportReceiptRequestDto requestDto, String userId) {
        // Kiểm tra trùng số phiếu
        if (exportReceiptRepository.existsByReceiptNumber(requestDto.getReceiptNumber())) {
            throw new ConflictException(ErrorMessage.ExportReceipt.ERR_DUPLICATE_NUMBER, requestDto.getReceiptNumber());
        }
        ExportReceipt exportReceipt = exportReceiptMapper.toExportReceipt(requestDto);

        // Thêm sách mới vào phiếu xuất
        for (Long bookId : requestDto.getBookIds()) {
            getBook(exportReceipt, bookId);
        }

        exportReceiptRepository.save(exportReceipt);

        logService.createLog(TAG, EventConstants.ADD, "Tạo phiếu xuất mới: " + exportReceipt.getReceiptNumber(), userId);

        String message = messageSource.getMessage(SuccessMessage.CREATE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message);
    }

    @Override
    public CommonResponseDto update(Long id, ExportReceiptRequestDto requestDto, String userId) {
        ExportReceipt exportReceipt = getEntity(id);

        // Kiểm tra trùng số phiếu
        if (!Objects.equals(exportReceipt.getReceiptNumber(), requestDto.getReceiptNumber()) &&
                exportReceiptRepository.existsByReceiptNumber(requestDto.getReceiptNumber())) {
            throw new ConflictException(ErrorMessage.ExportReceipt.ERR_DUPLICATE_NUMBER, requestDto.getReceiptNumber());
        }

        // Cập nhật thông tin phiếu xuất
        exportReceipt.setReceiptNumber(requestDto.getReceiptNumber());
        exportReceipt.setExportDate(requestDto.getExportDate());
        exportReceipt.setExportReason(requestDto.getExportReason());

        // Kiểm tra và cập nhật danh sách sách
        Set<Long> newBookIds = requestDto.getBookIds();
        Set<Book> currentBooks = exportReceipt.getBooks();

        // Lấy danh sách ID sách hiện tại trong phiếu xuất
        Set<Long> currentBookIds = currentBooks.stream()
                .map(Book::getId)
                .collect(Collectors.toSet());

        // Tìm sách cần thêm mới
        Set<Long> bookIdsToAdd = new HashSet<>(newBookIds);
        bookIdsToAdd.removeAll(currentBookIds);

        // Tìm sách cần xóa
        Set<Long> bookIdsToRemove = new HashSet<>(currentBookIds);
        bookIdsToRemove.removeAll(newBookIds);

        // Thêm sách mới vào phiếu xuất
        if (!bookIdsToAdd.isEmpty()) {
            for (Long bookId : bookIdsToAdd) {
                getBook(exportReceipt, bookId);
            }
        }

        // Xóa sách không còn trong danh sách
        if (!bookIdsToRemove.isEmpty()) {
            Set<Book> booksToRemove = currentBooks.stream()
                    .filter(book -> bookIdsToRemove.contains(book.getId()))
                    .collect(Collectors.toSet());

            for (Book book : booksToRemove) {
                book.setExportReceipt(null);
            }

            bookRepository.saveAll(booksToRemove);

            exportReceipt.getBooks().removeAll(booksToRemove);
        }

        exportReceiptRepository.save(exportReceipt);

        logService.createLog(TAG, EventConstants.EDIT, "Sửa phiếu xuất: " + exportReceipt.getReceiptNumber(), userId);

        String message = messageSource.getMessage(SuccessMessage.UPDATE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message);
    }

    @Override
    public CommonResponseDto delete(Long id, String userId) {
        ExportReceipt exportReceipt = getEntity(id);

        // Xóa sách liên kết với phiếu xuất
        exportReceipt.getBooks().forEach(book -> {
            book.setExportReceipt(null);
            bookRepository.save(book);
        });
        exportReceipt.getBooks().clear();

        exportReceiptRepository.delete(exportReceipt);

        logService.createLog(TAG, EventConstants.DELETE, "Xóa phiếu xuất: " + exportReceipt.getReceiptNumber(), userId);

        String message = messageSource.getMessage(SuccessMessage.DELETE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message);
    }

    @Override
    public PaginationResponseDto<ExportReceiptResponseDto> findAll(PaginationFullRequestDto requestDto) {
        Pageable pageable = PaginationUtil.buildPageable(requestDto, SortByDataConstant.EXPORT_RECEIPT);

        Page<ExportReceipt> page = exportReceiptRepository.findAll(
                EntitySpecification.filterExportReceipts(requestDto.getKeyword(), requestDto.getSearchBy()),
                pageable);

        List<ExportReceiptResponseDto> items = page.getContent().stream()
                .map(ExportReceiptResponseDto::new)
                .toList();

        PagingMeta pagingMeta = PaginationUtil.buildPagingMeta(requestDto, SortByDataConstant.EXPORT_RECEIPT, page);

        PaginationResponseDto<ExportReceiptResponseDto> responseDto = new PaginationResponseDto<>();
        responseDto.setItems(items);
        responseDto.setMeta(pagingMeta);

        return responseDto;
    }

    @Override
    public ExportReceiptResponseDto findById(Long id) {
        ExportReceipt exportReceipt = getEntity(id);
        ExportReceiptResponseDto responseDto = new ExportReceiptResponseDto(exportReceipt);

        for (Book book : exportReceipt.getBooks()) {
            responseDto.getBookIds().add(book.getId());
        }

        return responseDto;
    }
}