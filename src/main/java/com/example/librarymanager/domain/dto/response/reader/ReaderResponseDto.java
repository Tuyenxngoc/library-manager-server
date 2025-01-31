package com.example.librarymanager.domain.dto.response.reader;

import com.example.librarymanager.constant.CardStatus;
import com.example.librarymanager.constant.CardType;
import com.example.librarymanager.constant.Gender;
import com.example.librarymanager.domain.entity.Reader;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ReaderResponseDto {

    private final long id;

    private final CardType cardType;

    private final String fullName;

    private final LocalDate dateOfBirth;

    private final Gender gender;

    private final String avatar;

    private final String address;

    private final String email;

    private final String phoneNumber;

    private final String cardNumber;

    private final LocalDate expiryDate;

    private final CardStatus status;

    private final long currentBorrowedBooks;

    private final long libraryVisitCount;

    public ReaderResponseDto(Reader reader) {
        this.id = reader.getId();
        this.cardType = reader.getCardType();
        this.fullName = reader.getFullName();
        this.dateOfBirth = reader.getDateOfBirth();
        this.gender = reader.getGender();
        this.avatar = reader.getAvatar();
        this.address = reader.getAddress();
        this.email = reader.getEmail();
        this.phoneNumber = reader.getPhoneNumber();
        this.cardNumber = reader.getCardNumber();
        this.expiryDate = reader.getExpiryDate();
        this.status = reader.getStatus();

        // Tính số phiếu mượn
        this.currentBorrowedBooks = reader.getBorrowReceipts().size();

        // Tính số lượt vào thư viện
        this.libraryVisitCount = reader.getLibraryVisits().size();
    }

}
