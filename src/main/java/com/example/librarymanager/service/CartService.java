package com.example.librarymanager.service;

import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.pagination.PaginationResponseDto;
import com.example.librarymanager.domain.dto.response.borrowreceipt.BorrowRequestSummaryResponseDto;
import com.example.librarymanager.domain.dto.response.cart.CartDetailResponseDto;

import java.util.List;

public interface CartService {

    List<CartDetailResponseDto> getCartDetails(String cardNumber, String title, String type);

    CommonResponseDto addToCart(String cardNumber, Long bookId);

    CommonResponseDto removeFromCart(String cardNumber, Long cartDetailId);

    CommonResponseDto clearCart(String cardNumber);

    PaginationResponseDto<BorrowRequestSummaryResponseDto> getPendingBorrowRequests(PaginationFullRequestDto requestDto);
}
