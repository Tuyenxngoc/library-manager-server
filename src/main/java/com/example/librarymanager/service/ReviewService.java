package com.example.librarymanager.service;

import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.request.CreateReviewRequestDto;
import com.example.librarymanager.domain.dto.request.UpdateReviewRequestDto;
import com.example.librarymanager.domain.dto.response.ReviewResponseDto;

import java.util.List;

public interface ReviewService {
    List<ReviewResponseDto> getReviewsByBook(Long bookId);

    List<ReviewResponseDto> getReviewsByReader(Long readerId);

    ReviewResponseDto addReview(String cardNumber, CreateReviewRequestDto requestDto);

    ReviewResponseDto updateReview(Long reviewId, UpdateReviewRequestDto requestDto, String cardNumber);

    CommonResponseDto deleteReview(Long reviewId, String cardNumber);
}
