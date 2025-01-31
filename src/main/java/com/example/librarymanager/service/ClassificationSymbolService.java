package com.example.librarymanager.service;

import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.pagination.PaginationResponseDto;
import com.example.librarymanager.domain.dto.request.ClassificationSymbolRequestDto;
import com.example.librarymanager.domain.entity.ClassificationSymbol;

public interface ClassificationSymbolService {
    void initClassificationSymbolsFromCsv(String classificationSymbolsCsvPath);

    CommonResponseDto save(ClassificationSymbolRequestDto requestDto);

    CommonResponseDto update(Long id, ClassificationSymbolRequestDto requestDto);

    CommonResponseDto delete(Long id);

    PaginationResponseDto<ClassificationSymbol> findAll(PaginationFullRequestDto requestDto);

    ClassificationSymbol findById(Long id);

    CommonResponseDto toggleActiveStatus(Long id);
}
