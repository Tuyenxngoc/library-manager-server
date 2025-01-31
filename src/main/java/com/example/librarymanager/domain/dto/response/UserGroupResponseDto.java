package com.example.librarymanager.domain.dto.response;

import com.example.librarymanager.domain.entity.Role;
import com.example.librarymanager.domain.entity.UserGroup;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class UserGroupResponseDto {

    private final long id;

    private final String code;

    private final String name;

    private final String notes;

    private final boolean activeFlag;

    private final long userCount;

    private final Set<RoleResponseDto> roles = new HashSet<>();

    public UserGroupResponseDto(UserGroup userGroup) {
        this.id = userGroup.getId();
        this.code = userGroup.getCode();
        this.name = userGroup.getName();
        this.notes = userGroup.getNotes();
        this.activeFlag = userGroup.getActiveFlag();
        this.userCount = userGroup.getUsers().size();
        this.roles.addAll(userGroup.getUserGroupRoles().stream().map(userGroupRole -> {
            Role role = userGroupRole.getRole();
            return new RoleResponseDto(role);
        }).collect(Collectors.toSet()));
    }
}
