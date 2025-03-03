package com.example.librarymanager.domain.specification;

import com.example.librarymanager.domain.entity.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.ListJoin;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class CartSpecification {

    public static Specification<Cart> filterCarts(String keyword, String searchBy) {
        return (root, query, builder) -> {
            query.distinct(true);

            Predicate predicate = builder.conjunction();

            ListJoin<Cart, CartDetail> cartCartDetailListJoin = root.join(Cart_.cartDetails, JoinType.INNER);
            predicate = builder.and(predicate, builder.greaterThan(cartCartDetailListJoin.get(CartDetail_.borrowTo), LocalDateTime.now()));

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                Join<Cart, Reader> readerJoin = root.join(Cart_.reader);

                switch (searchBy) {
                    case Reader_.CARD_NUMBER:
                        predicate = builder.and(predicate, builder.like(readerJoin.get(Reader_.cardNumber), "%" + keyword + "%"));
                        break;

                    case Reader_.FULL_NAME:
                        predicate = builder.and(predicate, builder.like(readerJoin.get(Reader_.fullName), "%" + keyword + "%"));
                        break;
                }
            }

            return predicate;
        };
    }

}
