package com.example.librarymanager.controller;

import com.example.librarymanager.annotation.RestApiV1;
import com.example.librarymanager.base.VsResponseUtil;
import com.example.librarymanager.constant.UrlConstant;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.request.ClassificationSymbolRequestDto;
import com.example.librarymanager.service.ClassificationSymbolService;
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
@Tag(name = "Classification Symbol")
public class ClassificationSymbolController {

    ClassificationSymbolService classificationSymbolService;

    @Operation(summary = "API Create Classification Symbol")
    @PreAuthorize("hasRole('ROLE_MANAGE_CLASSIFICATION_SYMBOL')")
    @PostMapping(UrlConstant.ClassificationSymbol.CREATE)
    public ResponseEntity<?> createClassificationSymbol(@Valid @RequestBody ClassificationSymbolRequestDto requestDto) {
        return VsResponseUtil.success(HttpStatus.CREATED, classificationSymbolService.save(requestDto));
    }

    @Operation(summary = "API Update Classification Symbol")
    @PreAuthorize("hasRole('ROLE_MANAGE_CLASSIFICATION_SYMBOL')")
    @PutMapping(UrlConstant.ClassificationSymbol.UPDATE)
    public ResponseEntity<?> updateClassificationSymbol(
            @PathVariable Long id,
            @Valid @RequestBody ClassificationSymbolRequestDto requestDto
    ) {
        return VsResponseUtil.success(classificationSymbolService.update(id, requestDto));
    }

    @Operation(summary = "API Delete Classification Symbol")
    @PreAuthorize("hasRole('ROLE_MANAGE_CLASSIFICATION_SYMBOL')")
    @DeleteMapping(UrlConstant.ClassificationSymbol.DELETE)
    public ResponseEntity<?> deleteClassificationSymbol(@PathVariable Long id) {
        return VsResponseUtil.success(classificationSymbolService.delete(id));
    }

    @Operation(summary = "API Get All Classification Symbols")
    @PreAuthorize("hasAnyRole('ROLE_MANAGE_CLASSIFICATION_SYMBOL', 'ROLE_MANAGE_BOOK_DEFINITION')")
    @GetMapping(UrlConstant.ClassificationSymbol.GET_ALL)
    public ResponseEntity<?> getAllClassificationSymbols(@ParameterObject PaginationFullRequestDto requestDto) {
        return VsResponseUtil.success(classificationSymbolService.findAll(requestDto));
    }

    @Operation(summary = "API Get Classification Symbol By Id")
    @PreAuthorize("hasRole('ROLE_MANAGE_CLASSIFICATION_SYMBOL')")
    @GetMapping(UrlConstant.ClassificationSymbol.GET_BY_ID)
    public ResponseEntity<?> getClassificationSymbolById(@PathVariable Long id) {
        return VsResponseUtil.success(classificationSymbolService.findById(id));
    }

    @Operation(summary = "API Toggle Active Status of Classification Symbol")
    @PreAuthorize("hasRole('ROLE_MANAGE_CLASSIFICATION_SYMBOL')")
    @PatchMapping(UrlConstant.ClassificationSymbol.TOGGLE_ACTIVE)
    public ResponseEntity<?> toggleActiveStatus(@PathVariable Long id) {
        return VsResponseUtil.success(classificationSymbolService.toggleActiveStatus(id));
    }
}
