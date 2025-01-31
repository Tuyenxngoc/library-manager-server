package com.example.librarymanager.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SlideResponseDto {
    private String id;

    private String title;

    private String description;

    private String imageUrl;

    private Boolean activeFlag;
}
