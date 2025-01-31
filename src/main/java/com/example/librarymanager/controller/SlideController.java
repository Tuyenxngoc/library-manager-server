package com.example.librarymanager.controller;

import com.example.librarymanager.annotation.CurrentUser;
import com.example.librarymanager.annotation.RestApiV1;
import com.example.librarymanager.base.VsResponseUtil;
import com.example.librarymanager.constant.UrlConstant;
import com.example.librarymanager.domain.dto.request.SlideRequestDto;
import com.example.librarymanager.security.CustomUserDetails;
import com.example.librarymanager.service.SlideService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Slide management")
public class SlideController {

    SlideService slideService;

    @Operation(summary = "API Get All Slides")
    @GetMapping(UrlConstant.SystemSetting.GET_ALL_SLIDES)
    public ResponseEntity<?> getAllSlides(@RequestParam(required = false) Boolean activeFlag) {
        return VsResponseUtil.success(slideService.getAllSlides(activeFlag));
    }

    @Operation(summary = "API Get Slide by ID")
    @GetMapping(UrlConstant.SystemSetting.GET_SLIDE_BY_ID)
    public ResponseEntity<?> getSlideById(@PathVariable String id) {
        return VsResponseUtil.success(slideService.getSlideById(id));
    }

    @Operation(summary = "API Add a New Slide")
    @PreAuthorize("hasRole('ROLE_MANAGE_SYSTEM_SETTINGS')")
    @PostMapping(value = UrlConstant.SystemSetting.ADD_SLIDE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addSlide(
            @Valid @ModelAttribute SlideRequestDto requestDto,
            @RequestParam(value = "image") MultipartFile image,
            @CurrentUser CustomUserDetails userDetails
    ) {
        return VsResponseUtil.success(HttpStatus.CREATED, slideService.addSlide(requestDto, image, userDetails.getUserId()));
    }

    @Operation(summary = "API Update Slide")
    @PreAuthorize("hasRole('ROLE_MANAGE_SYSTEM_SETTINGS')")
    @PutMapping(value = UrlConstant.SystemSetting.UPDATE_SLIDE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateSlide(
            @PathVariable String id,
            @Valid @ModelAttribute SlideRequestDto requestDto,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @CurrentUser CustomUserDetails userDetails
    ) {
        return VsResponseUtil.success(slideService.updateSlide(id, requestDto, image, userDetails.getUserId()));
    }

    @Operation(summary = "API Toggle Active Status of Author")
    @PreAuthorize("hasRole('ROLE_MANAGE_SYSTEM_SETTINGS')")
    @PatchMapping(UrlConstant.SystemSetting.TOGGLE_ACTIVE_SLIDE)
    public ResponseEntity<?> toggleActiveStatus(
            @PathVariable String id,
            @CurrentUser CustomUserDetails userDetails
    ) {
        return VsResponseUtil.success(slideService.toggleActiveStatus(id, userDetails.getUserId()));
    }

    @Operation(summary = "API Delete Slide")
    @PreAuthorize("hasRole('ROLE_MANAGE_SYSTEM_SETTINGS')")
    @DeleteMapping(UrlConstant.SystemSetting.DELETE_SLIDE)
    public ResponseEntity<?> deleteSlide(
            @PathVariable String id,
            @CurrentUser CustomUserDetails userDetails
    ) {
        return VsResponseUtil.success(slideService.deleteSlide(id, userDetails.getUserId()));
    }
}
