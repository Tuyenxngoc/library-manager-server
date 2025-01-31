package com.example.librarymanager.service;

import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.pagination.PaginationResponseDto;
import com.example.librarymanager.domain.dto.request.PublisherRequestDto;
import com.example.librarymanager.domain.entity.Publisher;

public interface PublisherService {
    void initPublishersFromCsv(String publishersCsvPath);

    CommonResponseDto save(PublisherRequestDto requestDto);

    CommonResponseDto update(Long id, PublisherRequestDto requestDto);

    CommonResponseDto delete(Long id);

    PaginationResponseDto<Publisher> findAll(PaginationFullRequestDto requestDto);

    Publisher findById(Long id);

    CommonResponseDto toggleActiveStatus(Long id);
}