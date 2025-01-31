package com.example.librarymanager.controller;

import com.example.librarymanager.annotation.CurrentUser;
import com.example.librarymanager.annotation.RestApiV1;
import com.example.librarymanager.base.VsResponseUtil;
import com.example.librarymanager.constant.UrlConstant;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.request.UserGroupRequestDto;
import com.example.librarymanager.security.CustomUserDetails;
import com.example.librarymanager.service.UserGroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "User Group")
public class UserGroupController {

    UserGroupService userGroupService;

    @Operation(summary = "API Create User Group")
    @PreAuthorize("hasRole('ROLE_MANAGE_USER_GROUP')")
    @PostMapping(UrlConstant.UserGroup.CREATE)
    public ResponseEntity<?> createUserGroup(
            @Valid @RequestBody UserGroupRequestDto requestDto,
            @CurrentUser CustomUserDetails userDetails
    ) {
        return VsResponseUtil.success(HttpStatus.CREATED, userGroupService.save(requestDto, userDetails.getUserId()));
    }

    @Operation(summary = "API Update User Group")
    @PreAuthorize("hasRole('ROLE_MANAGE_USER_GROUP')")
    @PutMapping(UrlConstant.UserGroup.UPDATE)
    public ResponseEntity<?> updateUserGroup(
            @PathVariable Long id,
            @Valid @RequestBody UserGroupRequestDto requestDto,
            @CurrentUser CustomUserDetails userDetails
    ) {
        return VsResponseUtil.success(userGroupService.update(id, requestDto, userDetails.getUserId()));
    }

    @Operation(summary = "API Delete User Group")
    @PreAuthorize("hasRole('ROLE_MANAGE_USER_GROUP')")
    @DeleteMapping(UrlConstant.UserGroup.DELETE)
    public ResponseEntity<?> deleteUserGroup(
            @PathVariable Long id,
            @CurrentUser CustomUserDetails userDetails
    ) {
        return VsResponseUtil.success(userGroupService.delete(id, userDetails.getUserId()));
    }

    @Operation(summary = "API Get All User Groups")
    @PreAuthorize("hasRole('ROLE_MANAGE_USER_GROUP')")
    @GetMapping(UrlConstant.UserGroup.GET_ALL)
    public ResponseEntity<?> getAllUserGroups(@ParameterObject PaginationFullRequestDto requestDto) {
        return VsResponseUtil.success(userGroupService.findAll(requestDto));
    }

    @Operation(summary = "API Get User Group By Id")
    @PreAuthorize("hasRole('ROLE_MANAGE_USER_GROUP')")
    @GetMapping(UrlConstant.UserGroup.GET_BY_ID)
    public ResponseEntity<?> getUserGroupById(@PathVariable Long id) {
        return VsResponseUtil.success(userGroupService.findById(id));
    }

    @Operation(summary = "API Toggle Active Status of User Group")
    @PreAuthorize("hasRole('ROLE_MANAGE_USER_GROUP')")
    @PatchMapping(UrlConstant.UserGroup.TOGGLE_ACTIVE)
    public ResponseEntity<?> toggleActiveStatus(
            @PathVariable Long id,
            @CurrentUser CustomUserDetails userDetails
    ) {
        return VsResponseUtil.success(userGroupService.toggleActiveStatus(id, userDetails.getUserId()));
    }
}
