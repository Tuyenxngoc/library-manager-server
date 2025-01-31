package com.example.librarymanager.domain.mapper;

import com.example.librarymanager.domain.dto.request.ReaderRequestDto;
import com.example.librarymanager.domain.entity.Reader;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReaderMappper {
    Reader toReader(ReaderRequestDto requestDto);
}
