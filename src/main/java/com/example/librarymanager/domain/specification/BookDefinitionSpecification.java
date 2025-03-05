package com.example.librarymanager.domain.specification;

import com.example.librarymanager.constant.ErrorMessage;
import com.example.librarymanager.constant.QueryOperator;
import com.example.librarymanager.domain.dto.filter.BookDefinitionFilter;
import com.example.librarymanager.domain.dto.filter.QueryFilter;
import com.example.librarymanager.domain.entity.*;
import com.example.librarymanager.exception.BadRequestException;
import jakarta.persistence.criteria.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Map;

import static com.example.librarymanager.util.SpecificationsUtil.castToRequiredType;

public class BookDefinitionSpecification {

    private static final Map<String, List<QueryOperator>> ALLOWED_FIELDS = Map.of(
            "title", List.of(QueryOperator.LIKE, QueryOperator.EQUALS),
            "author", List.of(QueryOperator.LIKE, QueryOperator.EQUALS, QueryOperator.IN),
            "publisher", List.of(QueryOperator.LIKE, QueryOperator.EQUALS, QueryOperator.IN),
            "publishingYear", List.of(QueryOperator.EQUALS, QueryOperator.GREATER_THAN, QueryOperator.LESS_THAN),
            "isbn", List.of(QueryOperator.LIKE, QueryOperator.EQUALS)
    );

