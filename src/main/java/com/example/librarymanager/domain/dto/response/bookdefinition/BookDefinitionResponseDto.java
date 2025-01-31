package com.example.librarymanager.domain.dto.response.bookdefinition;

import com.example.librarymanager.domain.dto.common.BaseEntityDto;
import com.example.librarymanager.domain.entity.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class BookDefinitionResponseDto {

    private final long id;

    private final String title;

    private final Double price;

    private final String isbn;

    private final String publishingYear;

    private final String edition;

    private final Double referencePrice;

    private final String publicationPlace;

    private final String bookCode;

    private final Integer pageCount;

    private final String bookSize;

    private final String parallelTitle;

    private final String summary;

    private final String subtitle;

    private final String additionalMaterial;

    private final String keywords;

    private final String language;

    private final String imageUrl;

    private final String series;

    private final String additionalInfo;

    private final Boolean activeFlag;

    private final List<BaseEntityDto> authors = new ArrayList<>();

    private final BaseEntityDto publisher;

    private final BaseEntityDto bookSet;

    private final BaseEntityDto category;

    private final BaseEntityDto classificationSymbol;

    public BookDefinitionResponseDto(BookDefinition bookDefinition) {
        this.id = bookDefinition.getId();
        this.title = bookDefinition.getTitle();
        this.price = bookDefinition.getPrice();
        this.isbn = bookDefinition.getIsbn();
        this.publishingYear = bookDefinition.getPublishingYear();
        this.edition = bookDefinition.getEdition();
        this.referencePrice = bookDefinition.getReferencePrice();
        this.publicationPlace = bookDefinition.getPublicationPlace();
        this.bookCode = bookDefinition.getBookCode();
        this.pageCount = bookDefinition.getPageCount();
        this.bookSize = bookDefinition.getBookSize();
        this.parallelTitle = bookDefinition.getParallelTitle();
        this.summary = bookDefinition.getSummary();
        this.subtitle = bookDefinition.getSubtitle();
        this.additionalMaterial = bookDefinition.getAdditionalMaterial();
        this.keywords = bookDefinition.getKeywords();
        this.language = bookDefinition.getLanguage();
        this.imageUrl = bookDefinition.getImageUrl();
        this.series = bookDefinition.getSeries();
        this.additionalInfo = bookDefinition.getAdditionalInfo();
        this.activeFlag = bookDefinition.getActiveFlag();

        // Set category
        Category c = bookDefinition.getCategory();
        this.category = c != null ? new BaseEntityDto(c.getId(), c.getCategoryName()) : null;

        // Set authors
        List<BookAuthor> au = bookDefinition.getBookAuthors();
        if (au != null) {
            this.authors.addAll(au.stream()
                    .map(BookAuthor::getAuthor)
                    .map(author -> new BaseEntityDto(author.getId(), author.getFullName()))
                    .toList());
        }

        // Set publisher
        Publisher p = bookDefinition.getPublisher();
        this.publisher = p != null ? new BaseEntityDto(p.getId(), p.getName()) : null;

        // Set bookSet
        BookSet b = bookDefinition.getBookSet();
        this.bookSet = b != null ? new BaseEntityDto(b.getId(), b.getName()) : null;

        // Set classificationSymbol
        ClassificationSymbol cl = bookDefinition.getClassificationSymbol();
        this.classificationSymbol = cl != null ? new BaseEntityDto(cl.getId(), cl.getName()) : null;
    }
}
