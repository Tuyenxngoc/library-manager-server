package com.example.librarymanager.repository;

import com.example.librarymanager.domain.dto.response.cart.CartDetailResponseDto;
import com.example.librarymanager.domain.entity.Book;
import com.example.librarymanager.domain.entity.Cart;
import com.example.librarymanager.domain.entity.CartDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, Long>, JpaSpecificationExecutor<Cart> {

    @Query("SELECT new com.example.librarymanager.domain.dto.response.cart.CartDetailResponseDto(cd) " +
            "FROM CartDetail cd " +
            "INNER JOIN cd.cart c " +
            "WHERE c.id = :cartId")
    List<CartDetailResponseDto> getAllByCartId(@Param("cartId") Long cartId);

    @Query("SELECT COUNT(DISTINCT cd.cart.id) FROM CartDetail cd WHERE cd.borrowTo > CURRENT_DATE")
    int countBorrowRequests();

    @Modifying
    @Transactional
    @Query("DELETE " +
            "FROM CartDetail cd WHERE " +
            "cd.cart.id = :cartId " +
            "AND cd.book IN :books " +
            "AND cd.borrowTo > :now")
    void deleteByCartIdAndBooks(@Param("cartId") Long cartId, @Param("books") Set<Book> books, @Param("now") LocalDateTime now);

}
