package com.example.librarymanager.domain.mapper;

import com.example.librarymanager.domain.dto.request.CategoryGroupRequestDto;
import com.example.librarymanager.domain.entity.CategoryGroup;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryGroupMapper {
    CategoryGroup toCategoryGroup(CategoryGroupRequestDto requestDto);
}
