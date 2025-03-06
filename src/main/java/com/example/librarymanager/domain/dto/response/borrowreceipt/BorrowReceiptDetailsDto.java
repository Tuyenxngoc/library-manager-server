package com.example.librarymanager.domain.dto.response.borrowreceipt;

import com.example.librarymanager.constant.BorrowStatus;
import com.example.librarymanager.domain.dto.response.BookBorrowDto;
import com.example.librarymanager.domain.entity.BorrowReceipt;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class BorrowReceiptDetailsDto {
    private final Long id;
    private final String receiptNumber;
    private final String fullName;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;
    private final BorrowStatus status;
    private final List<BookBorrowDto> books;

    public BorrowReceiptDetailsDto(BorrowReceipt borrowReceipt) {
        this.id = borrowReceipt.getId();
        this.receiptNumber = borrowReceipt.getReceiptNumber();
        this.fullName = borrowReceipt.getReader().getFullName();
        this.borrowDate = borrowReceipt.getBorrowDate();
        this.dueDate = borrowReceipt.getDueDate();
        this.status = borrowReceipt.getStatus();
        this.books = borrowReceipt.getBookBorrows().stream()
                .map(BookBorrowDto::new)
                .toList();
    }
}
