package com.example.librarymanager.service;

import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.request.auth.*;
import com.example.librarymanager.domain.dto.response.auth.LoginResponseDto;
import com.example.librarymanager.domain.dto.response.auth.TokenRefreshResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;

public interface AuthService {

    LoginResponseDto readerLogin(ReaderLoginRequestDto request);

    LoginResponseDto adminLogin(AdminLoginRequestDto request);

    CommonResponseDto logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication);

    TokenRefreshResponseDto refresh(TokenRefreshRequestDto request);

    CommonResponseDto adminForgetPassword(AdminForgetPasswordRequestDto requestDto);

    CommonResponseDto adminChangePassword(ChangePasswordRequestDto requestDto, String username);

    CommonResponseDto forgetPassword(ReaderForgetPasswordRequestDto requestDto);

    CommonResponseDto changePassword(ChangePasswordRequestDto requestDto, String cardNumber);
}
