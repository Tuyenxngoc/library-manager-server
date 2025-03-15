package com.example.librarymanager.service;

import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.pagination.PaginationResponseDto;
import com.example.librarymanager.domain.dto.request.UserRequestDto;
import com.example.librarymanager.domain.dto.response.UserResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    void init();

    CommonResponseDto save(UserRequestDto requestDto, String userId);

    CommonResponseDto update(String id, UserRequestDto requestDto, String userId);

    CommonResponseDto delete(String id, String userId);

    PaginationResponseDto<UserResponseDto> findAll(PaginationFullRequestDto requestDto);

    UserResponseDto findById(String id);

    List<String> uploadImages(List<MultipartFile> files, String userId);
}
