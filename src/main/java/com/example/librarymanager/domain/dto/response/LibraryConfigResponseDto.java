package com.example.librarymanager.domain.dto.response;

import lombok.Getter;

@Getter
public class LibraryConfigResponseDto {
    private final int rowsPerPage;

    private final int reservationTime;

    private final int maxBorrowLimit;

    private final int maxRenewalTimes;

    private final int maxRenewalDays;

    public LibraryConfigResponseDto(int rowsPerPage, int reservationTime, int maxBorrowLimit, int maxRenewalTimes, int maxRenewalDays) {
        this.rowsPerPage = rowsPerPage;
        this.reservationTime = reservationTime;
        this.maxBorrowLimit = maxBorrowLimit;
        this.maxRenewalTimes = maxRenewalTimes;
        this.maxRenewalDays = maxRenewalDays;
    }
}
