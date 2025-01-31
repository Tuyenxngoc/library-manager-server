package com.example.librarymanager.domain.dto.response;

import com.example.librarymanager.domain.entity.ExportReceipt;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ExportReceiptResponseDto {

    private final long id;

    private final String receiptNumber;

    private final LocalDate exportDate;

    private final String exportReason;

    private final List<Long> bookIds = new ArrayList<>();

    public ExportReceiptResponseDto(ExportReceipt exportReceipt) {
        this.id = exportReceipt.getId();
        this.receiptNumber = exportReceipt.getReceiptNumber();
        this.exportDate = exportReceipt.getExportDate();
        this.exportReason = exportReceipt.getExportReason();
    }
}
