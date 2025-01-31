package com.example.librarymanager.service;

import com.example.librarymanager.constant.BookCondition;
import com.example.librarymanager.constant.BookStatus;
import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.pagination.PaginationResponseDto;
import com.example.librarymanager.domain.dto.response.book.BookResponseDto;

import java.util.List;
import java.util.Set;

public interface BookService {
    CommonResponseDto updateStatus(Long id, BookStatus status, String userId);

    PaginationResponseDto<BookResponseDto> findAll(PaginationFullRequestDto requestDto, BookCondition bookCondition);

    List<BookResponseDto> findByIds(Set<Long> ids);

    List<BookResponseDto> findByCodes(Set<String> codes);

    BookResponseDto findById(Long id);

    byte[] getBooksPdfContent(Set<Long> ids);

    byte[] getBooksLabelType1PdfContent(Set<Long> ids);

    byte[] getBooksLabelType2PdfContent(Set<Long> ids);

    byte[] generateBookListPdf();
}