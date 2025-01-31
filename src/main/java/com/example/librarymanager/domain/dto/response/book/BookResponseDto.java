package com.example.librarymanager.domain.dto.response.book;

import com.example.librarymanager.constant.BookCondition;
import com.example.librarymanager.constant.BookStatus;
import com.example.librarymanager.domain.dto.response.bookdefinition.BookDefinitionResponseDto;
import com.example.librarymanager.domain.entity.Book;
import lombok.Getter;

@Getter
public class BookResponseDto {

    private final long id;

    private final String bookCode;

    private final BookCondition bookCondition;

    private final BookStatus bookStatus;

    private final BookDefinitionResponseDto bookDefinition;

    public BookResponseDto(Book book) {
        this.id = book.getId();
        this.bookCode = book.getBookCode();
        this.bookCondition = book.getBookCondition();
        this.bookStatus = book.getBookStatus();
        this.bookDefinition = new BookDefinitionResponseDto(book.getBookDefinition());
    }
}