    public static Specification<BookDefinition> baseFilterBookDefinitions(String keyword, String searchBy, Boolean activeFlag) {
        return (root, query, builder) -> {
            query.distinct(true);

            Predicate predicate = builder.conjunction();

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                switch (searchBy) {
                    case BookDefinition_.TITLE ->
                            predicate = builder.and(predicate, builder.like(root.get(BookDefinition_.title), "%" + keyword + "%"));

                    case BookDefinition_.BOOK_CODE ->
                            predicate = builder.and(predicate, builder.like(root.get(BookDefinition_.bookCode), "%" + keyword + "%"));
                }
            }

            if (activeFlag != null) {
                predicate = builder.and(predicate, builder.equal(root.get(BookDefinition_.activeFlag), activeFlag));
            }

            return predicate;
        };
    }

    public static Specification<BookDefinition> filterByCategoryGroupId(Long categoryGroupId) {
        return (root, query, builder) -> {
            if (categoryGroupId != null) {
                Join<BookDefinition, Category> categoryJoin = root.join(BookDefinition_.category, JoinType.INNER);
                Join<Category, CategoryGroup> categoryGroupJoin = categoryJoin.join(Category_.categoryGroup, JoinType.INNER);
                return builder.equal(categoryGroupJoin.get(CategoryGroup_.id), categoryGroupId);
            }
            return builder.conjunction();
        };
    }

    public static Specification<BookDefinition> filterByAuthorId(Long authorId) {
        return (root, query, builder) -> {
            if (authorId != null) {
                ListJoin<BookDefinition, BookAuthor> bookAuthorListJoin = root.join(BookDefinition_.bookAuthors, JoinType.INNER);
                Join<BookAuthor, Author> authorJoin = bookAuthorListJoin.join(BookAuthor_.author, JoinType.INNER);
                return builder.equal(authorJoin.get(Author_.id), authorId);
            }
            return builder.conjunction();
        };
    }

    public static Specification<BookDefinition> filterByBooksCountGreaterThanZero() {
        return (root, query, builder) -> {
            Join<BookDefinition, Book> bookJoin = root.join(BookDefinition_.books, JoinType.LEFT);

            query.groupBy(root.get(BookDefinition_.id));

            query.having(builder.greaterThan(builder.count(bookJoin), 0L));

            return builder.conjunction();
        };
    }

    public static Specification<BookDefinition> filterByCategoryId(Long categoryId) {
        return (root, query, builder) -> {
            if (categoryId != null) {
                Join<BookDefinition, Category> categoryJoin = root.join(BookDefinition_.category, JoinType.INNER);
                return builder.equal(categoryJoin.get(Category_.id), categoryId);
            }
            return builder.conjunction();
        };
    }

    public static Specification<BookDefinition> orderByBorrowCount() {
        return (root, query, criteriaBuilder) -> {
            Join<BookDefinition, Book> bookJoin = root.join(BookDefinition_.books, JoinType.LEFT);
            Join<Book, BookBorrow> borrowJoin = bookJoin.join(Book_.bookBorrows, JoinType.LEFT);

            query.groupBy(root);
            query.orderBy(criteriaBuilder.desc(criteriaBuilder.count(borrowJoin.get(BookBorrow_.id))));

            return criteriaBuilder.conjunction();
        };
    }

    public static Specification<BookDefinition> orderByNewReleases() {
        return (root, query, criteriaBuilder) -> {
            Expression<Integer> yearAsNumber = criteriaBuilder.function("TRY_CAST", Integer.class, root.get(BookDefinition_.publishingYear));
            query.orderBy(criteriaBuilder.desc(yearAsNumber));

            return criteriaBuilder.conjunction();
        };
    }

    private static void validateField(String fieldName, QueryOperator operator) {
        if (!ALLOWED_FIELDS.containsKey(fieldName)) {
            throw new BadRequestException(ErrorMessage.BookDefinition.ERR_INVALID_FIELD, fieldName);
        }

        if (!ALLOWED_FIELDS.get(fieldName).contains(operator)) {
            throw new BadRequestException(ErrorMessage.INVALID_OPERATOR_NOT_SUPPORTED, operator, fieldName);
        }
    }

    public static Specification<BookDefinition> createSpecification(QueryFilter input) {
        return (root, query, criteriaBuilder) -> {
            String fieldName = input.getField();
            QueryOperator operator = input.getOperator();
            validateField(fieldName, operator);

            Path<?> path = null;

            if ("author".equalsIgnoreCase(fieldName)) {
                Join<BookDefinition, BookAuthor> bookAuthorJoin = root.join(BookDefinition_.bookAuthors, JoinType.LEFT);
                Join<BookAuthor, Author> authorJoin = bookAuthorJoin.join(BookAuthor_.author, JoinType.LEFT);
                path = authorJoin.get(Author_.fullName);
            }

            if ("publisher".equalsIgnoreCase(fieldName)) {
                Join<BookDefinition, Publisher> publisherJoin = root.join(BookDefinition_.publisher, JoinType.LEFT);
                path = publisherJoin.get(Publisher_.name);
            }

            if (path == null) {
                path = root.get(fieldName);
            }

            return switch (operator) {
                case EQUALS -> criteriaBuilder.equal(path, castToRequiredType(path.getJavaType(), input.getValue()));

                case NOT_EQUALS ->
                        criteriaBuilder.notEqual(path, castToRequiredType(path.getJavaType(), input.getValue()));

                case GREATER_THAN ->
                        criteriaBuilder.gt(path.as(Number.class), (Number) castToRequiredType(path.getJavaType(), input.getValue()));

                case LESS_THAN ->
                        criteriaBuilder.lt(path.as(Number.class), (Number) castToRequiredType(path.getJavaType(), input.getValue()));

                case LIKE -> criteriaBuilder.like(path.as(String.class), "%" + input.getValue() + "%");

                case IN -> path.in(castToRequiredType(path.getJavaType(), input.getValues()));
            };
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

            if (previousQueryFilter.getJoinType() == com.example.librarymanager.constant.JoinType.OR) {
                specification = specification.or(nextSpecification);
            } else {
                specification = specification.and(nextSpecification);
            }
        }
        return specification;
    }

    public static Specification<BookDefinition> filterBookDefinitions(BookDefinitionFilter filters) {
        return (root, query, builder) -> {
            query.distinct(true);

            Predicate predicate = builder.conjunction();

            if (filters.getBookCode() != null && StringUtils.isNotBlank(filters.getBookCode())) {
                Join<BookDefinition, Book> bookJoin = root.join(BookDefinition_.books, JoinType.LEFT);
                predicate = builder.and(predicate, builder.equal(bookJoin.get(Book_.bookCode), filters.getBookCode()));
            }

            if (filters.getTitle() != null && StringUtils.isNotBlank(filters.getTitle())) {
                predicate = builder.and(predicate, builder.like(root.get(BookDefinition_.title), "%" + filters.getTitle() + "%"));
            }

            if (filters.getKeyword() != null && StringUtils.isNotBlank(filters.getKeyword())) {
                predicate = builder.and(predicate, builder.like(root.get(BookDefinition_.keywords), "%" + filters.getKeyword() + "%"));
            }

            if (filters.getPublishingYear() != null) {
                predicate = builder.and(predicate, builder.equal(root.get(BookDefinition_.publishingYear), filters.getPublishingYear()));
            }

            if (filters.getAuthor() != null && StringUtils.isNotBlank(filters.getAuthor())) {
                Join<BookDefinition, BookAuthor> bookAuthorJoin = root.join(BookDefinition_.bookAuthors, JoinType.LEFT);
                Join<BookAuthor, Author> authorJoin = bookAuthorJoin.join(BookAuthor_.author, JoinType.LEFT);

                predicate = builder.and(predicate, builder.like(authorJoin.get(Author_.fullName), "%" + filters.getAuthor() + "%"));
            }

            return predicate;
        };
    }
}
