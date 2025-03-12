package com.example.librarymanager.service.impl;

import com.example.librarymanager.constant.ErrorMessage;
import com.example.librarymanager.constant.SuccessMessage;
import com.example.librarymanager.domain.dto.common.CommonResponseDto;
import com.example.librarymanager.domain.dto.pagination.PaginationFullRequestDto;
import com.example.librarymanager.domain.dto.pagination.PaginationResponseDto;
import com.example.librarymanager.domain.dto.response.borrowreceipt.BorrowRequestSummaryResponseDto;
import com.example.librarymanager.domain.dto.response.cart.CartDetailResponseDto;
import com.example.librarymanager.domain.entity.Book;
import com.example.librarymanager.domain.entity.Cart;
import com.example.librarymanager.domain.entity.CartDetail;
import com.example.librarymanager.domain.entity.Reader;
import com.example.librarymanager.exception.BadRequestException;
import com.example.librarymanager.exception.NotFoundException;
import com.example.librarymanager.repository.*;
import com.example.librarymanager.service.CartDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CartDetailServiceImpl implements CartDetailService {

    private final CartRepository cartRepository;

    private final CartDetailRepository cartDetailRepository;

    private final ReaderRepository readerRepository;

    private final BookRepository bookRepository;

    private final MessageSource messageSource;

    @Value("${cartDetail.maxBooks}")
    private int maxBooksInCart;

    private Cart getEntity(String cardNumber) {
        return cartRepository.findByReaderCardNumber(cardNumber).orElseGet(() -> {
            Cart cart = new Cart();
            Reader reader = readerRepository.findByCardNumber(cardNumber)
                    .orElseThrow(() -> new NotFoundException(ErrorMessage.Reader.ERR_NOT_FOUND_CARD_NUMBER, cardNumber));
            cart.setReader(reader);
            return cartRepository.save(cart);
        });
    }

    @Override
    public List<CartDetailResponseDto> getCartDetails(String cardNumber) {
        return cartDetailRepository.getAllByCardNumber(cardNumber);
    }

    @Override
    public CommonResponseDto addToCart(String cardNumber, String bookCode) {
        Cart cart = getEntity(cardNumber);

        // Kiểm tra xem giỏ hàng đã đầy chưa
        if (cart.getCartDetails().size() >= maxBooksInCart) {
            throw new BadRequestException(ErrorMessage.Cart.ERR_MAX_BOOKS_IN_CART);
        }

        Book book = bookRepository.findByBookCode(bookCode).orElseThrow(() -> new NotFoundException(ErrorMessage.Book.ERR_NOT_FOUND_CODE, bookCode));

        CartDetail cartDetail = new CartDetail();
        cartDetail.setBook(book);
        cartDetail.setCart(cart);

        cartDetailRepository.save(cartDetail);

        String message = messageSource.getMessage(SuccessMessage.CREATE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message);
    }

    @Override
    public CommonResponseDto removeFromCart(String cardNumber, Set<Long> cartDetailIds) {
        Cart cart = getEntity(cardNumber);

        cart.getCartDetails().removeIf(detail -> cartDetailIds.contains(detail.getId()));

        cartRepository.save(cart);

        String message = messageSource.getMessage(SuccessMessage.DELETE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message);
    }

    @Override
    public CommonResponseDto clearCart(String cardNumber) {
        Cart cart = getEntity(cardNumber);

        cart.getCartDetails().clear();

        cartRepository.save(cart);

        String message = messageSource.getMessage(SuccessMessage.DELETE, null, LocaleContextHolder.getLocale());
        return new CommonResponseDto(message);
    }

    @Override
    public PaginationResponseDto<BorrowRequestSummaryResponseDto> getPendingBorrowRequests(PaginationFullRequestDto requestDto) {
        return null;
    }
}
