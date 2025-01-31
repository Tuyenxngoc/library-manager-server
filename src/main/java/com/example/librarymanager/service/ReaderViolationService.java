package com.example.librarymanager.service;

import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.pagination.PaginationResponseDto;
import com.example.librarymanager.domain.dto.request.ReaderViolationRequestDto;
import com.example.librarymanager.domain.dto.response.reader.ReaderViolationResponseDto;

public interface ReaderViolationService {
    CommonResponseDto save(ReaderViolationRequestDto requestDto, String userId);

    CommonResponseDto update(Long id, ReaderViolationRequestDto requestDto, String userId);

    CommonResponseDto delete(Long id, String userId);

    PaginationResponseDto<ReaderViolationResponseDto> findAll(PaginationFullRequestDto requestDto);

    ReaderViolationResponseDto findById(Long id);
}
