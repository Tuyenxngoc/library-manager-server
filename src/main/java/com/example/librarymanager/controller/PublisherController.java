package com.example.librarymanager.controller;

import com.example.librarymanager.annotation.RestApiV1;
import com.example.librarymanager.base.VsResponseUtil;
import com.example.librarymanager.constant.UrlConstant;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.request.PublisherRequestDto;
import com.example.librarymanager.service.PublisherService;
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
@Tag(name = "Publisher")
public class PublisherController {

    PublisherService publisherService;

    @Operation(summary = "API Create Publisher")
    @PreAuthorize("hasRole('ROLE_MANAGE_PUBLISHER')")
    @PostMapping(UrlConstant.Publisher.CREATE)
    public ResponseEntity<?> createPublisher(@Valid @RequestBody PublisherRequestDto requestDto) {
        return VsResponseUtil.success(HttpStatus.CREATED, publisherService.save(requestDto));
    }

    @Operation(summary = "API Update Publisher")
    @PreAuthorize("hasRole('ROLE_MANAGE_PUBLISHER')")
    @PutMapping(UrlConstant.Publisher.UPDATE)
    public ResponseEntity<?> updatePublisher(
            @PathVariable Long id,
            @Valid @RequestBody PublisherRequestDto requestDto
    ) {
        return VsResponseUtil.success(publisherService.update(id, requestDto));
    }

    @Operation(summary = "API Delete Publisher")
    @PreAuthorize("hasRole('ROLE_MANAGE_PUBLISHER')")
    @DeleteMapping(UrlConstant.Publisher.DELETE)
    public ResponseEntity<?> deletePublisher(@PathVariable Long id) {
        return VsResponseUtil.success(publisherService.delete(id));
    }

    @Operation(summary = "API Get All Publishers")
    @PreAuthorize("hasAnyRole('ROLE_MANAGE_PUBLISHER', 'ROLE_MANAGE_BOOK_DEFINITION')")
    @GetMapping(UrlConstant.Publisher.GET_ALL)
    public ResponseEntity<?> getAllPublishers(@ParameterObject PaginationFullRequestDto requestDto) {
        return VsResponseUtil.success(publisherService.findAll(requestDto));
    }

    @Operation(summary = "API Get Publisher By Id")
    @PreAuthorize("hasRole('ROLE_MANAGE_PUBLISHER')")
    @GetMapping(UrlConstant.Publisher.GET_BY_ID)
    public ResponseEntity<?> getPublisherById(@PathVariable Long id) {
        return VsResponseUtil.success(publisherService.findById(id));
    }

    @Operation(summary = "API Toggle Active Status of Publisher")
    @PreAuthorize("hasRole('ROLE_MANAGE_PUBLISHER')")
    @PatchMapping(UrlConstant.Publisher.TOGGLE_ACTIVE)
    public ResponseEntity<?> toggleActiveStatus(@PathVariable Long id) {
        return VsResponseUtil.success(publisherService.toggleActiveStatus(id));
    }
}
