package com.example.librarymanager.domain.specification;

import com.example.librarymanager.constant.CardStatus;
import com.example.librarymanager.domain.entity.Reader;
import com.example.librarymanager.domain.entity.Reader_;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class ReaderSpecification {

    public static Specification<Reader> filterReaders(String keyword, String searchBy) {
        return (root, query, builder) -> {
            query.distinct(true);

            Predicate predicate = builder.conjunction();

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                switch (searchBy) {
                    case Reader_.CARD_NUMBER ->
                            predicate = builder.and(predicate, builder.like(root.get(Reader_.cardNumber), "%" + keyword + "%"));

                    case Reader_.FULL_NAME ->
                            predicate = builder.and(predicate, builder.like(root.get(Reader_.fullName), "%" + keyword + "%"));

                    case Reader_.STATUS -> {
                        try {
                            CardStatus statusEnum = CardStatus.valueOf(keyword.toUpperCase());
                            predicate = builder.and(predicate, builder.equal(root.get(Reader_.status), statusEnum));
                        } catch (IllegalArgumentException ignored) {
                        }
                    }
                }
            }

            return predicate;
        };
    }

}
