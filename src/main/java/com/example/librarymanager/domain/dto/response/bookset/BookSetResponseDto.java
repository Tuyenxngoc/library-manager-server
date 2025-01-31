package com.example.librarymanager.domain.dto.response.bookset;

import com.example.librarymanager.domain.entity.BookSet;
import lombok.Getter;

@Getter
public class BookSetResponseDto {

    private final Long id;

    private final String name;

    private final Boolean activeFlag;

    private final int bookCount;

    public BookSetResponseDto(BookSet bookSet) {
        this.id = bookSet.getId();
        this.name = bookSet.getName();
        this.activeFlag = bookSet.getActiveFlag();
        this.bookCount = bookSet.getBookDefinitions().size();
    }
}
