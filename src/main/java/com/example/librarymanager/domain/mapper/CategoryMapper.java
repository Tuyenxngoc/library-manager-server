package com.example.librarymanager.domain.mapper;

import com.example.librarymanager.domain.dto.request.CategoryRequestDto;
import com.example.librarymanager.domain.entity.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toCategory(CategoryRequestDto requestDto);
}
