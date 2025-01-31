package com.example.librarymanager.service;

import com.example.librarymanager.domain.dto.pagination.PaginationRequestDto;
import com.example.librarymanager.domain.dto.response.statistics.*;

import java.util.List;

public interface StatisticsService {
    LibraryStatisticsResponseDto getLibraryStatistics();

    BorrowStatisticsResponseDto getBorrowStatistics();

    LoanStatusResponseDto getLoanStatus();

    List<PublicationResponseDto> getMostBorrowedPublications();

    List<CategoryStatisticsResponseDto> getPublicationCountByCategory(PaginationRequestDto requestDto);
}
