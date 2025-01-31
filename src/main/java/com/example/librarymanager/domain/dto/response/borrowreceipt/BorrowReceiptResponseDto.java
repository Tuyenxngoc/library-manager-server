package com.example.librarymanager.domain.dto.response.borrowreceipt;

import com.example.librarymanager.constant.BorrowStatus;
import com.example.librarymanager.domain.entity.BorrowReceipt;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class BorrowReceiptResponseDto {
    private final long id;

    private final String receiptNumber;

    private final LocalDate borrowDate;

    private final LocalDate dueDate;

    private final BorrowStatus status;

    private final String note;

    private final String cardNumber;

    private final String fullName;

    private final int books;

    public BorrowReceiptResponseDto(BorrowReceipt borrowReceipt) {
        this.id = borrowReceipt.getId();
        this.receiptNumber = borrowReceipt.getReceiptNumber();
        this.borrowDate = borrowReceipt.getBorrowDate();
        this.dueDate = borrowReceipt.getDueDate();
        this.status = borrowReceipt.getStatus();
        this.note = borrowReceipt.getNote();
        this.cardNumber = borrowReceipt.getReader().getCardNumber();
        this.fullName = borrowReceipt.getReader().getFullName();
        this.books = borrowReceipt.getBookBorrows().size();
    }
}
