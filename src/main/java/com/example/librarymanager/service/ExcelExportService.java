package com.example.librarymanager.service;

import com.example.librarymanager.domain.entity.BorrowReceipt;

import java.io.IOException;
import java.util.List;

public interface ExcelExportService {
    byte[] createBorrowingReport(List<BorrowReceipt> borrowReceipts) throws IOException;
}
