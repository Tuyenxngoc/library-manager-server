package com.example.librarymanager.controller;

import com.example.librarymanager.annotation.CurrentUser;
import com.example.librarymanager.annotation.RestApiV1;
import com.example.librarymanager.base.VsResponseUtil;
import com.example.librarymanager.constant.UrlConstant;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.request.ImportReceiptRequestDto;
import com.example.librarymanager.security.CustomUserDetails;
import com.example.librarymanager.service.ImportReceiptService;
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
@Tag(name = "Import Receipt")
public class ImportReceiptController {

    ImportReceiptService importReceiptService;

    @Operation(summary = "API Generate New Receipt Number")
    @PreAuthorize("hasRole('ROLE_MANAGE_IMPORT_RECEIPT')")
    @GetMapping(UrlConstant.ImportReceipt.GENERATE_RECEIPT_NUMBER)
    public ResponseEntity<?> generateReceiptNumber() {
        return VsResponseUtil.success(importReceiptService.generateReceiptNumber());
    }

    @Operation(summary = "API Create Import Receipt")
    @PreAuthorize("hasRole('ROLE_MANAGE_IMPORT_RECEIPT')")
    @PostMapping(UrlConstant.ImportReceipt.CREATE)
    public ResponseEntity<?> createImportReceipt(
            @Valid @RequestBody ImportReceiptRequestDto requestDto,
            @CurrentUser CustomUserDetails userDetails
    ) {
        return VsResponseUtil.success(HttpStatus.CREATED, importReceiptService.save(requestDto, userDetails.getUserId()));
    }

    @Operation(summary = "API Update Import Receipt")
    @PreAuthorize("hasRole('ROLE_MANAGE_IMPORT_RECEIPT')")
    @PutMapping(UrlConstant.ImportReceipt.UPDATE)
    public ResponseEntity<?> updateImportReceipt(
            @PathVariable Long id,
            @Valid @RequestBody ImportReceiptRequestDto requestDto,
            @CurrentUser CustomUserDetails userDetails
    ) {
        return VsResponseUtil.success(importReceiptService.update(id, requestDto, userDetails.getUserId()));
    }

    @Operation(summary = "API Delete Import Receipt")
    @PreAuthorize("hasRole('ROLE_MANAGE_IMPORT_RECEIPT')")
    @DeleteMapping(UrlConstant.ImportReceipt.DELETE)
    public ResponseEntity<?> deleteImportReceipt(
            @PathVariable Long id,
            @CurrentUser CustomUserDetails userDetails
    ) {
        return VsResponseUtil.success(importReceiptService.delete(id, userDetails.getUserId()));
    }

    @Operation(summary = "API Get All Import Receipts")
    @PreAuthorize("hasRole('ROLE_MANAGE_IMPORT_RECEIPT')")
    @GetMapping(UrlConstant.ImportReceipt.GET_ALL)
    public ResponseEntity<?> getAllImportReceipts(@ParameterObject PaginationFullRequestDto requestDto) {
        return VsResponseUtil.success(importReceiptService.findAll(requestDto));
    }

    @Operation(summary = "API Get Import Receipt By Id")
    @PreAuthorize("hasRole('ROLE_MANAGE_IMPORT_RECEIPT')")
    @GetMapping(UrlConstant.ImportReceipt.GET_BY_ID)
    public ResponseEntity<?> getImportReceiptById(@PathVariable Long id) {
        return VsResponseUtil.success(importReceiptService.findById(id));
    }

}
