package com.example.librarymanager.domain.dto.request;

import com.example.librarymanager.constant.BookStatus;
import com.example.librarymanager.constant.ErrorMessage;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookReturnRequestDto {
    @NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED)
    private Long bookBorrowId;

    private BookStatus bookStatus;
}
