package com.example.librarymanager.domain.dto.response;

import com.example.librarymanager.domain.entity.Review;
import lombok.Getter;

@Getter
public class ReviewResponseDto {

    private final long id;

    private final int rating;

    private final String comment;

    public ReviewResponseDto(Review review) {
        this.id = review.getId();
        this.rating = review.getRating();
        this.comment = review.getComment();
    }

}
