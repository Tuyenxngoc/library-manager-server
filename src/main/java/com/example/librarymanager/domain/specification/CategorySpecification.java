package com.example.librarymanager.domain.specification;

import com.example.librarymanager.domain.entity.Category;
import com.example.librarymanager.domain.entity.Category_;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class CategorySpecification {

    public static Specification<Category> filterCategories(String keyword, String searchBy, Boolean activeFlag) {
        return (root, query, builder) -> {
            query.distinct(true);

            Predicate predicate = builder.conjunction();

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                switch (searchBy) {
                    case Category_.CATEGORY_NAME ->
                            predicate = builder.and(predicate, builder.like(root.get(Category_.categoryName), "%" + keyword + "%"));
                }
            }

            if (activeFlag != null) {
                predicate = builder.and(predicate, builder.equal(root.get(Category_.activeFlag), activeFlag));
            }

            return predicate;
        };
    }

}
