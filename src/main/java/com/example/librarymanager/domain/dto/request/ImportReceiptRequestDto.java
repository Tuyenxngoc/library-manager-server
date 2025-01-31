package com.example.librarymanager.domain.dto.request;

import com.example.librarymanager.constant.ErrorMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImportReceiptRequestDto {
    @NotBlank(message = ErrorMessage.INVALID_NOT_BLANK_FIELD)
    @Size(max = 100, message = ErrorMessage.INVALID_TEXT_LENGTH)
    private String receiptNumber;  // Số phiếu nhập

    @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
    private LocalDate importDate;  // Ngày nhập

    @Size(max = 100, message = ErrorMessage.INVALID_TEXT_LENGTH)
    private String generalRecordNumber;  // Số vào sổ tổng quát

    @Size(max = 100, message = ErrorMessage.INVALID_TEXT_LENGTH)
    private String fundingSource;  // Nguồn cấp

    @Size(max = 100, message = ErrorMessage.INVALID_TEXT_LENGTH)
    private String importReason;  // Lý do nhập

    @NotNull(message = ErrorMessage.INVALID_ARRAY_IS_REQUIRED)
    @Size(min = 1, max = 100, message = ErrorMessage.INVALID_ARRAY_LENGTH)
    private Set<BookRequestDto> bookRequestDtos = new HashSet<>(); // Danh sách sách
}
