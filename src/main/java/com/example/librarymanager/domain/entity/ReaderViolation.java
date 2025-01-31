package com.example.librarymanager.domain.entity;

import com.example.librarymanager.constant.PenaltyForm;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reader_violations")
public class ReaderViolation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reader_violation_id")
    private Long id;

    @Column(name = "violation_details", nullable = false)
    private String violationDetails; // Nội dung vi phạm

    @Enumerated(EnumType.STRING)
    @Column(name = "penalty_form", nullable = false)
    private PenaltyForm penaltyForm; // Hình thức phạt

    @Column(name = "other_penalty_form")
    private String otherPenaltyForm; // Hình thức khác

    @Column(name = "penalty_date", nullable = false)
    private LocalDate penaltyDate; // Ngày phạt

    @Column(name = "end_date")
    private LocalDate endDate; // Ngày kết thúc

    @Column(name = "fine_amount")
    private Double fineAmount; // Số tiền phạt

    @Column(name = "notes")
    private String notes; // Ghi chú

    @ManyToOne
    @JoinColumn(name = "reader_id", foreignKey = @ForeignKey(name = "FK_VIOLATION_READER_ID"), referencedColumnName = "reader_id", nullable = false)
    @JsonIgnore
    private Reader reader;
}
