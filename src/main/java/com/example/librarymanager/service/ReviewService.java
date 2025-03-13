package com.example.librarymanager.service;

import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.request.ReviewRequestDto;

public interface ReviewService {
    CommonResponseDto addReview(String cardNumber, ReviewRequestDto request);

    Object getReviewsByBook(Long bookId);

    Object getReviewsByReader(Long readerId);

    CommonResponseDto updateReview(Long reviewId, ReviewRequestDto request);

    void deleteReview(Long reviewId);
}
