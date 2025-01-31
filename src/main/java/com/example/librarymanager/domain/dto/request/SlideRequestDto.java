package com.example.librarymanager.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SlideRequestDto {
    private String title = "";

    private String description = "";

    private boolean activeFlag = true;
}
