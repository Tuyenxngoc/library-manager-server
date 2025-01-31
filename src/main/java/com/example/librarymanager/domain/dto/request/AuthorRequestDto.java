package com.example.librarymanager.domain.dto.request;

import com.example.librarymanager.constant.CommonConstant;
import com.example.librarymanager.constant.ErrorMessage;
import com.example.librarymanager.constant.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthorRequestDto {

    @NotBlank(message = ErrorMessage.INVALID_NOT_BLANK_FIELD)
    @Pattern(regexp = CommonConstant.REGEXP_FULL_NAME, message = ErrorMessage.INVALID_FORMAT_NAME)
    @Size(min = 2, max = 100, message = ErrorMessage.INVALID_TEXT_LENGTH)
    private String fullName;

    @NotBlank(message = ErrorMessage.INVALID_NOT_BLANK_FIELD)
    @Size(max = 100, message = ErrorMessage.INVALID_TEXT_LENGTH)
    private String code; // Mã hiệu

    private String penName; // Bí danh

    private Gender gender; // Giới tính

    private LocalDate dateOfBirth; // Ngày sinh

    private LocalDate dateOfDeath; // Ngày mất

    private String title; // Chức danh

    private String residence; // Thường trú

    private String address; // Địa chỉ

    private String notes; // Ghi chú

}
