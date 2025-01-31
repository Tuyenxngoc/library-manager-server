package com.example.librarymanager.service;

import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.pagination.PaginationResponseDto;
import com.example.librarymanager.domain.dto.request.CreateReaderCardsRequestDto;
import com.example.librarymanager.domain.dto.request.ReaderRequestDto;
import com.example.librarymanager.domain.dto.response.reader.ReaderDetailResponseDto;
import com.example.librarymanager.domain.dto.response.reader.ReaderResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface ReaderService {
    void initReadersFromCsv(String readersCsvPath);

    CommonResponseDto save(ReaderRequestDto requestDto, MultipartFile image, String userId);

    CommonResponseDto update(Long id, ReaderRequestDto requestDto, MultipartFile image, String userId);

    CommonResponseDto delete(Long id, String userId);

    PaginationResponseDto<ReaderResponseDto> findAll(PaginationFullRequestDto requestDto);

    ReaderResponseDto findById(Long id);

    ReaderResponseDto findByCardNumber(String cardNumber);

    byte[] generateReaderCards(CreateReaderCardsRequestDto requestDto);

    ReaderDetailResponseDto getReaderDetailsByCardNumber(String cardNumber);
}
