package com.example.librarymanager.domain.entity;

import com.example.librarymanager.constant.Gender;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "authors",
        uniqueConstraints = @UniqueConstraint(name = "UN_AUTHOR_CODE", columnNames = "code"))
public class Author {//Tác giả

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "author_id")
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName; // Họ tên

    @Column(name = "code", nullable = false, unique = true)
    private String code; // Mã hiệu

    @Column(name = "pen_name")
    private String penName; // Bí danh

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender; // Giới tính

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth; // Ngày sinh

    @Column(name = "date_of_death")
    private LocalDate dateOfDeath; // Ngày mất

    @Column(name = "title")
    private String title; // Chức danh

    @Column(name = "residence")
    private String residence; // Thường trú

    @Column(name = "address")
    private String address; // Địa chỉ

    @Column(name = "notes")
    private String notes; // Ghi chú

    @Column(name = "active_flag", nullable = false)
    private Boolean activeFlag = Boolean.TRUE;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<BookAuthor> bookAuthors;

}
