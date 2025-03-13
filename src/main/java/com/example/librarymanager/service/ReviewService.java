package com.example.librarymanager.service;

import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.request.ReviewRequestDto;
import com.example.librarymanager.domain.dto.response.ReviewResponseDto;

import java.util.List;

public interface ReviewService {
    CommonResponseDto addReview(String cardNumber, ReviewRequestDto request);

    List<ReviewResponseDto> getReviewsByBook(Long bookId);

    List<ReviewResponseDto> getReviewsByReader(Long readerId);

    CommonResponseDto updateReview(Long reviewId, ReviewRequestDto request);

    CommonResponseDto deleteReview(Long reviewId);
}
