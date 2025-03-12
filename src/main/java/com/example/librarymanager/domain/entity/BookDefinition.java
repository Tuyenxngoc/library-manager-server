package com.example.librarymanager.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "book_definitions",
        uniqueConstraints = @UniqueConstraint(name = "UN_BOOK_DEFINITIONS_BOOK_NUMBER", columnNames = "book_number"))
public class BookDefinition {//Biên mục

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_definition_id")
    @EqualsAndHashCode.Include
    private Long id;  // Mã biên mục

    @Column(name = "title", nullable = false)
    private String title;  // Nhan đề của biên mục

    @Column(name = "book_number", nullable = false)
    private String bookNumber; // Kí hiệu tên sách

    @Column(name = "publishing_year")
    private Integer publishingYear; // Năm xuất bản

    @Column(name = "price")
    private Double price; // Giá bán

    @Column(name = "edition")
    private String edition; // Lần xuất bản

    @Column(name = "reference_price")
    private Double referencePrice; // Giá tham khảo

    @Column(name = "publication_place")
    private String publicationPlace; // Nơi xuất bản Hà Nội, vv

    @Column(name = "page_count")
    private Integer pageCount; // Số trang

    @Column(name = "book_size")
    private String bookSize; // Khổ sách (cm)

    @Column(name = "parallel_title")
    private String parallelTitle; // Nhan đề song song

    @Lob
    @Column(name = "summary")
    private String summary; // Tóm tắt

    @Column(name = "subtitle")
    private String subtitle; // Phụ đề

    @Column(name = "additional_material")
    private String additionalMaterial; // Tài liệu đi kèm

    @Column(name = "keywords")
    private String keywords; // Từ khóa tìm kiếm

    @Column(name = "isbn")
    private String isbn; // Mã ISBN

    @Column(name = "language")
    private String language; // Ngôn ngữ

    @Column(name = "image_url")
    private String imageUrl;// Ảnh bìa

    @Column(name = "series")
    private String series; // Tùng thư

    @Column(name = "additional_info")
    private String additionalInfo; // Thông tin khác

    @Column(name = "active_flag", nullable = false)
    private Boolean activeFlag = Boolean.TRUE;

    @OneToMany(mappedBy = "bookDefinition", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Book> books = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_set_id", foreignKey = @ForeignKey(name = "FK_BOOK_DEFINITION_BOOK_SET_ID"), referencedColumnName = "book_set_id")
    @JsonIgnore
    private BookSet bookSet;//Bộ sách

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(name = "FK_BOOK_DEFINITION_CATEGORY_ID"), referencedColumnName = "category_id", nullable = false)
    @JsonIgnore
    private Category category; // Danh mục

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publisher_id", foreignKey = @ForeignKey(name = "FK_BOOK_DEFINITION_PUBLISHER_ID"), referencedColumnName = "publisher_id")
    @JsonIgnore
    private Publisher publisher;  // Nhà xuất bản

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "classification_symbol_id", foreignKey = @ForeignKey(name = "FK_BOOK_DEFINITION_CLASSIFICATION_SYMBOL_ID"), referencedColumnName = "classification_symbol_id")
    @JsonIgnore
    private ClassificationSymbol classificationSymbol;// Kí hiệu phân loại

    @OneToMany(mappedBy = "bookDefinition", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<BookAuthor> bookAuthors = new ArrayList<>();  // Tác giả

}
