package com.example.librarymanager.domain.specification;

import com.example.librarymanager.domain.entity.User;
import com.example.librarymanager.domain.entity.User_;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<User> filterUsers(String keyword, String searchBy) {
        return (root, query, builder) -> {
            query.distinct(true);

            Predicate predicate = builder.conjunction();

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                switch (searchBy) {
                    case User_.USERNAME ->
                            predicate = builder.and(predicate, builder.like(root.get(User_.username), "%" + keyword + "%"));

                    case User_.FULL_NAME ->
                            predicate = builder.and(predicate, builder.like(root.get(User_.fullName), "%" + keyword + "%"));
                }
            }

            return predicate;
        };
    }

}
