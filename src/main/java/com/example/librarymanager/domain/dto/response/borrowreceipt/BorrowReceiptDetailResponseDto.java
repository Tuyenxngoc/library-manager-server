package com.example.librarymanager.domain.dto.response.borrowreceipt;

import com.example.librarymanager.domain.entity.BookBorrow;
import com.example.librarymanager.domain.entity.BorrowReceipt;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BorrowReceiptDetailResponseDto {

    private Long id;

    private String receiptNumber;

    private LocalDate borrowDate;

    private LocalDate dueDate;

    private String note;

    private long readerId;

    private List<String> books = new ArrayList<>();

    public BorrowReceiptDetailResponseDto() {
    }

    public BorrowReceiptDetailResponseDto(BorrowReceipt borrowReceipt) {
        this.id = borrowReceipt.getId();
        this.receiptNumber = borrowReceipt.getReceiptNumber();
        this.borrowDate = borrowReceipt.getBorrowDate();
        this.dueDate = borrowReceipt.getDueDate();
        this.note = borrowReceipt.getNote();
        this.readerId = borrowReceipt.getReader().getId();
        for (BookBorrow bookBorrow : borrowReceipt.getBookBorrows()) {
            books.add(bookBorrow.getBook().getBookCode());
        }
    }
}