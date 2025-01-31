package com.example.librarymanager.service;

import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.filter.LibraryVisitFilter;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.pagination.PaginationResponseDto;
import com.example.librarymanager.domain.dto.request.LibraryVisitRequestDto;
import com.example.librarymanager.domain.dto.response.LibraryVisitResponseDto;

public interface LibraryVisitService {
    CommonResponseDto save(LibraryVisitRequestDto requestDto);

    CommonResponseDto update(Long id, LibraryVisitRequestDto requestDto);

    PaginationResponseDto<LibraryVisitResponseDto> findAll(PaginationFullRequestDto requestDto, LibraryVisitFilter filter);

    LibraryVisitResponseDto findById(Long id);

    CommonResponseDto closeLibrary();
}