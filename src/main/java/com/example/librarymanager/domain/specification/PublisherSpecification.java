package com.example.librarymanager.domain.specification;

import com.example.librarymanager.domain.entity.Publisher;
import com.example.librarymanager.domain.entity.Publisher_;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class PublisherSpecification {

    public static Specification<Publisher> filterPublishers(String keyword, String searchBy, Boolean activeFlag) {
        return (root, query, builder) -> {
            query.distinct(true);

            Predicate predicate = builder.conjunction();

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                switch (searchBy) {
                    case Publisher_.CODE ->
                            predicate = builder.and(predicate, builder.like(root.get(Publisher_.code), "%" + keyword + "%"));

                    case Publisher_.NAME ->
                            predicate = builder.and(predicate, builder.like(root.get(Publisher_.name), "%" + keyword + "%"));

                    case Publisher_.ADDRESS ->
                            predicate = builder.and(predicate, builder.like(root.get(Publisher_.address), "%" + keyword + "%"));

                    case Publisher_.CITY ->
                            predicate = builder.and(predicate, builder.like(root.get(Publisher_.city), "%" + keyword + "%"));
                }
            }

            if (activeFlag != null) {
                predicate = builder.and(predicate, builder.equal(root.get(Publisher_.activeFlag), activeFlag));
            }

            return predicate;
        };
    }

}
