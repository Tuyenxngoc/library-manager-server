package com.example.librarymanager.domain.entity;

import com.example.librarymanager.domain.entity.common.DateAuditing;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reviews")
public class Review extends DateAuditing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int rating;  // 1-5 sao

    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reader_id", foreignKey = @ForeignKey(name = "FK_REVIEW_READER_ID"), referencedColumnName = "reader_id", nullable = false)
    @JsonIgnore
    private Reader reader;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_definition_id", foreignKey = @ForeignKey(name = "FK_REVIEW_BOOK_DEFINITION_ID"), referencedColumnName = "book_definition_id", nullable = false)
    @JsonIgnore
    private BookDefinition bookDefinition;

}
