package com.example.librarymanager.domain.dto.response.borrowreceipt;

import com.example.librarymanager.domain.dto.response.BookBorrowDto;
import com.example.librarymanager.domain.entity.BorrowReceipt;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class BorrowReceiptForReaderResponseDto {
    private final long id;

    private final String receiptNumber;

    private final LocalDate borrowDate; //Ngày mượn

    private final LocalDate dueDate; // Ngày hết hạn

    private final String status;

    private final String note; // Ghi chú

    private final List<BookBorrowDto> books;//Số sách mượn

    public BorrowReceiptForReaderResponseDto(BorrowReceipt borrowReceipt) {
        this.id = borrowReceipt.getId();
        this.receiptNumber = borrowReceipt.getReceiptNumber();
        this.borrowDate = borrowReceipt.getBorrowDate();
        this.dueDate = borrowReceipt.getDueDate();
        this.status = borrowReceipt.getStatus().getName();
        this.note = borrowReceipt.getNote();
        this.books = borrowReceipt.getBookBorrows().stream()
                .map(BookBorrowDto::new)
                .toList();
    }

}
