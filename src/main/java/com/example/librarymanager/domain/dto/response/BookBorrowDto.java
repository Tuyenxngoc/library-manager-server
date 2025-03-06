package com.example.librarymanager.domain.dto.response;

import com.example.librarymanager.constant.BookBorrowStatus;
import com.example.librarymanager.domain.entity.BookBorrow;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class BookBorrowDto {
    private final String title;
    private final String bookCode;
    private final LocalDate returnDate;
    private final BookBorrowStatus status;

    public BookBorrowDto(BookBorrow book) {
        this.title = book.getBook().getBookDefinition().getTitle();
        this.bookCode = book.getBook().getBookCode();
        this.returnDate = book.getReturnDate();
        this.status = book.getStatus();
    }

}