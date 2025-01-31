package com.example.librarymanager.domain.dto.request;

import com.example.librarymanager.constant.ErrorMessage;
import com.example.librarymanager.constant.PenaltyForm;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class ReaderViolationRequestDto {
    @NotBlank(message = ErrorMessage.INVALID_NOT_BLANK_FIELD)
    @Size(max = 100, message = ErrorMessage.INVALID_TEXT_LENGTH)
    private String violationDetails; // Nội dung vi phạm

    @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
    private PenaltyForm penaltyForm; // Hình thức phạt

    @Size(max = 100, message = ErrorMessage.INVALID_TEXT_LENGTH)
    private String otherPenaltyForm; // Hình thức khác (có thể là tùy chọn)

    @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
    private LocalDate penaltyDate; // Ngày phạt

    private LocalDate endDate; // Ngày kết thúc (nếu có)

    @Min(value = 0, message = ErrorMessage.INVALID_MINIMUM_ZERO)
    private Double fineAmount; // Số tiền phạt

    @Size(max = 255, message = ErrorMessage.INVALID_TEXT_LENGTH)
    private String notes; // Ghi chú

    @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
    private Long readerId; // ID của bạn đọc liên kết với vi phạm
}
