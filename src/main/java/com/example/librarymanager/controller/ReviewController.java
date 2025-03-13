package com.example.librarymanager.controller;

import com.example.librarymanager.annotation.CurrentUser;
import com.example.librarymanager.annotation.RestApiV1;
import com.example.librarymanager.base.VsResponseUtil;
import com.example.librarymanager.constant.UrlConstant;
import com.example.librarymanager.domain.dto.request.ReviewRequestDto;
import com.example.librarymanager.security.CustomUserDetails;
import com.example.librarymanager.service.ReviewService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Review")
public class ReviewController {

    ReviewService reviewService;

    @GetMapping(UrlConstant.Review.GET_BY_BOOK)
    public ResponseEntity<?> getReviewsByBook(@PathVariable Long bookId) {
        return VsResponseUtil.success(reviewService.getReviewsByBook(bookId));
    }

    @GetMapping(UrlConstant.Review.GET_BY_READER_ID)
    public ResponseEntity<?> getReviewsByUser(@PathVariable Long readerId) {
        return VsResponseUtil.success(reviewService.getReviewsByReader(readerId));
    }

    @PostMapping(UrlConstant.Review.CREATE)
    public ResponseEntity<?> addReview(@RequestBody ReviewRequestDto request, @CurrentUser CustomUserDetails userDetails) {
        return VsResponseUtil.success(HttpStatus.CREATED, reviewService.addReview(userDetails.getCardNumber(), request));
    }

    @PutMapping(UrlConstant.Review.UPDATE)
    public ResponseEntity<?> updateReview(@PathVariable Long reviewId, @RequestBody ReviewRequestDto request) {
        return VsResponseUtil.success(reviewService.updateReview(reviewId, request));
    }

    @DeleteMapping(UrlConstant.Review.DELETE)
    public ResponseEntity<?> deleteReview(@PathVariable Long reviewId) {
        return VsResponseUtil.success(reviewService.deleteReview(reviewId));
    }

}
