package com.example.librarymanager.domain.specification;

import com.example.librarymanager.constant.ErrorMessage;
import com.example.librarymanager.constant.JoinType;
import com.example.librarymanager.domain.dto.filter.BookDefinitionFilter;
import com.example.librarymanager.domain.dto.filter.QueryFilter;
import com.example.librarymanager.domain.entity.*;
import com.example.librarymanager.exception.BadRequestException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static com.example.librarymanager.util.SpecificationsUtil.castToRequiredType;

public class BookSpecification {

    private static void validateField(String fieldName) {
        try {
            BookDefinition.class.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new BadRequestException(ErrorMessage.BookDefinition.ERR_INVALID_FIELD, fieldName);
        }
    }

    public static Specification<BookDefinition> createSpecification(QueryFilter input) {
        validateField(input.getField());

        return switch (input.getOperator()) {

            case EQUALS -> (root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get(input.getField()),
                            castToRequiredType(root.get(input.getField()).getJavaType(),
                                    input.getValue()));

            case NOT_EQUALS -> (root, query, criteriaBuilder) ->
                    criteriaBuilder.notEqual(root.get(input.getField()),
                            castToRequiredType(root.get(input.getField()).getJavaType(),
                                    input.getValue()));

            case GREATER_THAN -> (root, query, criteriaBuilder) ->
                    criteriaBuilder.gt(root.get(input.getField()),
                            (Number) castToRequiredType(
                                    root.get(input.getField()).getJavaType(),
                                    input.getValue()));

            case LESS_THAN -> (root, query, criteriaBuilder) ->
                    criteriaBuilder.lt(root.get(input.getField()),
                            (Number) castToRequiredType(
                                    root.get(input.getField()).getJavaType(),
                                    input.getValue()));

            case LIKE -> (root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get(input.getField()),
                            "%" + input.getValue() + "%");

            case IN -> (root, query, criteriaBuilder) ->
                    criteriaBuilder.in(root.get(input.getField()))
                            .value(castToRequiredType(
                                    root.get(input.getField()).getJavaType(),
                                    input.getValues()));
        };
    }

    public static Specification<BookDefinition> getSpecificationFromFilters(List<QueryFilter> queryFilters) {
        if (queryFilters.isEmpty()) {
            return null;
        }

        Specification<BookDefinition> specification = Specification.where(createSpecification(queryFilters.get(0)));

        for (int i = 1; i < queryFilters.size(); i++) {
            QueryFilter currentQueryFilter = queryFilters.get(i);
            QueryFilter previousQueryFilter = queryFilters.get(i - 1);
            Specification<BookDefinition> nextSpecification = createSpecification(currentQueryFilter);

            if (previousQueryFilter.getJoinType() == JoinType.OR) {
                specification = specification.or(nextSpecification);
            } else {
                specification = specification.and(nextSpecification);
            }
        }
        return specification;
    }

    public static Specification<BookDefinition> filterBooks(BookDefinitionFilter filters) {
        return (root, query, builder) -> {
            query.distinct(true);

            Predicate predicate = builder.conjunction();

            if (filters.getBookCode() != null && StringUtils.isNotBlank(filters.getBookCode())) {
                Join<BookDefinition, Book> bookJoin = root.join(BookDefinition_.books, jakarta.persistence.criteria.JoinType.LEFT);
                predicate = builder.and(predicate, builder.equal(bookJoin.get(Book_.bookCode), filters.getBookCode()));
            }

            if (filters.getTitle() != null && StringUtils.isNotBlank(filters.getTitle())) {
                predicate = builder.and(predicate, builder.like(root.get(BookDefinition_.title), "%" + filters.getTitle() + "%"));
            }

            if (filters.getKeyword() != null && StringUtils.isNotBlank(filters.getKeyword())) {
                predicate = builder.and(predicate, builder.like(root.get(BookDefinition_.keywords), "%" + filters.getKeyword() + "%"));
            }

            if (filters.getPublishingYear() != null && StringUtils.isNotBlank(filters.getPublishingYear())) {
                predicate = builder.and(predicate, builder.like(root.get(BookDefinition_.publishingYear), "%" + filters.getPublishingYear() + "%"));
            }

            if (filters.getAuthor() != null && StringUtils.isNotBlank(filters.getAuthor())) {
                Join<BookDefinition, BookAuthor> bookAuthorJoin = root.join(BookDefinition_.bookAuthors, jakarta.persistence.criteria.JoinType.LEFT);
                Join<BookAuthor, Author> authorJoin = bookAuthorJoin.join(BookAuthor_.author, jakarta.persistence.criteria.JoinType.LEFT);

                predicate = builder.and(predicate, builder.like(authorJoin.get(Author_.fullName), "%" + filters.getAuthor() + "%"));
            }

            return predicate;
        };
    }
}
