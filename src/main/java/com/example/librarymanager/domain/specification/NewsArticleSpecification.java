package com.example.librarymanager.domain.specification;

import com.example.librarymanager.domain.entity.NewsArticle;
import com.example.librarymanager.domain.entity.NewsArticle_;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class NewsArticleSpecification {

    public static Specification<NewsArticle> filterNewsArticles(String keyword, String searchBy, Boolean activeFlag) {
        return (root, query, builder) -> {
            query.distinct(true);

            Predicate predicate = builder.conjunction();

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                switch (searchBy) {
                    case NewsArticle_.TITLE ->
                            predicate = builder.and(predicate, builder.like(root.get(NewsArticle_.title), "%" + keyword + "%"));

                    case NewsArticle_.DESCRIPTION ->
                            predicate = builder.and(predicate, builder.like(root.get(NewsArticle_.description), "%" + keyword + "%"));
                }
            }

            if (activeFlag != null) {
                predicate = builder.and(predicate, builder.equal(root.get(NewsArticle_.activeFlag), activeFlag));
            }

            return predicate;
        };
    }

}
