package com.example.librarymanager.service.impl;

import com.example.librarymanager.domain.entity.BorrowReceipt;
import com.example.librarymanager.service.ExcelExportService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExcelExportServiceImpl implements ExcelExportService {

    @Override
    public byte[] createBorrowingReport(List<BorrowReceipt> borrowReceipts) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Sheet1");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"Số phiếu", "Mã thẻ", "Bạn đọc", "Loại thẻ", "Ngày mượn", "Ngày hẹn trả", "Số lượng sách", "Trạng thái"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            CellStyle style = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            style.setFont(font);
            cell.setCellStyle(style);
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        int rowIdx = 1;
        for (BorrowReceipt borrowReceipt : borrowReceipts) {
            Row row = sheet.createRow(rowIdx++);

            Cell cell0 = row.createCell(0);
            cell0.setCellValue(borrowReceipt.getReceiptNumber());

            Cell cell1 = row.createCell(1);  // Mã thẻ
            cell1.setCellValue(borrowReceipt.getReader().getCardNumber());

            Cell cell2 = row.createCell(2);  // Bạn đọc
            cell2.setCellValue(borrowReceipt.getReader().getFullName());

            Cell cell3 = row.createCell(3);  // Loại thẻ
            cell3.setCellValue(borrowReceipt.getReader().getCardType().getDisplayName());

            Cell cell4 = row.createCell(4);  // Ngày mượn
            cell4.setCellValue(borrowReceipt.getBorrowDate().format(dateFormatter));

            Cell cell5 = row.createCell(5);  // Ngày hẹn trả
            cell5.setCellValue(borrowReceipt.getDueDate().format(dateFormatter));

            Cell cell6 = row.createCell(6);  // Số lượng sách
            cell6.setCellValue(borrowReceipt.getBookBorrows().size());

            Cell cell7 = row.createCell(7);  // Trạng thái
            cell7.setCellValue(borrowReceipt.getStatus().getName());
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return out.toByteArray();
    }
}
