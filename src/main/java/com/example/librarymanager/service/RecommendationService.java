package com.example.librarymanager.service;

import com.example.librarymanager.domain.dto.response.bookdefinition.BookForReaderResponseDto;

import java.util.List;

public interface RecommendationService {
    List<BookForReaderResponseDto> recommendBooks(String cardNumber, int topN);
}
