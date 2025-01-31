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
@Table(name = "categories",
        uniqueConstraints = {
                @UniqueConstraint(name = "UN_CATEGORY_NAME", columnNames = "category_name"),
                @UniqueConstraint(name = "UN_CATEGORY_CODE", columnNames = "category_code")
        })
public class Category {//Danh mục

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(name = "category_name", nullable = false)
    private String categoryName;

    @Column(name = "category_code", nullable = false)
    private String categoryCode;

    @Column(name = "active_flag", nullable = false)
    private Boolean activeFlag = Boolean.TRUE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_group_id", foreignKey = @ForeignKey(name = "FK_CATEGORY_CATEGORY_GROUP_ID"), referencedColumnName = "category_group_id", nullable = false)
    @JsonIgnore
    private CategoryGroup categoryGroup;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<BookDefinition> bookDefinitions = new ArrayList<>();

}
