package com.example.librarymanager.domain.dto.response;

import com.example.librarymanager.constant.RoleConstant;
import com.example.librarymanager.domain.entity.Role;
import lombok.Getter;

@Getter
public class RoleResponseDto {
    private final byte id;

    private final String name;

    private final RoleConstant code;

    public RoleResponseDto(Role role) {
        this.id = role.getId();
        this.name = role.getName();
        this.code = role.getCode();
    }
}
