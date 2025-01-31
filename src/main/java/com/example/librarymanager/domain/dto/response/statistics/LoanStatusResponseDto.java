package com.example.librarymanager.domain.dto.response.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoanStatusResponseDto {
    private final double borrowedBooks;

    private final double overdueBooks;

    private final double percentageBorrowed;

    private final double percentageOverdue;
}
