package com.example.librarymanager.domain.specification;

import com.example.librarymanager.domain.entity.Author;
import com.example.librarymanager.domain.entity.Author_;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class AuthorSpecification {

    public static Specification<Author> filterAuthors(String keyword, String searchBy, Boolean activeFlag) {
        return (root, query, builder) -> {
            query.distinct(true);

            Predicate predicate = builder.conjunction();

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                switch (searchBy) {
                    case Author_.CODE ->
                            predicate = builder.and(predicate, builder.like(root.get(Author_.code), "%" + keyword + "%"));

                    case Author_.FULL_NAME ->
                            predicate = builder.and(predicate, builder.like(root.get(Author_.fullName), "%" + keyword + "%"));
                }
            }

            if (activeFlag != null) {
                predicate = builder.and(predicate, builder.equal(root.get(Author_.activeFlag), activeFlag));
            }

            return predicate;
        };
    }

}
