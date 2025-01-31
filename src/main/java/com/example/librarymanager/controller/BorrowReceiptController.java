package com.example.librarymanager.controller;

import com.example.librarymanager.annotation.CurrentUser;
import com.example.librarymanager.annotation.RestApiV1;
import com.example.librarymanager.base.VsResponseUtil;
import com.example.librarymanager.constant.BorrowStatus;
import com.example.librarymanager.constant.ErrorMessage;
import com.example.librarymanager.constant.UrlConstant;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.request.BorrowReceiptRequestDto;
import com.example.librarymanager.domain.dto.request.CreateBorrowReceiptRequestDto;
import com.example.librarymanager.security.CustomUserDetails;
import com.example.librarymanager.service.BorrowReceiptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Borrow Receipt")
public class BorrowReceiptController {

    BorrowReceiptService borrowReceiptService;

    @Operation(summary = "API Generate New Receipt Number")
    @PreAuthorize("hasRole('ROLE_MANAGE_IMPORT_RECEIPT')")
    @GetMapping(UrlConstant.BorrowReceipt.GENERATE_RECEIPT_NUMBER)
    public ResponseEntity<?> generateReceiptNumber() {
        return VsResponseUtil.success(borrowReceiptService.generateReceiptNumber());
    }

    @Operation(summary = "API Create Borrow Receipt")
    @PreAuthorize("hasRole('ROLE_MANAGE_BORROW_RECEIPT')")
    @PostMapping(UrlConstant.BorrowReceipt.CREATE)
    public ResponseEntity<?> createBorrowReceipt(
            @Valid @RequestBody BorrowReceiptRequestDto requestDto,
            @CurrentUser CustomUserDetails userDetails
    ) {
        return VsResponseUtil.success(HttpStatus.CREATED, borrowReceiptService.save(requestDto, userDetails.getUserId()));
    }

    @Operation(summary = "API Update Borrow Receipt")
    @PreAuthorize("hasRole('ROLE_MANAGE_BORROW_RECEIPT')")
    @PutMapping(UrlConstant.BorrowReceipt.UPDATE)
    public ResponseEntity<?> updateBorrowReceipt(
            @PathVariable Long id,
            @Valid @RequestBody BorrowReceiptRequestDto requestDto,
            @CurrentUser CustomUserDetails userDetails
    ) {
        return VsResponseUtil.success(borrowReceiptService.update(id, requestDto, userDetails.getUserId()));
    }

    @Operation(summary = "API Delete Borrow Receipt")
    @PreAuthorize("hasRole('ROLE_MANAGE_BORROW_RECEIPT')")
    @DeleteMapping(UrlConstant.BorrowReceipt.DELETE)
    public ResponseEntity<?> deleteBorrowReceipt(
            @PathVariable Long id,
            @CurrentUser CustomUserDetails userDetails
    ) {
        return VsResponseUtil.success(borrowReceiptService.delete(id, userDetails.getUserId()));
    }

    @Operation(summary = "API Get All Borrow Receipts")
    @PreAuthorize("hasRole('ROLE_MANAGE_BORROW_RECEIPT')")
    @GetMapping(UrlConstant.BorrowReceipt.GET_ALL)
    public ResponseEntity<?> getAllBorrowReceipts(
            @ParameterObject PaginationFullRequestDto requestDto,
            @RequestParam(value = "status", required = false) BorrowStatus status
    ) {
        return VsResponseUtil.success(borrowReceiptService.findAll(requestDto, status));
    }

    @Operation(summary = "API Get Borrow Receipt By Id")
    @PreAuthorize("hasRole('ROLE_MANAGE_BORROW_RECEIPT')")
    @GetMapping(UrlConstant.BorrowReceipt.GET_BY_ID)
    public ResponseEntity<?> getBorrowReceiptById(@PathVariable Long id) {
        return VsResponseUtil.success(borrowReceiptService.findById(id));
    }

    @Operation(summary = "API Get Borrow Receipt By Cart Id")
    @PreAuthorize("hasRole('ROLE_MANAGE_BORROW_RECEIPT')")
    @GetMapping(UrlConstant.BorrowReceipt.GET_BY_CART_ID)
    public ResponseEntity<?> getBorrowReceiptByCartId(@PathVariable Long id) {
        return VsResponseUtil.success(borrowReceiptService.findByCartId(id));
    }

    @Operation(summary = "API Get Borrow Receipts by Reader")
    @PreAuthorize("hasRole('ROLE_READER')")
    @GetMapping(UrlConstant.BorrowReceipt.GET_BY_READER)
    public ResponseEntity<?> getBorrowReceiptsByReader(
            @CurrentUser CustomUserDetails userDetails,
            @ParameterObject PaginationFullRequestDto requestDto,
            @RequestParam(value = "status", required = false) BorrowStatus status
    ) {
        return VsResponseUtil.success(borrowReceiptService.findByCardNumber(userDetails.getCardNumber(), requestDto, status));
    }

    @Operation(summary = "API Get Borrow Receipt Details By Id")
    @PreAuthorize("hasRole('ROLE_MANAGE_BORROW_RECEIPT')")
    @GetMapping(UrlConstant.BorrowReceipt.GET_DETAILS_BY_ID)
    public ResponseEntity<?> getBorrowReceiptDetailsById(@PathVariable Long id) {
        return VsResponseUtil.success(borrowReceiptService.findDetailsById(id));
    }

    @Operation(summary = "API Cancel Return")
    @PreAuthorize("hasRole('ROLE_MANAGE_BORROW_RECEIPT')")
    @PutMapping(UrlConstant.BorrowReceipt.CANCEL_RETURN)
    public ResponseEntity<?> cancelReturn(
            @Valid @RequestBody
            @NotNull(message = ErrorMessage.INVALID_ARRAY_IS_REQUIRED)
            @Size(max = 100, message = ErrorMessage.INVALID_ARRAY_LENGTH)
            Set<@NotNull(message = ErrorMessage.INVALID_SOME_THING_FIELD_IS_REQUIRED) Long> borrowIds,
            @CurrentUser CustomUserDetails userDetails
    ) {
        return VsResponseUtil.success(borrowReceiptService.cancelReturn(borrowIds, userDetails.getUserId()));
    }

    @Operation(summary = "API Print Borrow Receipts")
    @PreAuthorize("hasRole('ROLE_MANAGE_BORROW_RECEIPT')")
    @PostMapping(UrlConstant.BorrowReceipt.PRINT)
    public ResponseEntity<byte[]> printBorrowReceipts(
            @Valid @RequestBody CreateBorrowReceiptRequestDto requestDto,
            @CurrentUser CustomUserDetails userDetails
    ) {
        byte[] pdfBytes = borrowReceiptService.createPdfForReceipts(requestDto, userDetails.getUserId());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=borrow_receipts.pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @Operation(summary = "API Print Overdue Borrow List")
    @PreAuthorize("hasRole('ROLE_MANAGE_BORROW_RECEIPT')")
    @GetMapping(UrlConstant.BorrowReceipt.PRINT_OVERDUE_LIST)
    public ResponseEntity<byte[]> printOverdueList() {
        byte[] pdfBytes = borrowReceiptService.createOverdueListPdf();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=overdue_list.pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @Operation(summary = "API Export Return Data")
    @PreAuthorize("hasRole('ROLE_MANAGE_BORROW_RECEIPT')")
    @GetMapping(UrlConstant.BorrowReceipt.EXPORT_RETURN_DATA)
    public ResponseEntity<byte[]> exportReturnData() {
        byte[] excelBytes = borrowReceiptService.exportReturnData();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=return_data.xlsx");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }
}
