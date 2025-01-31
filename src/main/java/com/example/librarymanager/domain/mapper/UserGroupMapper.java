package com.example.librarymanager.domain.mapper;

import com.example.librarymanager.domain.dto.request.UserGroupRequestDto;
import com.example.librarymanager.domain.entity.UserGroup;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserGroupMapper {
    UserGroup toUserGroup(UserGroupRequestDto requestDto);
}
