package com.example.librarymanager.domain.mapper;

import com.example.librarymanager.domain.dto.request.ClassificationSymbolRequestDto;
import com.example.librarymanager.domain.entity.ClassificationSymbol;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClassificationSymbolMapper {
    ClassificationSymbol toClassificationSymbol(ClassificationSymbolRequestDto requestDto);
}
