package com.example.librarymanager.service;

import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.pagination.PaginationResponseDto;
import com.example.librarymanager.domain.dto.request.AuthorRequestDto;
import com.example.librarymanager.domain.entity.Author;

public interface AuthorService {

    void initAuthorsFromCsv(String authorsCsvPath);

    CommonResponseDto save(AuthorRequestDto requestDto, String userId);

    CommonResponseDto update(Long id, AuthorRequestDto requestDto, String userId);

    CommonResponseDto delete(Long id, String userId);

    PaginationResponseDto<Author> findAll(PaginationFullRequestDto requestDto);

    Author findById(Long id);

    CommonResponseDto toggleActiveStatus(Long id, String userId);
}