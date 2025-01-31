package com.example.librarymanager.domain.mapper;

import com.example.librarymanager.domain.dto.request.ReaderViolationRequestDto;
import com.example.librarymanager.domain.entity.ReaderViolation;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReaderViolationMapper {
    ReaderViolation toReaderViolation(ReaderViolationRequestDto requestDto);
}
