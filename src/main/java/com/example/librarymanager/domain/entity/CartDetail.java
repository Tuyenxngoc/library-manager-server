package com.example.librarymanager.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cart_detail")
public class CartDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_detail_id")
    private Long id;

    // Đăng ký mượn từ
    @Column(name = "borrow_from", nullable = false)
    private LocalDateTime borrowFrom;

    // Đến
    @Column(name = "borrow_to", nullable = false)
    private LocalDateTime borrowTo;

    @ManyToOne
    @JoinColumn(name = "book_id", foreignKey = @ForeignKey(name = "FK_CART_DETAIL_PRODUCT_ID"), referencedColumnName = "book_id", nullable = false)
    @JsonIgnore
    private Book book;

    @ManyToOne
    @JoinColumn(name = "cart_id", foreignKey = @ForeignKey(name = "FK_CART_DETAIL_CART_ID"), referencedColumnName = "cart_id", nullable = false)
    @JsonIgnore
    private Cart cart;
}
