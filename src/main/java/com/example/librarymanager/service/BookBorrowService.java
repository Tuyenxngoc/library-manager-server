package com.example.librarymanager.service;

import com.example.librarymanager.constant.BookBorrowStatus;
import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.filter.TimeFilter;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.pagination.PaginationResponseDto;
import com.example.librarymanager.domain.dto.request.BookReturnRequestDto;
import com.example.librarymanager.domain.dto.response.bookborrow.BookBorrowResponseDto;

import java.util.List;
import java.util.Set;

public interface BookBorrowService {

    PaginationResponseDto<BookBorrowResponseDto> findAll(PaginationFullRequestDto requestDto, TimeFilter timeFilter, List<BookBorrowStatus> status);

    CommonResponseDto returnBooks(List<BookReturnRequestDto> requestDtos, String userId);

    CommonResponseDto reportLostBooksByIds(Set<Long> ids, String userId);

}