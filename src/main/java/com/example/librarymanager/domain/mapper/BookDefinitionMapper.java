package com.example.librarymanager.domain.mapper;

import com.example.librarymanager.domain.dto.request.BookDefinitionRequestDto;
import com.example.librarymanager.domain.entity.BookDefinition;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookDefinitionMapper {
    BookDefinition toBookDefinition(BookDefinitionRequestDto requestDto);
}
