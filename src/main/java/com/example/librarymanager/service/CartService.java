package com.example.librarymanager.service;

import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.pagination.PaginationResponseDto;
import com.example.librarymanager.domain.dto.response.borrowreceipt.BorrowRequestSummaryResponseDto;
import com.example.librarymanager.domain.dto.response.cart.CartDetailResponseDto;

import java.util.List;
import java.util.Set;

public interface CartDetailService {

    List<CartDetailResponseDto> getCartDetails(String cardNumber);

    CommonResponseDto addToCart(String cardNumber, String bookCode);

    CommonResponseDto removeFromCart(String cardNumber, Set<Long> cartDetailIds);

    CommonResponseDto clearCart(String cardNumber);

    PaginationResponseDto<BorrowRequestSummaryResponseDto> getPendingBorrowRequests(PaginationFullRequestDto requestDto);
    
}
