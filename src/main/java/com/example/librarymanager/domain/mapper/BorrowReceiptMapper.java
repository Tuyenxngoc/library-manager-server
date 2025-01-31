package com.example.librarymanager.domain.mapper;

import com.example.librarymanager.domain.dto.request.BorrowReceiptRequestDto;
import com.example.librarymanager.domain.entity.BorrowReceipt;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BorrowReceiptMapper {

    BorrowReceipt toBorrowReceipt(BorrowReceiptRequestDto requestDto);

}
