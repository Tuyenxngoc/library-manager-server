package com.example.librarymanager.service;

import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.pagination.PaginationResponseDto;
import com.example.librarymanager.domain.dto.request.ExportReceiptRequestDto;
import com.example.librarymanager.domain.dto.response.ExportReceiptResponseDto;

public interface ExportReceiptService {
    String generateReceiptNumber();

    CommonResponseDto save(ExportReceiptRequestDto requestDto, String userId);

    CommonResponseDto update(Long id, ExportReceiptRequestDto requestDto, String userId);

    CommonResponseDto delete(Long id, String userId);

    PaginationResponseDto<ExportReceiptResponseDto> findAll(PaginationFullRequestDto requestDto);

    ExportReceiptResponseDto findById(Long id);
}
