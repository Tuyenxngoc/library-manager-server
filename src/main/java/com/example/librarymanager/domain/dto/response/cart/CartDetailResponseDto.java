package com.example.librarymanager.domain.dto.response.cart;

import com.example.librarymanager.domain.dto.common.BaseEntityDto;
import com.example.librarymanager.domain.entity.BookAuthor;
import com.example.librarymanager.domain.entity.CartDetail;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CartDetailResponseDto {

    private final Long id;

    private final String bookCode;

    private final String title;

    private final List<BaseEntityDto> authors = new ArrayList<>();

    public CartDetailResponseDto(CartDetail cartDetail) {
        this.id = cartDetail.getId();
        this.bookCode = cartDetail.getBook().getBookCode();
        this.title = cartDetail.getBook().getBookDefinition().getTitle();

        // Set authors
        List<BookAuthor> au = cartDetail.getBook().getBookDefinition().getBookAuthors();
        if (au != null) {
            this.authors.addAll(au.stream()
                    .map(BookAuthor::getAuthor)
                    .map(author -> new BaseEntityDto(author.getId(), author.getFullName()))
                    .toList());
        }
    }
}
