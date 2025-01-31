package com.example.librarymanager.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HolidayResponseDto {
    private String id;

    private String name;

    private LocalDate startDate;

    private LocalDate endDate;

    private Boolean activeFlag;
}
