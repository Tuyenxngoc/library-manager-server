package com.example.librarymanager.domain.dto.response.statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LibraryStatisticsResponseDto {
    private final long publications;

    private final long authors;

    private final long publishers;

    private final long readers;
}
