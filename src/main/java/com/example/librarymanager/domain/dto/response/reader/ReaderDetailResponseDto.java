package com.example.librarymanager.domain.dto.response.reader;

import com.example.librarymanager.domain.entity.Reader;
import com.example.librarymanager.util.MaskingUtils;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ReaderDetailResponseDto {
    private final String cardNumber;

    private final String fullName;

    private final String email;

    private final String phoneNumber;

    private final String gender;

    private final LocalDate dateOfBirth;

    private final String address;

    private final String avatar;

    private final String status;

    private final LocalDate createdDate;

    private final LocalDate expiryDate;

    public ReaderDetailResponseDto(Reader reader) {
        this.cardNumber = reader.getCardNumber();
        this.fullName = reader.getFullName();
        this.email = MaskingUtils.maskEmail(reader.getEmail());
        this.phoneNumber = MaskingUtils.maskPhoneNumber(reader.getPhoneNumber());
        this.gender = reader.getGender() != null ? reader.getGender().getName() : "";
        this.dateOfBirth = reader.getDateOfBirth();
        this.address = reader.getAddress();
        this.avatar = reader.getAvatar();
        this.status = reader.getStatus().getName();
        this.createdDate = LocalDate.now();
        this.expiryDate = reader.getExpiryDate();
    }

}
