package com.example.librarymanager.service;

import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.pagination.PaginationResponseDto;
import com.example.librarymanager.domain.dto.request.UserGroupRequestDto;
import com.example.librarymanager.domain.dto.response.UserGroupResponseDto;
import com.example.librarymanager.domain.entity.UserGroup;

public interface UserGroupService {
    UserGroup initUserGroup();

    CommonResponseDto save(UserGroupRequestDto requestDto, String userId);

    CommonResponseDto update(Long id, UserGroupRequestDto requestDto, String userId);

    CommonResponseDto delete(Long id, String userId);

    PaginationResponseDto<UserGroupResponseDto> findAll(PaginationFullRequestDto requestDto);

    UserGroupResponseDto findById(Long id);

    CommonResponseDto toggleActiveStatus(Long id, String userId);
}
