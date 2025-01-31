package com.example.librarymanager.domain.mapper;

import com.example.librarymanager.domain.dto.request.BookSetRequestDto;
import com.example.librarymanager.domain.entity.BookSet;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookSetMapper {
    BookSet toBookSet(BookSetRequestDto requestDto);
}
