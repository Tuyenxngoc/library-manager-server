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
@Table(name = "publishers",
        uniqueConstraints = @UniqueConstraint(name = "UN_PUBLISHER_CODE", columnNames = "code"))
public class Publisher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "publisher_id")
    private Long id;  // Mã nhà xuất bản

    @Column(name = "code", nullable = false)
    private String code;  // Mã hiệu

    @Column(name = "name", nullable = false)
    private String name;  // Tên nhà xuất bản

    @Column(name = "address")
    private String address;  // Địa chỉ nhà xuất bản

    @Column(name = "city")
    private String city;  // Tỉnh/Thành phố

    @Column(name = "notes")
    private String notes;  // Ghi chú

    @Column(name = "active_flag", nullable = false)
    private Boolean activeFlag = Boolean.TRUE;

    @OneToMany(mappedBy = "publisher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<BookDefinition> bookDefinitions = new ArrayList<>();

}
