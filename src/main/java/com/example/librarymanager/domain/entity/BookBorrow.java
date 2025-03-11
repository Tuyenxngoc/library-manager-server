package com.example.librarymanager.domain.entity;

import com.example.librarymanager.constant.BookBorrowStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "book_borrows")
public class BookBorrow {//Sách mượn chi tiết

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_borrow_id")
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private BookBorrowStatus status = BookBorrowStatus.NOT_RETURNED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", foreignKey = @ForeignKey(name = "FK_BOOK_BORROWS_BOOK_ID"), referencedColumnName = "book_id", nullable = false)
    @JsonIgnore
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrow_receipt_id", foreignKey = @ForeignKey(name = "FK_BOOK_BORROWS_BORROWS_RECEIPT_ID"), referencedColumnName = "borrow_receipt_id", nullable = false)
    @JsonIgnore
    private BorrowReceipt borrowReceipt;

}
