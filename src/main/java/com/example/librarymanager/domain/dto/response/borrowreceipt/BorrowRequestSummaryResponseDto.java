package com.example.librarymanager.domain.dto.response.borrowreceipt;

import com.example.librarymanager.domain.entity.CartDetail;
import com.example.librarymanager.domain.entity.Reader;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class BorrowRequestSummaryResponseDto {

    private long cartId;
    private String cardNumber;
    private String fullName;
    private List<String> borrowedDocuments = new ArrayList<>();

    public void setCartDetails(List<CartDetail> filteredCartDetails) {
        for (CartDetail cartDetail : filteredCartDetails) {
            borrowedDocuments.add(cartDetail.getBook().getBookCode());
        }
    }

    public void setReader(Reader reader) {
        this.cardNumber = reader.getCardNumber();
        this.fullName = reader.getFullName();
        this.cartId = reader.getCart().getId();
    }
}
