package com.example.librarymanager.domain.mapper;

import com.example.librarymanager.domain.dto.request.NewsArticleRequestDto;
import com.example.librarymanager.domain.entity.NewsArticle;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NewsArticleMapper {

    NewsArticle toNewsArticle(NewsArticleRequestDto requestDto);

}
