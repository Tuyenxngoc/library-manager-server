package com.example.librarymanager.controller;

import com.example.librarymanager.annotation.CurrentUser;
import com.example.librarymanager.annotation.RestApiV1;
import com.example.librarymanager.base.VsResponseUtil;
import com.example.librarymanager.constant.UrlConstant;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.request.NewsArticleRequestDto;
import com.example.librarymanager.security.CustomUserDetails;
import com.example.librarymanager.service.NewsArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "News Article")
public class NewsArticleController {

    NewsArticleService newsArticleService;

    @Operation(summary = "API Create News Article")
    @PreAuthorize("hasRole('ROLE_MANAGE_NEWS_ARTICLE')")
    @PostMapping(value = UrlConstant.NewsArticle.CREATE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createNewsArticle(
            @Valid @ModelAttribute NewsArticleRequestDto requestDto,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            @CurrentUser CustomUserDetails userDetails
    ) {
        return VsResponseUtil.success(HttpStatus.CREATED, newsArticleService.save(requestDto, imageFile, userDetails.getUserId()));
    }

    @Operation(summary = "API Update News Article")
    @PreAuthorize("hasRole('ROLE_MANAGE_NEWS_ARTICLE')")
    @PutMapping(value = UrlConstant.NewsArticle.UPDATE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateNewsArticle(
            @PathVariable Long id,
            @Valid @ModelAttribute NewsArticleRequestDto requestDto,
            @RequestParam(value = "image", required = false) MultipartFile imageFile,
            @CurrentUser CustomUserDetails userDetails
    ) {
        return VsResponseUtil.success(newsArticleService.update(id, requestDto, imageFile, userDetails.getUserId()));
    }

    @Operation(summary = "API Delete News Article")
    @PreAuthorize("hasRole('ROLE_MANAGE_NEWS_ARTICLE')")
    @DeleteMapping(UrlConstant.NewsArticle.DELETE)
    public ResponseEntity<?> deleteNewsArticle(
            @PathVariable Long id,
            @CurrentUser CustomUserDetails userDetails
    ) {
        return VsResponseUtil.success(newsArticleService.delete(id, userDetails.getUserId()));
    }

    @Operation(summary = "API Get All News Articles")
    @PreAuthorize("hasRole('ROLE_MANAGE_NEWS_ARTICLE')")
    @GetMapping(UrlConstant.NewsArticle.GET_ALL)
    public ResponseEntity<?> getAllNewsArticles(@ParameterObject PaginationFullRequestDto requestDto) {
        return VsResponseUtil.success(newsArticleService.findAll(requestDto));
    }

    @Operation(summary = "API Get News Article By Id")
    @PreAuthorize("hasRole('ROLE_MANAGE_NEWS_ARTICLE')")
    @GetMapping(UrlConstant.NewsArticle.GET_BY_ID)
    public ResponseEntity<?> getNewsArticleById(@PathVariable Long id) {
        return VsResponseUtil.success(newsArticleService.findById(id));
    }

    @Operation(summary = "API Toggle Active Status of News Article")
    @PreAuthorize("hasRole('ROLE_MANAGE_NEWS_ARTICLE')")
    @PatchMapping(UrlConstant.NewsArticle.TOGGLE_ACTIVE)
    public ResponseEntity<?> toggleActiveStatus(
            @PathVariable Long id,
            @CurrentUser CustomUserDetails userDetails
    ) {
        return VsResponseUtil.success(newsArticleService.toggleActiveStatus(id, userDetails.getUserId()));
    }

    @Operation(summary = "API Get News Articles")
    @GetMapping(UrlConstant.NewsArticle.GET_ALL_FOR_USER)
    public ResponseEntity<?> getNewsArticles(@ParameterObject PaginationFullRequestDto requestDto) {
        return VsResponseUtil.success(newsArticleService.getNewsArticles(requestDto));
    }

    @Operation(summary = "API Get News Article Detail")
    @GetMapping(UrlConstant.NewsArticle.GET_BY_ID_FOR_USER)
    public ResponseEntity<?> getNewsArticleDetail(@PathVariable String id) {
        return VsResponseUtil.success(newsArticleService.getNewsArticleByTitleSlug(id));
    }
}
