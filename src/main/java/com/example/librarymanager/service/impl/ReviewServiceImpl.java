package com.example.librarymanager.service.impl;

import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.request.ReviewRequestDto;
import com.example.librarymanager.repository.ReviewRepository;
import com.example.librarymanager.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;


    @Override
    public CommonResponseDto addReview(String cardNumber, ReviewRequestDto request) {
        return null;
    }

    @Override
    public Object getReviewsByBook(Long bookId) {
        return null;
    }

    @Override
    public Object getReviewsByReader(Long readerId) {
        return null;
    }

    @Override
    public CommonResponseDto updateReview(Long reviewId, ReviewRequestDto request) {
        return null;
    }

    @Override
    public void deleteReview(Long reviewId) {

    }
}
