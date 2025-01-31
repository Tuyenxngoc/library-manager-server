package com.example.librarymanager.controller;

import com.example.librarymanager.annotation.CurrentUser;
import com.example.librarymanager.annotation.RestApiV1;
import com.example.librarymanager.base.VsResponseUtil;
import com.example.librarymanager.constant.UrlConstant;
import com.example.librarymanager.domain.dto.request.auth.*;
import com.example.librarymanager.security.CustomUserDetails;
import com.example.librarymanager.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Auth")
public class AuthController {

    AuthService authService;

    @Operation(summary = "API Login")
    @PostMapping(UrlConstant.Auth.LOGIN)
    public ResponseEntity<?> login(@Valid @RequestBody ReaderLoginRequestDto request) {
        return VsResponseUtil.success(authService.readerLogin(request));
    }

    @Operation(summary = "API Admin Login")
    @PostMapping(UrlConstant.Auth.ADMIN_LOGIN)
    public ResponseEntity<?> adminLogin(@Valid @RequestBody AdminLoginRequestDto request) {
        return VsResponseUtil.success(authService.adminLogin(request));
    }

    @Operation(summary = "API Logout")
    @PostMapping(UrlConstant.Auth.LOGOUT)
    public ResponseEntity<?> logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        return VsResponseUtil.success(authService.logout(request, response, authentication));
    }

    @Operation(summary = "API Refresh token")
    @PostMapping(UrlConstant.Auth.REFRESH_TOKEN)
    public ResponseEntity<?> refresh(@Valid @RequestBody TokenRefreshRequestDto tokenRefreshRequestDto) {
        return VsResponseUtil.success(authService.refresh(tokenRefreshRequestDto));
    }

    @Operation(summary = "API Admin forget password")
    @PostMapping(UrlConstant.Auth.ADMIN_FORGET_PASSWORD)
    public ResponseEntity<?> adminForgetPassword(@Valid @RequestBody AdminForgetPasswordRequestDto requestDto) {
        return VsResponseUtil.success(authService.adminForgetPassword(requestDto));
    }

    @Operation(summary = "API Admin change password")
    @PatchMapping(UrlConstant.Auth.ADMIN_CHANGE_PASSWORD)
    public ResponseEntity<?> adminChangePassword(
            @Valid @RequestBody ChangePasswordRequestDto requestDto,
            @CurrentUser CustomUserDetails userDetails
    ) {
        return VsResponseUtil.success(authService.adminChangePassword(requestDto, userDetails.getUsername()));
    }

    @Operation(summary = "API forget password")
    @PostMapping(UrlConstant.Auth.FORGET_PASSWORD)
    public ResponseEntity<?> forgetPassword(@Valid @RequestBody ReaderForgetPasswordRequestDto requestDto) {
        return VsResponseUtil.success(authService.forgetPassword(requestDto));
    }

    @Operation(summary = "API change password")
    @PatchMapping(UrlConstant.Auth.CHANGE_PASSWORD)
    public ResponseEntity<?> changePassword(
            @Valid @RequestBody ChangePasswordRequestDto requestDto,
            @CurrentUser CustomUserDetails userDetails
    ) {
        return VsResponseUtil.success(authService.changePassword(requestDto, userDetails.getCardNumber()));
    }
}
