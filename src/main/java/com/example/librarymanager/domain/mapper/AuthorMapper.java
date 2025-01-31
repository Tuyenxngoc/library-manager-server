package com.example.librarymanager.domain.mapper;

import com.example.librarymanager.domain.dto.request.AuthorRequestDto;
import com.example.librarymanager.domain.entity.Author;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    Author toAuthor(AuthorRequestDto requestDto);

}
