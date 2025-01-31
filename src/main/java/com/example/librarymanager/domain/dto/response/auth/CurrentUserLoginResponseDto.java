package com.example.librarymanager.domain.dto.response.auth;

import com.example.librarymanager.constant.RoleConstant;
import com.example.librarymanager.domain.entity.Reader;
import com.example.librarymanager.domain.entity.User;
import com.example.librarymanager.domain.entity.UserGroupRole;
import lombok.Builder;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Builder
public class CurrentUserLoginResponseDto {

    private String name;

    private Set<RoleConstant> roleNames;

    public static CurrentUserLoginResponseDto create(User user) {
        CurrentUserLoginResponseDto responseDto = CurrentUserLoginResponseDto.builder()
                .name(user.getFullName())
                .roleNames(new HashSet<>())
                .build();

        Set<UserGroupRole> roles = user.getUserGroup().getUserGroupRoles();
        for (UserGroupRole role : roles) {
            responseDto.getRoleNames().add(role.getRole().getCode());
        }

        return responseDto;
    }

    public static CurrentUserLoginResponseDto create(Reader reader) {
        CurrentUserLoginResponseDto responseDto = CurrentUserLoginResponseDto.builder()
                .name(reader.getFullName())
                .roleNames(new HashSet<>())
                .build();
        responseDto.getRoleNames().add(RoleConstant.ROLE_READER);

        return responseDto;
    }

}
