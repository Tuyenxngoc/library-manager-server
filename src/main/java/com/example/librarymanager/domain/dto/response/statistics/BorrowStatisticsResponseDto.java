package com.example.librarymanager.domain.dto.response.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BorrowStatisticsResponseDto {
    private final int borrowRequests;

    private final int currentlyBorrowed;

    private final int dueToday;

    private final int overdue;
}
