package com.example.librarymanager.domain.dto.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookDefinitionFilter {
    private String bookCode;

    private String title;

    private String keyword;

    private String publishingYear;

    private String author;
}
