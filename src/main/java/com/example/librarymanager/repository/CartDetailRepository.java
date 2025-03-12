package com.example.librarymanager.repository;

import com.example.librarymanager.domain.dto.response.cart.CartDetailResponseDto;
import com.example.librarymanager.domain.entity.Cart;
import com.example.librarymanager.domain.entity.CartDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, Long>, JpaSpecificationExecutor<Cart> {

    @Query("SELECT new com.example.librarymanager.domain.dto.response.cart.CartDetailResponseDto(cd) " +
            "FROM CartDetail cd " +
            "JOIN cd.cart c " +
            "JOIN c.reader r " +
            "WHERE r.cardNumber = :cardNumber")
    List<CartDetailResponseDto> getAllByCardNumber(@Param("cardNumber") String cardNumber);

}
