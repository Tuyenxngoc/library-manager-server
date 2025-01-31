package com.example.librarymanager.domain.mapper;

import com.example.librarymanager.domain.dto.request.ImportReceiptRequestDto;
import com.example.librarymanager.domain.entity.ImportReceipt;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ImportReceiptMapper {
    ImportReceipt toImportReceipt(ImportReceiptRequestDto requestDto);
}
