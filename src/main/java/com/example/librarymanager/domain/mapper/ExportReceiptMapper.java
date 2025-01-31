package com.example.librarymanager.domain.mapper;

import com.example.librarymanager.domain.dto.request.ExportReceiptRequestDto;
import com.example.librarymanager.domain.entity.ExportReceipt;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExportReceiptMapper {
    ExportReceipt toExportReceipt(ExportReceiptRequestDto requestDto);
}
