package com.example.librarymanager.service.impl;

import com.example.librarymanager.constant.ErrorMessage;
import com.example.librarymanager.constant.SuccessMessage;
import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.request.CreateReviewRequestDto;
import com.example.librarymanager.domain.dto.request.UpdateReviewRequestDto;
import com.example.librarymanager.domain.dto.response.ReviewResponseDto;
import com.example.librarymanager.domain.entity.BookDefinition;
import com.example.librarymanager.domain.entity.Reader;
import com.example.librarymanager.domain.entity.Review;
import com.example.librarymanager.exception.BadRequestException;
import com.example.librarymanager.exception.NotFoundException;
import com.example.librarymanager.exception.UnauthorizedException;
import com.example.librarymanager.repository.BookDefinitionRepository;
import com.example.librarymanager.repository.ReaderRepository;
import com.example.librarymanager.repository.ReviewRepository;
import com.example.librarymanager.service.ReviewService;
import com.example.librarymanager.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    private final BookDefinitionRepository bookDefinitionRepository;

    private final ReaderRepository readerRepository;

    private final MessageUtil messageUtil;

    private Review getEntity(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Review.ERR_NOT_FOUND_ID, reviewId));
    }

    @Override
    public List<ReviewResponseDto> getReviewsByBook(Long bookId) {
        return reviewRepository.getReviewsByBook(bookId);
    }

    @Override
    public List<ReviewResponseDto> getReviewsByReader(Long readerId) {
        return reviewRepository.getReviewsByReader(readerId);
    }

    @Override
    public ReviewResponseDto addReview(String cardNumber, CreateReviewRequestDto requestDto) {
        boolean hasReviewed = reviewRepository.existsByBookDefinition_IdAndReader_CardNumber(requestDto.getBookDefinitionId(), cardNumber);
        if (hasReviewed) {
            throw new BadRequestException(ErrorMessage.Review.ERR_ALREADY_REVIEWED);
        }

        BookDefinition bookDefinition = bookDefinitionRepository.findById(requestDto.getBookDefinitionId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.BookDefinition.ERR_NOT_FOUND_ID, requestDto.getBookDefinitionId()));

        Reader reader = readerRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Reader.ERR_NOT_FOUND_CARD_NUMBER, cardNumber));

        Review review = new Review();
        review.setBookDefinition(bookDefinition);
        review.setReader(reader);
        review.setComment(requestDto.getComment());
        review.setRating(requestDto.getRating());

        reviewRepository.save(review);

        return new ReviewResponseDto(review);
    }

    @Override
    public ReviewResponseDto updateReview(Long reviewId, UpdateReviewRequestDto requestDto, String cardNumber) {
        Review review = getEntity(reviewId);

        if (cardNumber != null && !review.getReader().getCardNumber().equals(cardNumber)) {
            throw new UnauthorizedException(ErrorMessage.ERR_FORBIDDEN_UPDATE_DELETE);
        }

        review.setRating(requestDto.getRating());
        review.setComment(requestDto.getComment());

        reviewRepository.save(review);

        return new ReviewResponseDto(review);
    }

    @Override
    public CommonResponseDto deleteReview(Long reviewId, String cardNumber) {
        Review review = getEntity(reviewId);

        if (cardNumber != null && !review.getReader().getCardNumber().equals(cardNumber)) {
            throw new UnauthorizedException(ErrorMessage.ERR_FORBIDDEN_UPDATE_DELETE);
        }

        reviewRepository.delete(review);

        String message = messageUtil.getMessage(SuccessMessage.DELETE);
        return new CommonResponseDto(message);
    }
}
