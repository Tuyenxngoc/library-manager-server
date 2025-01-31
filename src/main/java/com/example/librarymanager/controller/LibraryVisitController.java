package com.example.librarymanager.controller;

import com.example.librarymanager.annotation.RestApiV1;
import com.example.librarymanager.base.VsResponseUtil;
import com.example.librarymanager.constant.UrlConstant;
import com.example.librarymanager.domain.dto.filter.LibraryVisitFilter;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.request.LibraryVisitRequestDto;
import com.example.librarymanager.service.LibraryVisitService;
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
@Tag(name = "Library Visit")
public class LibraryVisitController {

    LibraryVisitService libraryVisitService;

    @Operation(summary = "API Create Visit")
    @PreAuthorize("hasRole('ROLE_MANAGE_READER')")
    @PostMapping(UrlConstant.LibraryVisit.CREATE)
    public ResponseEntity<?> createVisit(@Valid @RequestBody LibraryVisitRequestDto requestDto) {
        return VsResponseUtil.success(HttpStatus.CREATED, libraryVisitService.save(requestDto));
    }

    @Operation(summary = "API Update Visit")
    @PreAuthorize("hasRole('ROLE_MANAGE_READER')")
    @PutMapping(UrlConstant.LibraryVisit.UPDATE)
    public ResponseEntity<?> updateVisit(
            @PathVariable Long id,
            @Valid @RequestBody LibraryVisitRequestDto requestDto
    ) {
        return VsResponseUtil.success(libraryVisitService.update(id, requestDto));
    }

    @Operation(summary = "API Close Library - Update exit time for all readers")
    @PreAuthorize("hasRole('ROLE_MANAGE_READER')")
    @PostMapping(UrlConstant.LibraryVisit.CLOSE)
    public ResponseEntity<?> closeLibrary() {
        return VsResponseUtil.success(libraryVisitService.closeLibrary());
    }

    @Operation(summary = "API Get Visits")
    @PreAuthorize("hasRole('ROLE_MANAGE_READER')")
    @GetMapping(UrlConstant.LibraryVisit.GET_ALL)
    public ResponseEntity<?> getVisits(
            @ParameterObject PaginationFullRequestDto requestDto,
            @ParameterObject LibraryVisitFilter filter
    ) {
        return VsResponseUtil.success(libraryVisitService.findAll(requestDto, filter));
    }

    @Operation(summary = "API Get Visit By Id")
    @PreAuthorize("hasRole('ROLE_MANAGE_READER')")
    @GetMapping(UrlConstant.LibraryVisit.GET_BY_ID)
    public ResponseEntity<?> getVisitById(@PathVariable Long id) {
        return VsResponseUtil.success(libraryVisitService.findById(id));
    }

}
