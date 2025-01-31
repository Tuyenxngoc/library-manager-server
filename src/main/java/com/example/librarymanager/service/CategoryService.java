package com.example.librarymanager.service;

import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.pagination.PaginationResponseDto;
import com.example.librarymanager.domain.dto.request.CategoryRequestDto;
import com.example.librarymanager.domain.dto.response.category.CategoryResponseDto;
import com.example.librarymanager.domain.entity.Category;

public interface CategoryService {
    void initCategoriesFromCsv(String categoriesCsvPath);

    CommonResponseDto save(CategoryRequestDto requestDto, String userId);

    CommonResponseDto update(Long id, CategoryRequestDto requestDto, String userId);

    CommonResponseDto delete(Long id, String userId);

    PaginationResponseDto<CategoryResponseDto> findAll(PaginationFullRequestDto requestDto);

    Category findById(Long id);

    CommonResponseDto toggleActiveStatus(Long id, String userId);
}