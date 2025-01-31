package com.example.librarymanager.controller;

import com.example.librarymanager.annotation.RestApiV1;
import com.example.librarymanager.base.VsResponseUtil;
import com.example.librarymanager.constant.UrlConstant;
import com.example.librarymanager.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;

@RestApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Role")
public class RoleController {

    RoleService roleService;

    @Operation(summary = "API get roles")
    @PreAuthorize("hasRole('ROLE_MANAGE_USER_GROUP')")
    @GetMapping(UrlConstant.Role.GET_ALL)
    public ResponseEntity<?> getRoles() {
        return VsResponseUtil.success(roleService.getRoles());
    }

}
