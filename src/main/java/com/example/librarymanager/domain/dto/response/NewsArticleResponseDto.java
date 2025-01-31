package com.example.librarymanager.domain.dto.response;

import com.example.librarymanager.domain.entity.NewsArticle;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class NewsArticleResponseDto {
    private final long id;

    private final String image;

    private final LocalDate createdDate;

    private final String title;

    private final String titleSlug;

    private final String description;

    private final String author;

    public NewsArticleResponseDto(NewsArticle newsArticle) {
        this.id = newsArticle.getId();
        this.image = newsArticle.getImageUrl();
        this.createdDate = newsArticle.getCreatedDate();
        this.title = newsArticle.getTitle();
        this.titleSlug = newsArticle.getTitleSlug();
        this.description = newsArticle.getDescription();
        this.author = "Admin";
    }
}
