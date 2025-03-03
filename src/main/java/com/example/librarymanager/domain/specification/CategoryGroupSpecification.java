package com.example.librarymanager.domain.specification;

import com.example.librarymanager.domain.entity.CategoryGroup;
import com.example.librarymanager.domain.entity.CategoryGroup_;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class CategoryGroupSpecification {

    public static Specification<CategoryGroup> filterCategoryGroups(String keyword, String searchBy, Boolean activeFlag) {
        return (root, query, builder) -> {
            query.distinct(true);

            Predicate predicate = builder.conjunction();

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                switch (searchBy) {
                    case CategoryGroup_.GROUP_NAME ->
                            predicate = builder.and(predicate, builder.like(root.get(CategoryGroup_.groupName), "%" + keyword + "%"));
                }
            }

            if (activeFlag != null) {
                predicate = builder.and(predicate, builder.equal(root.get(CategoryGroup_.activeFlag), activeFlag));
            }

            return predicate;
        };
    }

}
