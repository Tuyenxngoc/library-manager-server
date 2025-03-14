package com.example.librarymanager.service;

import com.example.librarymanager.domain.entity.Book;
import com.example.librarymanager.domain.entity.BorrowReceipt;
import com.example.librarymanager.domain.entity.Reader;
import com.example.librarymanager.domain.entity.User;

import java.util.List;

public interface PdfService {
    byte[] createReaderCard(String managementUnit, String schoolName, String principalName, List<Reader> readers);

    byte[] createReceipt(User user, String schoolName, List<BorrowReceipt> borrowReceipts);

    byte[] createReceiptWithFourPerPage(String schoolName);

    byte[] createPdfFromBooks(List<Book> books);

    byte[] createLabelType1Pdf(String librarySymbol, List<Book> books);

    byte[] createLabelType2Pdf(List<Book> books);

    byte[] createBookListPdf(List<Book> books);

    byte[] createOverdueListPdf(List<BorrowReceipt> borrowReceipts);
}
