package com.example.librarymanager.service;

import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.pagination.PaginationResponseDto;
import com.example.librarymanager.domain.dto.request.CategoryGroupRequestDto;
import com.example.librarymanager.domain.dto.response.category.CategoryGroupTree;
import com.example.librarymanager.domain.entity.CategoryGroup;

import java.util.List;

public interface CategoryGroupService {
    void initCategoryGroupsFromCsv(String categoryGroupsCsvPath);

    CommonResponseDto save(CategoryGroupRequestDto requestDto, String userId);

    CommonResponseDto update(Long id, CategoryGroupRequestDto requestDto, String userId);

    CommonResponseDto delete(Long id, String userId);

    PaginationResponseDto<CategoryGroup> findAll(PaginationFullRequestDto requestDto);

    CategoryGroup findById(Long id);

    CommonResponseDto toggleActiveStatus(Long id, String userId);

    List<CategoryGroupTree> findTree();
}