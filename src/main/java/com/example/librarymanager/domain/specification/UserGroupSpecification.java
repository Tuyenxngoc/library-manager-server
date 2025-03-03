package com.example.librarymanager.domain.specification;

import com.example.librarymanager.domain.entity.UserGroup;
import com.example.librarymanager.domain.entity.UserGroup_;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class UserGroupSpecification {

    public static Specification<UserGroup> filterUserGroups(String keyword, String searchBy, Boolean activeFlag) {
        return (root, query, builder) -> {
            query.distinct(true);

            Predicate predicate = builder.conjunction();

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                switch (searchBy) {
                    case UserGroup_.CODE ->
                            predicate = builder.and(predicate, builder.like(root.get(UserGroup_.code), "%" + keyword + "%"));

                    case UserGroup_.NAME ->
                            predicate = builder.and(predicate, builder.like(root.get(UserGroup_.name), "%" + keyword + "%"));
                }
            }

            if (activeFlag != null) {
                predicate = builder.and(predicate, builder.equal(root.get(UserGroup_.activeFlag), activeFlag));
            }

            return predicate;
        };
    }

}
