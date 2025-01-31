package com.example.librarymanager.service;

import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.pagination.PaginationResponseDto;
import com.example.librarymanager.domain.dto.request.ImportReceiptRequestDto;
import com.example.librarymanager.domain.dto.response.ImportReceiptResponseDto;

public interface ImportReceiptService {
    String generateReceiptNumber();

    CommonResponseDto save(ImportReceiptRequestDto requestDto, String userId);

    CommonResponseDto update(Long id, ImportReceiptRequestDto requestDto, String userId);

    CommonResponseDto delete(Long id, String userId);

    PaginationResponseDto<ImportReceiptResponseDto> findAll(PaginationFullRequestDto requestDto);

    ImportReceiptResponseDto findById(Long id);
}