package com.example.librarymanager.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "import_receipts",
        uniqueConstraints = @UniqueConstraint(name = "UN_IMPORT_RECEIPTS_RECEIPT_NUMBER", columnNames = "receipt_number"))
public class ImportReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "import_receipt_id")
    private Long id;

    @Column(name = "receipt_number", nullable = false)
    private String receiptNumber;  // Số phiếu nhập

    @Column(name = "import_date", nullable = false)
    private LocalDate importDate;  // Ngày nhập

    @Column(name = "general_record_number", nullable = false)
    private String generalRecordNumber;  // Số vào sổ tổng quát

    @Column(name = "funding_source")
    private String fundingSource;  // Nguồn cấp

    @Column(name = "import_reason")
    private String importReason;  // Lý do nhập

    @OneToMany(mappedBy = "importReceipt", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Book> books = new ArrayList<>();  // Sách đã nhập

}
