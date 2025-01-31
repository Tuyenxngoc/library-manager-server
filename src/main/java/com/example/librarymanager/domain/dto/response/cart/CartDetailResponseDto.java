package com.example.librarymanager.domain.dto.response.cart;

import com.example.librarymanager.constant.CommonConstant;
import com.example.librarymanager.domain.dto.common.BaseEntityDto;
import com.example.librarymanager.domain.entity.BookAuthor;
import com.example.librarymanager.domain.entity.CartDetail;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CartDetailResponseDto {

    private final Long id;

    private final String bookCode;          // Mã nhan đề

    private final String title;             // Nhan đề

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstant.PATTERN_DATE_TIME)
    private final LocalDateTime borrowFrom; // Đăng ký mượn từ

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstant.PATTERN_DATE_TIME)
    private final LocalDateTime borrowTo;   // Đến

    private final List<BaseEntityDto> authors = new ArrayList<>();        // Tác giả

    public CartDetailResponseDto(CartDetail cartDetail) {
        this.id = cartDetail.getId();
        this.bookCode = cartDetail.getBook().getBookCode();
        this.title = cartDetail.getBook().getBookDefinition().getTitle();
        this.borrowFrom = cartDetail.getBorrowFrom();
        this.borrowTo = cartDetail.getBorrowTo();

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
