package com.example.librarymanager.controller;

import com.example.librarymanager.annotation.CurrentUser;
import com.example.librarymanager.annotation.RestApiV1;
import com.example.librarymanager.base.VsResponseUtil;
import com.example.librarymanager.constant.UrlConstant;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.request.ExportReceiptRequestDto;
import com.example.librarymanager.security.CustomUserDetails;
import com.example.librarymanager.service.ExportReceiptService;
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
@Tag(name = "Export Receipt")
public class ExportReceiptController {

    ExportReceiptService exportReceiptService;

    @Operation(summary = "API Generate New Receipt Number")
    @PreAuthorize("hasRole('ROLE_MANAGE_EXPORT_RECEIPT')")
    @GetMapping(UrlConstant.ExportReceipt.GENERATE_RECEIPT_NUMBER)
    public ResponseEntity<?> generateReceiptNumber() {
        return VsResponseUtil.success(exportReceiptService.generateReceiptNumber());
    }

    @Operation(summary = "API Create Export Receipt")
    @PreAuthorize("hasRole('ROLE_MANAGE_EXPORT_RECEIPT')")
    @PostMapping(UrlConstant.ExportReceipt.CREATE)
    public ResponseEntity<?> createExportReceipt(
            @Valid @RequestBody ExportReceiptRequestDto requestDto,
            @CurrentUser CustomUserDetails userDetails
    ) {
        return VsResponseUtil.success(HttpStatus.CREATED, exportReceiptService.save(requestDto, userDetails.getUserId()));
    }

    @Operation(summary = "API Update Export Receipt")
    @PreAuthorize("hasRole('ROLE_MANAGE_EXPORT_RECEIPT')")
    @PutMapping(UrlConstant.ExportReceipt.UPDATE)
    public ResponseEntity<?> updateExportReceipt(
            @PathVariable Long id,
            @Valid @RequestBody ExportReceiptRequestDto requestDto,
            @CurrentUser CustomUserDetails userDetails
    ) {
        return VsResponseUtil.success(exportReceiptService.update(id, requestDto, userDetails.getUserId()));
    }

    @Operation(summary = "API Delete Export Receipt")
    @PreAuthorize("hasRole('ROLE_MANAGE_EXPORT_RECEIPT')")
    @DeleteMapping(UrlConstant.ExportReceipt.DELETE)
    public ResponseEntity<?> deleteExportReceipt(
            @PathVariable Long id,
            @CurrentUser CustomUserDetails userDetails
    ) {
        return VsResponseUtil.success(exportReceiptService.delete(id, userDetails.getUserId()));
    }

    @Operation(summary = "API Get All Export Receipts")
    @PreAuthorize("hasRole('ROLE_MANAGE_EXPORT_RECEIPT')")
    @GetMapping(UrlConstant.ExportReceipt.GET_ALL)
    public ResponseEntity<?> getAllExportReceipts(@ParameterObject PaginationFullRequestDto requestDto) {
        return VsResponseUtil.success(exportReceiptService.findAll(requestDto));
    }

    @Operation(summary = "API Get Export Receipt By Id")
    @PreAuthorize("hasRole('ROLE_MANAGE_EXPORT_RECEIPT')")
    @GetMapping(UrlConstant.ExportReceipt.GET_BY_ID)
    public ResponseEntity<?> getExportReceiptById(@PathVariable Long id) {
        return VsResponseUtil.success(exportReceiptService.findById(id));
    }

}
