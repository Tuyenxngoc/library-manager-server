package com.example.librarymanager.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "classification_symbols",
        uniqueConstraints = @UniqueConstraint(name = "UN_CLASSIFICATION_SYMBOLS_CODE", columnNames = "code"))
public class ClassificationSymbol {//Kí hiệu phân loại sách

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "classification_symbol_id")
    private Long id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "level")
    private Integer level;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "active_flag", nullable = false)
    private Boolean activeFlag = Boolean.TRUE;

    @OneToMany(mappedBy = "classificationSymbol", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<BookDefinition> bookDefinitions = new ArrayList<>();

}
