package com.example.librarymanager.domain.specification;

import com.example.librarymanager.constant.BookCondition;
import com.example.librarymanager.domain.entity.Book;
import com.example.librarymanager.domain.entity.BookDefinition;
import com.example.librarymanager.domain.entity.BookDefinition_;
import com.example.librarymanager.domain.entity.Book_;
import com.example.librarymanager.util.SpecificationsUtil;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

    public static Specification<Book> filterBooks(String keyword, String searchBy, BookCondition bookCondition) {
        return (root, query, builder) -> {
            query.distinct(true);

            Predicate predicate = builder.conjunction();

            predicate = builder.and(predicate, builder.isNull(root.get(Book_.exportReceipt)));

            if (bookCondition != null) {
                predicate = builder.and(predicate, builder.equal(root.get(Book_.bookCondition), bookCondition));
            }

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                switch (searchBy) {
                    case Book_.ID -> predicate = builder.and(predicate, builder.equal(root.get(Book_.ID),
                            SpecificationsUtil.castToRequiredType(root.get(Book_.id).getJavaType(), keyword)));

                    case Book_.BOOK_CODE ->
                            predicate = builder.and(predicate, builder.like(root.get(Book_.bookCode), "%" + keyword + "%"));

                    case BookDefinition_.TITLE -> {
                        Join<Book, BookDefinition> bookDefinitionBookJoin = root.join(Book_.bookDefinition, jakarta.persistence.criteria.JoinType.INNER);
                        predicate = builder.and(predicate, builder.like(bookDefinitionBookJoin.get(BookDefinition_.title), "%" + keyword + "%"));
                    }
                }
            }
            return predicate;
        };
    }

}
