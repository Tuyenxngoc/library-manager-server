package com.example.librarymanager.controller;

import com.example.librarymanager.annotation.CurrentUser;
import com.example.librarymanager.annotation.RestApiV1;
import com.example.librarymanager.base.VsResponseUtil;
import com.example.librarymanager.constant.UrlConstant;
import com.example.librarymanager.security.CustomUserDetails;
import com.example.librarymanager.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

@RestApiV1
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "Cart")
public class CartController {

    CartService cartService;

    @Operation(summary = "API Get Cart Details")
    @PreAuthorize("hasRole('ROLE_READER')")
    @GetMapping(UrlConstant.Cart.GET_DETAILS)
    public ResponseEntity<?> getCartDetails(@CurrentUser CustomUserDetails userDetails) {
        return VsResponseUtil.success(cartService.getCartDetails(userDetails.getCardNumber()));
    }

    @Operation(summary = "API Add Book to Cart")
    @PreAuthorize("hasRole('ROLE_READER')")
    @PostMapping(UrlConstant.Cart.ADD)
    public ResponseEntity<?> addToCart(
            @RequestParam String bookCode,
            @CurrentUser CustomUserDetails userDetails
    ) {
        return VsResponseUtil.success(HttpStatus.CREATED, cartService.addToCart(userDetails.getCardNumber(), bookCode));
    }

    @Operation(summary = "API Remove Book from Cart")
    @PreAuthorize("hasRole('ROLE_READER')")
    @DeleteMapping(UrlConstant.Cart.REMOVE)
    public ResponseEntity<?> removeFromCart(
            @RequestParam Set<Long> cartDetailIds,
            @CurrentUser CustomUserDetails userDetails
    ) {
        return VsResponseUtil.success(cartService.removeFromCart(userDetails.getCardNumber(), cartDetailIds));
    }

    @Operation(summary = "API Clear Cart")
    @PreAuthorize("hasRole('ROLE_READER')")
    @DeleteMapping(UrlConstant.Cart.CLEAR)
    public ResponseEntity<?> clearCart(@CurrentUser CustomUserDetails userDetails) {
        return VsResponseUtil.success(cartService.clearCart(userDetails.getCardNumber()));
    }

}
