package com.example.librarymanager.domain.specification;

import com.example.librarymanager.domain.entity.*;
import jakarta.persistence.criteria.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class BookDefinitionSpecification {

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

}
