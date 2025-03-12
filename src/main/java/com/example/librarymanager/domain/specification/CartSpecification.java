package com.example.librarymanager.domain.specification;

import com.example.librarymanager.domain.entity.Cart;
import com.example.librarymanager.domain.entity.Cart_;
import com.example.librarymanager.domain.entity.Reader;
import com.example.librarymanager.domain.entity.Reader_;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class CartSpecification {

    public static Specification<Cart> filterCarts(String keyword, String searchBy) {
        return (root, query, builder) -> {
            query.distinct(true);

            Predicate predicate = builder.conjunction();

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
