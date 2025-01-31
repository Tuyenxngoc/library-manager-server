package com.example.librarymanager.service;

import com.example.librarymanager.domain.dto.request.CreateBorrowReceiptRequestDto;
import com.example.librarymanager.domain.dto.request.CreateReaderCardsRequestDto;
import com.example.librarymanager.domain.entity.Book;
import com.example.librarymanager.domain.entity.BorrowReceipt;
import com.example.librarymanager.domain.entity.Reader;
import com.example.librarymanager.domain.entity.User;

import java.util.List;

public interface PdfService {
    byte[] createReaderCard(CreateReaderCardsRequestDto requestDto, List<Reader> readers);

    byte[] createReceipt(User user, CreateBorrowReceiptRequestDto requestDto, List<BorrowReceipt> borrowReceipts);

    byte[] createReceiptWithFourPerPage(CreateBorrowReceiptRequestDto requestDto);

    byte[] createPdfFromBooks(List<Book> books);

    byte[] createLabelType1Pdf(List<Book> books);

    byte[] createLabelType2Pdf(List<Book> books);

    byte[] createBookListPdf(List<Book> books);

    byte[] createOverdueListPdf(List<BorrowReceipt> borrowReceipts);
}
