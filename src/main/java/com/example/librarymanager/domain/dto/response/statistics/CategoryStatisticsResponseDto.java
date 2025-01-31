package com.example.librarymanager.domain.dto.response.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryStatisticsResponseDto {
    private String categoryName;

    private Long count;
}
