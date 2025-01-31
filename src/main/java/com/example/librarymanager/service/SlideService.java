package com.example.librarymanager.service;

import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.request.SlideRequestDto;
import com.example.librarymanager.domain.dto.response.SlideResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SlideService {
    CommonResponseDto addSlide(SlideRequestDto slideRequestDto, MultipartFile image, String userId);

    CommonResponseDto updateSlide(String id, SlideRequestDto slideRequestDto, MultipartFile image, String userId);

    CommonResponseDto deleteSlide(String id, String userId);

    List<SlideResponseDto> getAllSlides(Boolean activeFlag);

    SlideResponseDto getSlideById(String id);

    CommonResponseDto toggleActiveStatus(String id, String userId);
}
