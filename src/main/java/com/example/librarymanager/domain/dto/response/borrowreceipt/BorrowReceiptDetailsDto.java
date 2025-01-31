package com.example.librarymanager.domain.dto.response.borrowreceipt;

import com.example.librarymanager.constant.BookBorrowStatus;
import com.example.librarymanager.constant.BorrowStatus;
import com.example.librarymanager.domain.entity.BookBorrow;
import com.example.librarymanager.domain.entity.BorrowReceipt;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class BorrowReceiptDetailsDto {

    @Getter
    static class BookDto {
        private final String title;
        private final String bookCode;
        private final LocalDate returnDate;
        private final BookBorrowStatus status;

        public BookDto(BookBorrow book) {
            this.title = book.getBook().getBookDefinition().getTitle();
            this.bookCode = book.getBook().getBookCode();
            this.returnDate = book.getReturnDate();
            this.status = book.getStatus();
        }
    }

    private final Long id;
    private final String receiptNumber;
    private final String fullName;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;
    private final BorrowStatus status;
    private final List<BookDto> books;

    public BorrowReceiptDetailsDto(BorrowReceipt borrowReceipt) {
        this.id = borrowReceipt.getId();
        this.receiptNumber = borrowReceipt.getReceiptNumber();
        this.fullName = borrowReceipt.getReader().getFullName();
        this.borrowDate = borrowReceipt.getBorrowDate();
        this.dueDate = borrowReceipt.getDueDate();
        this.status = borrowReceipt.getStatus();
        this.books = borrowReceipt.getBookBorrows().stream()
                .map(BookDto::new)
                .toList();
    }
}
