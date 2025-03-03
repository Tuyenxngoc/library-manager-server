package com.example.librarymanager.domain.specification;

import com.example.librarymanager.domain.entity.ClassificationSymbol;
import com.example.librarymanager.domain.entity.ClassificationSymbol_;
import com.example.librarymanager.util.SpecificationsUtil;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class ClassificationSymbolSpecification {

    public static Specification<ClassificationSymbol> filterClassificationSymbols(String keyword, String searchBy, Boolean activeFlag) {
        return (root, query, builder) -> {
            query.distinct(true);

            Predicate predicate = builder.conjunction();

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                switch (searchBy) {
                    case ClassificationSymbol_.CODE ->
                            predicate = builder.and(predicate, builder.like(root.get(ClassificationSymbol_.code), "%" + keyword + "%"));

                    case ClassificationSymbol_.NAME ->
                            predicate = builder.and(predicate, builder.like(root.get(ClassificationSymbol_.name), "%" + keyword + "%"));

                    case ClassificationSymbol_.LEVEL ->
                            predicate = builder.and(predicate, builder.equal(root.get(ClassificationSymbol_.level),
                                    SpecificationsUtil.castToRequiredType(root.get(ClassificationSymbol_.level).getJavaType(), keyword)));
                }
            }

            if (activeFlag != null) {
                predicate = builder.and(predicate, builder.equal(root.get(ClassificationSymbol_.activeFlag), activeFlag));
            }

            return predicate;
        };
    }

}
