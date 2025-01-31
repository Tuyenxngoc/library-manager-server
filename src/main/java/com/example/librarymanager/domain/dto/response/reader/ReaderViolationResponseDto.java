package com.example.librarymanager.domain.dto.response.reader;

import com.example.librarymanager.constant.PenaltyForm;
import com.example.librarymanager.domain.entity.ReaderViolation;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ReaderViolationResponseDto {
    private final long id;

    private final String violationDetails;

    private final PenaltyForm penaltyForm;

    private final String otherPenaltyForm;

    private final LocalDate penaltyDate;

    private final LocalDate endDate;

    private final Double fineAmount;

    private final String notes;

    private final long readerId;

    private final String cardNumber;

    private final String fullName;

    public ReaderViolationResponseDto(ReaderViolation violation) {
        this.id = violation.getId();
        this.violationDetails = violation.getViolationDetails();
        this.penaltyForm = violation.getPenaltyForm();
        this.otherPenaltyForm = violation.getOtherPenaltyForm();
        this.penaltyDate = violation.getPenaltyDate();
        this.endDate = violation.getEndDate();
        this.fineAmount = violation.getFineAmount();
        this.notes = violation.getNotes();
        this.readerId = violation.getReader().getId();
        this.cardNumber = violation.getReader().getCardNumber();
        this.fullName = violation.getReader().getFullName();
    }
}
