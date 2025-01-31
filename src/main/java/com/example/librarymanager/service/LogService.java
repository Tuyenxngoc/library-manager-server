package com.example.librarymanager.service;

import com.example.librarymanager.domain.dto.filter.LogFilter;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.pagination.PaginationResponseDto;
import com.example.librarymanager.domain.dto.response.LogResponseDto;

public interface LogService {
    PaginationResponseDto<LogResponseDto> findAll(PaginationFullRequestDto requestDto, LogFilter logFilter);

    void createLog(String feature, String event, String content, String userId);
}