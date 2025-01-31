package com.example.librarymanager.service;

import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.request.LibraryConfigRequestDto;
import com.example.librarymanager.domain.dto.request.LibraryInfoRequestDto;
import com.example.librarymanager.domain.dto.request.LibraryRulesRequestDto;
import com.example.librarymanager.domain.dto.response.LibraryConfigResponseDto;
import com.example.librarymanager.domain.dto.response.LibraryInfoResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface SystemSettingService {
    CommonResponseDto updateLibraryRules(LibraryRulesRequestDto requestDto, String userId);

    String getLibraryRules();

    LibraryConfigResponseDto getLibraryConfig();

    CommonResponseDto updateLibraryConfig(LibraryConfigRequestDto requestDto, String userId);

    LibraryInfoResponseDto getLibraryInfo();

    CommonResponseDto updateLibraryInfo(LibraryInfoRequestDto requestDto, MultipartFile logo, String userId);
}
