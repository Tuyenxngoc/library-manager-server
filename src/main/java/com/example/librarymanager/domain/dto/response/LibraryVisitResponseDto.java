package com.example.librarymanager.domain.dto.response;

import com.example.librarymanager.constant.CommonConstant;
import com.example.librarymanager.domain.entity.LibraryVisit;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class LibraryVisitResponseDto {
    private final long id;

    private final String cardNumber;

    private final String fullName;

    private final String cardType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstant.PATTERN_DATE_TIME)
    private final LocalDateTime entryTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = CommonConstant.PATTERN_DATE_TIME)
    private final LocalDateTime exitTime;

    public LibraryVisitResponseDto(LibraryVisit libraryVisit) {
        this.id = libraryVisit.getId();
        this.cardNumber = libraryVisit.getReader().getCardNumber();
        this.fullName = libraryVisit.getReader().getFullName();
        this.cardType = libraryVisit.getReader().getCardType().getDisplayName();
        this.entryTime = libraryVisit.getEntryTime();
        this.exitTime = libraryVisit.getExitTime();
    }
}
