package com.example.librarymanager.domain.mapper;

import com.example.librarymanager.domain.dto.request.UserRequestDto;
import com.example.librarymanager.domain.dto.request.auth.RegisterRequestDto;
import com.example.librarymanager.domain.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(RegisterRequestDto requestDto);

    User toUser(UserRequestDto requestDto);
}
