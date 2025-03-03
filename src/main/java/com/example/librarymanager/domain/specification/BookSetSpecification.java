package com.example.librarymanager.domain.specification;

import com.example.librarymanager.domain.entity.BookSet;
import com.example.librarymanager.domain.entity.BookSet_;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class BookSetSpecification {

    public static Specification<BookSet> filterBookSets(String keyword, String searchBy, Boolean activeFlag) {
        return (root, query, builder) -> {
            query.distinct(true);

            Predicate predicate = builder.conjunction();

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                switch (searchBy) {
                    case BookSet_.NAME ->
                            predicate = builder.and(predicate, builder.like(root.get(BookSet_.name), "%" + keyword + "%"));
                }
            }

            if (activeFlag != null) {
                predicate = builder.and(predicate, builder.equal(root.get(BookSet_.activeFlag), activeFlag));
            }

            return predicate;
        };
    }

}
