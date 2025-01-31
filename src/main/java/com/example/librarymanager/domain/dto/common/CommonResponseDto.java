package com.example.librarymanager.domain.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponseDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean status;

    private String message;

    private Object data;

    public CommonResponseDto(String message) {
        this.status = null;
        this.message = message;
    }

    public CommonResponseDto(String message, Object data) {
        this.status = null;
        this.message = message;
        this.data = data;
    }

}
