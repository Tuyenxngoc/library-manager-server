package com.example.librarymanager.domain.specification;

import com.example.librarymanager.constant.BookBorrowStatus;
import com.example.librarymanager.domain.dto.filter.TimeFilter;
import com.example.librarymanager.domain.entity.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class BookBorrowSpecification {

    public static Specification<BookBorrow> filterBookBorrows(List<BookBorrowStatus> status) {
        return (root, query, builder) -> {
            query.distinct(true);
            Predicate predicate = builder.conjunction();

            if (status != null && !status.isEmpty()) {
                predicate = builder.and(predicate, root.get(BookBorrow_.status).in(status));
            }

            return predicate;
        };
    }

    public static Specification<BookBorrow> filterBookBorrows(TimeFilter timeFilter) {
        return (root, query, builder) -> {
            query.distinct(true);
            Predicate predicate = builder.conjunction();

            if (timeFilter != null) {
                if (timeFilter.getStartDate() != null) {
                    predicate = builder.and(predicate,
                            builder.greaterThanOrEqualTo(root.get(BookBorrow_.returnDate), timeFilter.getStartDate()));
                }

                if (timeFilter.getEndDate() != null) {
                    predicate = builder.and(predicate,
                            builder.lessThanOrEqualTo(root.get(BookBorrow_.returnDate), timeFilter.getEndDate()));
                }
            }

            return predicate;
        };
    }

    public static Specification<BookBorrow> filterBookBorrows(String keyword, String searchBy) {
        return (root, query, builder) -> {
            query.distinct(true);

            Predicate predicate = builder.conjunction();

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                switch (searchBy) {
                    case BorrowReceipt_.RECEIPT_NUMBER -> {
                        Join<BookBorrow, BorrowReceipt> borrowReceiptJoin = root.join(BookBorrow_.borrowReceipt);
                        predicate = builder.and(predicate, builder.like(borrowReceiptJoin.get(BorrowReceipt_.receiptNumber), "%" + keyword + "%"));
                    }

                    case Reader_.FULL_NAME -> {
                        Join<BookBorrow, BorrowReceipt> borrowReceiptJoin = root.join(BookBorrow_.borrowReceipt);
                        Join<BorrowReceipt, Reader> readerJoin = borrowReceiptJoin.join(BorrowReceipt_.reader);
                        predicate = builder.and(predicate, builder.like(readerJoin.get(Reader_.fullName), "%" + keyword + "%"));
                    }

                    case Reader_.CARD_NUMBER -> {
                        Join<BookBorrow, BorrowReceipt> borrowReceiptJoin = root.join(BookBorrow_.borrowReceipt);
                        Join<BorrowReceipt, Reader> readerJoin = borrowReceiptJoin.join(BorrowReceipt_.reader);
                        predicate = builder.and(predicate, builder.like(readerJoin.get(Reader_.cardNumber), "%" + keyword + "%"));
                    }
                }
            }

            return predicate;
        };
    }

}
