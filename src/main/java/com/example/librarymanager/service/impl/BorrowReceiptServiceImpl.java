package com.example.librarymanager.service.impl;

import com.example.librarymanager.constant.*;
import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.pagination.PaginationResponseDto;
import com.example.librarymanager.domain.dto.pagination.PagingMeta;
import com.example.librarymanager.domain.dto.request.BorrowReceiptRequestDto;
import com.example.librarymanager.domain.dto.request.CreateBorrowReceiptRequestDto;
import com.example.librarymanager.domain.dto.response.borrowreceipt.BorrowReceiptDetailResponseDto;
import com.example.librarymanager.domain.dto.response.borrowreceipt.BorrowReceiptDetailsDto;
import com.example.librarymanager.domain.dto.response.borrowreceipt.BorrowReceiptForReaderResponseDto;
import com.example.librarymanager.domain.dto.response.borrowreceipt.BorrowReceiptResponseDto;
import com.example.librarymanager.domain.entity.*;
import com.example.librarymanager.domain.mapper.BorrowReceiptMapper;
import com.example.librarymanager.domain.specification.EntitySpecification;
import com.example.librarymanager.exception.BadRequestException;
import com.example.librarymanager.exception.ConflictException;
import com.example.librarymanager.exception.ForbiddenException;
import com.example.librarymanager.exception.NotFoundException;
import com.example.librarymanager.repository.*;
import com.example.librarymanager.service.BorrowReceiptService;
import com.example.librarymanager.service.ExcelExportService;
import com.example.librarymanager.service.LogService;
import com.example.librarymanager.service.PdfService;
import com.example.librarymanager.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BorrowReceiptServiceImpl implements BorrowReceiptService {
    private static final String TAG = "Quản lý phiếu mượn";

    private final LogService logService;

    private final MessageSource messageSource;

    private final BorrowReceiptRepository borrowReceiptRepository;

    private final BorrowReceiptMapper borrowReceiptMapper;

    private final ReaderRepository readerRepository;

    private final BookRepository bookRepository;

    private final BookBorrowRepository bookBorrowRepository;

    private final CartRepository cartRepository;

    private final CartDetailRepository cartDetailRepository;

    private final UserRepository userRepository;

    private final PdfService pdfService;

    private final ExcelExportService excelExportService;

    private BorrowReceipt getEntity(Long id) {
        return borrowReceiptRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.BorrowReceipt.ERR_NOT_FOUND_ID, id));
    }

    private void getReader(BorrowReceipt borrowReceipt, BorrowReceiptRequestDto requestDto) {
        Reader reader = readerRepository.findById(requestDto.getReaderId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Reader.ERR_NOT_FOUND_ID, requestDto.getReaderId()));
        switch (reader.getStatus()) {
            case INACTIVE -> throw new ForbiddenException(ErrorMessage.Reader.ERR_READER_INACTIVE);
            case SUSPENDED -> throw new ForbiddenException(ErrorMessage.Reader.ERR_READER_SUSPENDED);
            case REVOKED -> throw new ForbiddenException(ErrorMessage.Reader.ERR_READER_REVOKED);
        }
        if (reader.getExpiryDate().isBefore(LocalDate.now())) {
            throw new ForbiddenException(ErrorMessage.Reader.ERR_READER_EXPIRED);
        }

        borrowReceipt.setReader(reader);
    }

    private void getBook(BorrowReceipt borrowReceipt, String bookCode) {
        Book book = bookRepository.findByBookCode(bookCode)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Book.ERR_NOT_FOUND_CODE, bookCode));

        //Kiểm tra sách đã được mượn hay chưa
        if (!book.getBookCondition().equals(BookCondition.AVAILABLE)) {
            throw new ConflictException(ErrorMessage.Book.ERR_BOOK_ALREADY_BORROWED, bookCode);
        }

        //Kiểm tra sách xem có ai khác đã đăng ký mượn từ trước hay chưa
        LocalDateTime now = LocalDateTime.now();
        for (CartDetail cartDetail : book.getCartDetails()) {
            if (cartDetail.getBorrowTo().isAfter(now)
                    && !Objects.equals(cartDetail.getCart().getReader().getId(), borrowReceipt.getReader().getId())) {
                throw new ConflictException(ErrorMessage.Book.ERR_BOOK_RESERVED_BY_ANOTHER_READER, bookCode);
            }
        }

        //Cập nhật trạng thái sách là đang mượn
        book.setBookCondition(BookCondition.ON_LOAN);
        bookRepository.save(book);

        BookBorrow bookBorrow = new BookBorrow();
        bookBorrow.setBook(book);
        bookBorrow.setBorrowReceipt(borrowReceipt);
        borrowReceipt.getBookBorrows().add(bookBorrow);
    }

    @Override
    public String generateReceiptNumber() {
        long currentCount = borrowReceiptRepository.count();
        long nextNumber = currentCount + 1;
        return String.format("PM%05d", nextNumber);
    }

    @Override
    @Transactional
    public CommonResponseDto save(BorrowReceiptRequestDto requestDto, String userId) {
        //Kiểm tra số phiếu mượn
        if (borrowReceiptRepository.existsByReceiptNumber(requestDto.getReceiptNumber())) {
            throw new ConflictException(ErrorMessage.BorrowReceipt.ERR_DUPLICATE_RECEIPT_NUMBER);
        }

        BorrowReceipt borrowReceipt = borrowReceiptMapper.toBorrowReceipt(requestDto);
        borrowReceipt.setStatus(BorrowStatus.NOT_RETURNED);

        //Lấy ra bạn đọc và kiểm tra thông tin
        getReader(borrowReceipt, requestDto);
        borrowReceipt.setBookBorrows(new ArrayList<>());

        //Lấy ra sách và kiểm tra thông tin
        for (String code : requestDto.getBooks()) {
            getBook(borrowReceipt, code);
        }

        Cart cart = borrowReceipt.getReader().getCart();
        if (cart != null) {
            LocalDateTime now = LocalDateTime.now();
            Set<Book> booksToBorrow = borrowReceipt.getBookBorrows().stream()
                    .map(BookBorrow::getBook)
                    .collect(Collectors.toSet());
            cartDetailRepository.deleteByCartIdAndBooks(cart.getId(), booksToBorrow, now);
        }

        borrowReceiptRepository.save(borrowReceipt);

        logService.createLog(TAG, EventConstants.ADD, "Tạo phiếu mượn mới mã: " + borrowReceipt.getReceiptNumber(), userId);

        String message = messageSource.getMessage(SuccessMessage.CREATE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message);
    }

    @Override
    @Transactional
    public CommonResponseDto update(Long id, BorrowReceiptRequestDto requestDto, String userId) {
        BorrowReceipt borrowReceipt = getEntity(id);

        if (!Objects.equals(borrowReceipt.getReceiptNumber(), requestDto.getReceiptNumber())
                && borrowReceiptRepository.existsByReceiptNumber(requestDto.getReceiptNumber())) {
            throw new ConflictException(ErrorMessage.BorrowReceipt.ERR_DUPLICATE_RECEIPT_NUMBER);
        }

        //Nếu bạn đọc thay đổi thì cập nhật thông tin
        if (!borrowReceipt.getReader().getId().equals(requestDto.getReaderId())) {
            //Lấy ra bạn đọc và kiểm tra thông tin
            getReader(borrowReceipt, requestDto);
        }

        // Lấy danh sách sách hiện tại trong phiếu mượn
        Set<String> currentBookCodes = borrowReceipt.getBookBorrows().stream()
                .map(bookBorrow -> bookBorrow.getBook().getBookCode())
                .collect(Collectors.toSet());

        //Lấy danh sách mới
        Set<String> newBookCodes = requestDto.getBooks();

        // Tìm sách cần thêm mới
        Set<String> bookCodesToAdd = new HashSet<>(newBookCodes);
        bookCodesToAdd.removeAll(currentBookCodes);

        // Tìm sách cần xóa
        Set<String> bookCodesToRemove = new HashSet<>(currentBookCodes);
        bookCodesToRemove.removeAll(newBookCodes);

        // Thêm sách mới vào phiếu xuất
        if (!bookCodesToAdd.isEmpty()) {
            for (String code : bookCodesToAdd) {
                getBook(borrowReceipt, code);
            }
        }

        // Xóa sách không còn trong danh sách
        if (!bookCodesToRemove.isEmpty()) {
            Set<BookBorrow> booksToRemove = borrowReceipt.getBookBorrows().stream()
                    .filter(bookBorrow -> bookCodesToRemove.contains(bookBorrow.getBook().getBookCode()))
                    .collect(Collectors.toSet());

            //Cập nhật trạng thái của sách bị xóa khỏi phiếu mượn
            for (BookBorrow bookBorrow : booksToRemove) {
                Book book = bookBorrow.getBook();
                book.setBookCondition(BookCondition.AVAILABLE);
                bookRepository.save(book);
            }

            bookBorrowRepository.deleteAll(booksToRemove);

            borrowReceipt.getBookBorrows().removeAll(booksToRemove);
        }

        borrowReceipt.setReceiptNumber(requestDto.getReceiptNumber());
        borrowReceipt.setBorrowDate(requestDto.getBorrowDate());
        borrowReceipt.setDueDate(requestDto.getDueDate());
        borrowReceipt.setNote(requestDto.getNote());
        updateBorrowStatus(borrowReceipt);

        borrowReceiptRepository.save(borrowReceipt);

        logService.createLog(TAG, EventConstants.EDIT, "Cập nhật phiếu mượn mã: " + borrowReceipt.getReceiptNumber(), userId);

        String message = messageSource.getMessage(SuccessMessage.UPDATE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message);
    }

    @Override
    public void updateBorrowStatus(BorrowReceipt borrowReceipt) {
        boolean allReturned = true;
        boolean partiallyReturned = false;

        for (BookBorrow bookBorrow : borrowReceipt.getBookBorrows()) {
            if (!BookBorrowStatus.RETURNED.equals(bookBorrow.getStatus())) {
                allReturned = false;
            } else {
                partiallyReturned = true;
            }
        }

        if (allReturned) {
            borrowReceipt.setStatus(BorrowStatus.RETURNED);
            return;
        }

        boolean overdue = borrowReceipt.getDueDate().isBefore(LocalDate.now());
        if (overdue) {
            borrowReceipt.setStatus(BorrowStatus.OVERDUE);
            return;
        }

        if (partiallyReturned) {
            borrowReceipt.setStatus(BorrowStatus.PARTIALLY_RETURNED);
            return;
        }

        borrowReceipt.setStatus(BorrowStatus.NOT_RETURNED);
    }

    @Override
    public CommonResponseDto delete(Long id, String userId) {
        BorrowReceipt borrowReceipt = getEntity(id);

        borrowReceiptRepository.delete(borrowReceipt);

        logService.createLog(TAG, EventConstants.DELETE, "Xóa phiếu mượn mã: " + borrowReceipt.getReceiptNumber(), userId);

        String message = messageSource.getMessage(SuccessMessage.DELETE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message);
    }

    @Override
    public PaginationResponseDto<BorrowReceiptResponseDto> findAll(PaginationFullRequestDto requestDto, BorrowStatus status) {
        borrowReceiptRepository.updateOverdueStatus();

        Pageable pageable = PaginationUtil.buildPageable(requestDto, SortByDataConstant.BORROW_RECEIPT);

        Page<BorrowReceipt> page = borrowReceiptRepository.findAll(
                EntitySpecification.filterBorrowReceipts(requestDto.getKeyword(), requestDto.getSearchBy(), status),
                pageable);

        List<BorrowReceiptResponseDto> items = page.getContent().stream()
                .map(BorrowReceiptResponseDto::new)
                .toList();

        PagingMeta pagingMeta = PaginationUtil.buildPagingMeta(requestDto, SortByDataConstant.BORROW_RECEIPT, page);

        PaginationResponseDto<BorrowReceiptResponseDto> responseDto = new PaginationResponseDto<>();
        responseDto.setItems(items);
        responseDto.setMeta(pagingMeta);

        return responseDto;
    }

    @Override
    public BorrowReceiptDetailResponseDto findById(Long id) {
        borrowReceiptRepository.updateOverdueStatus();
        BorrowReceipt borrowReceipt = getEntity(id);
        return new BorrowReceiptDetailResponseDto(borrowReceipt);
    }

    @Override
    public BorrowReceiptDetailResponseDto findByCartId(Long id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Cart.ERR_NOT_FOUND_ID, id));

        BorrowReceiptDetailResponseDto responseDto = new BorrowReceiptDetailResponseDto();
        responseDto.setReaderId(cart.getReader().getId());

        LocalDateTime now = LocalDateTime.now();
        for (CartDetail cartDetail : cart.getCartDetails()) {
            if (cartDetail.getBorrowTo().isAfter(now)) {
                responseDto.getBooks().add(cartDetail.getBook().getBookCode());
            }
        }

        return responseDto;
    }

    @Override
    public PaginationResponseDto<BorrowReceiptForReaderResponseDto> findByCardNumber(String cardNumber, PaginationFullRequestDto requestDto, BorrowStatus status) {
        borrowReceiptRepository.updateOverdueStatus();

        Pageable pageable = PaginationUtil.buildPageable(requestDto, SortByDataConstant.BORROW_RECEIPT);

        Specification<BorrowReceipt> spec = EntitySpecification.filterBorrowReceiptsByReader(cardNumber)
                .and(EntitySpecification.filterBorrowReceipts(requestDto.getKeyword(), requestDto.getSearchBy(), status));
        Page<BorrowReceipt> page = borrowReceiptRepository.findAll(spec, pageable);

        List<BorrowReceiptForReaderResponseDto> items = page.getContent().stream()
                .map(BorrowReceiptForReaderResponseDto::new)
                .toList();

        PagingMeta pagingMeta = PaginationUtil.buildPagingMeta(requestDto, SortByDataConstant.BORROW_RECEIPT, page);

        PaginationResponseDto<BorrowReceiptForReaderResponseDto> responseDto = new PaginationResponseDto<>();
        responseDto.setItems(items);
        responseDto.setMeta(pagingMeta);

        return responseDto;
    }

    @Override
    public BorrowReceiptDetailsDto findDetailsById(Long id) {
        borrowReceiptRepository.updateOverdueStatus();
        BorrowReceipt borrowReceipt = getEntity(id);
        return new BorrowReceiptDetailsDto(borrowReceipt);
    }

    @Override
    public CommonResponseDto cancelReturn(Set<Long> borrowIds, String userId) {
        List<BorrowReceipt> borrowReceipts = borrowReceiptRepository.findAllByIdIn(borrowIds);
        if (borrowReceipts.isEmpty()) {
            throw new BadRequestException(ErrorMessage.BorrowReceipt.ERR_NOT_FOUND_ID, borrowIds);
        }

        LocalDate now = LocalDate.now();
        List<Book> updatedBooks = new ArrayList<>();
        List<BookBorrow> updatedBookBorrows = new ArrayList<>();
        List<BorrowReceipt> updateBorrowReceipts = new ArrayList<>();
        List<String> receiptNumbers = new ArrayList<>();

        //Cập nhật trạng thái phiếu mượn
        for (BorrowReceipt borrowReceipt : borrowReceipts) {
            //Cập nhật trạng thái sách
            for (BookBorrow bookBorrow : borrowReceipt.getBookBorrows()) {
                Book book = bookBorrow.getBook();
                if (book.getBookCondition().equals(BookCondition.ON_LOAN)) {
                    throw new ConflictException(ErrorMessage.BookBorrow.ERR_NOT_RETURNED_IN_ANOTHER_RECEIPT, book.getBookCode());
                }
                book.setBookCondition(BookCondition.ON_LOAN);

                bookBorrow.setReturnDate(null);
                bookBorrow.setStatus(BookBorrowStatus.NOT_RETURNED);

                updatedBooks.add(book);
                updatedBookBorrows.add(bookBorrow);
            }

            if (borrowReceipt.getDueDate().isBefore(now)) {
                borrowReceipt.setStatus(BorrowStatus.OVERDUE);
            } else {
                borrowReceipt.setStatus(BorrowStatus.NOT_RETURNED);
            }
            updateBorrowReceipts.add(borrowReceipt);

            receiptNumbers.add(borrowReceipt.getReceiptNumber());
        }

        bookRepository.saveAll(updatedBooks);
        bookBorrowRepository.saveAll(updatedBookBorrows);
        borrowReceiptRepository.saveAll(updateBorrowReceipts);

        logService.createLog(TAG, EventConstants.EDIT, "Hủy trả sách cho các phiếu mượn: " + String.join(", ", receiptNumbers), userId);

        String message = messageSource.getMessage(SuccessMessage.UPDATE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message);
    }

    @Override
    public byte[] createPdfForReceipts(CreateBorrowReceiptRequestDto requestDto, String userId) {
        if (requestDto.getBorrowIds().isEmpty()) {
            return pdfService.createReceiptWithFourPerPage(requestDto);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.User.ERR_NOT_FOUND_ID, userId));

        List<BorrowReceipt> borrowReceipts = borrowReceiptRepository.findAllByIdIn(requestDto.getBorrowIds());
        if (borrowReceipts.isEmpty()) {
            throw new BadRequestException(ErrorMessage.BorrowReceipt.ERR_NOT_FOUND_ID, requestDto.getBorrowIds());
        }

        return pdfService.createReceipt(user, requestDto, borrowReceipts);
    }

    @Override
    public byte[] createOverdueListPdf() {
        List<BorrowReceipt> borrowReceipts = borrowReceiptRepository.findRecordsByReturnDate();
        if (borrowReceipts.isEmpty()) {
            throw new BadRequestException(ErrorMessage.BorrowReceipt.ERR_NOT_FOUND_OVERDUE);
        }

        return pdfService.createOverdueListPdf(borrowReceipts);
    }

    @Override
    public byte[] exportReturnData() {
        try {
            return excelExportService.createBorrowingReport(borrowReceiptRepository.findAll());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}