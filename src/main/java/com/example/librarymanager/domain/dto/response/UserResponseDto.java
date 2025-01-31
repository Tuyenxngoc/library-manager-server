package com.example.librarymanager.domain.dto.response;

import com.example.librarymanager.constant.AccountStatus;
import com.example.librarymanager.domain.dto.common.BaseEntityDto;
import com.example.librarymanager.domain.entity.User;
import com.example.librarymanager.domain.entity.UserGroup;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UserResponseDto {
    private final String username;

    private final BaseEntityDto userGroup;

    private final LocalDate expiryDate;

    private final AccountStatus status;

    private final String fullName;

    private final String id;

    private final String position;

    private final String email;

    private final String phoneNumber;

    private final String address;

    private final String note;

    public UserResponseDto(User user) {
        this.username = user.getUsername();
        UserGroup g = user.getUserGroup();
        this.userGroup = new BaseEntityDto(g.getId(), g.getName());
        this.expiryDate = user.getExpiryDate();
        this.status = user.getStatus();
        this.fullName = user.getFullName();
        this.id = user.getId();
        this.position = user.getPosition();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
        this.address = user.getAddress();
        this.note = user.getNote();
    }

}
