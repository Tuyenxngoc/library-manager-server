package com.example.librarymanager.repository;

import com.example.librarymanager.domain.dto.response.ReviewResponseDto;
import com.example.librarymanager.domain.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Review> {

    @Query("SELECT new com.example.librarymanager.domain.dto.response.ReviewResponseDto(r) FROM Review r WHERE r.bookDefinition.id = :bookDefinitionId")
    List<ReviewResponseDto> getReviewsByBook(@Param("bookDefinitionId") Long bookDefinitionId);

    @Query("SELECT new com.example.librarymanager.domain.dto.response.ReviewResponseDto(r) FROM Review r WHERE r.reader.id = :readerId")
    List<ReviewResponseDto> getReviewsByReader(@Param("readerId") Long readerId);

    boolean existsByBookDefinition_IdAndReader_CardNumber(Long bookDefinitionId, String cardNumber);

}
