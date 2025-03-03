package com.example.librarymanager.domain.specification;

import com.example.librarymanager.constant.BorrowStatus;
import com.example.librarymanager.domain.entity.BorrowReceipt;
import com.example.librarymanager.domain.entity.BorrowReceipt_;
import com.example.librarymanager.domain.entity.Reader;
import com.example.librarymanager.domain.entity.Reader_;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

public class BorrowReceiptSpecification {

    public static Specification<BorrowReceipt> filterBorrowReceipts(String keyword, String searchBy, BorrowStatus status) {
        return (root, query, builder) -> {
            query.distinct(true);

            Predicate predicate = builder.conjunction();

            if (status != null) {
                predicate = builder.and(predicate, builder.equal(root.get(BorrowReceipt_.status), status));
            }

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                switch (searchBy) {
                    case BorrowReceipt_.RECEIPT_NUMBER ->
                            predicate = builder.and(predicate, builder.like(root.get(BorrowReceipt_.receiptNumber), "%" + keyword + "%"));

                    case Reader_.FULL_NAME -> {
                        Join<BorrowReceipt, Reader> readerJoin = root.join(BorrowReceipt_.reader);
                        predicate = builder.and(predicate, builder.like(readerJoin.get(Reader_.fullName), "%" + keyword + "%"));
                    }

                    case Reader_.CARD_NUMBER -> {
                        Join<BorrowReceipt, Reader> readerJoin = root.join(BorrowReceipt_.reader);
                        predicate = builder.and(predicate, builder.like(readerJoin.get(Reader_.cardNumber), "%" + keyword + "%"));
                    }
                }
            }

            return predicate;
        };
    }

    public static Specification<BorrowReceipt> filterBorrowReceiptsByReader(String cardNumber) {
        return (root, query, builder) -> {
            query.distinct(true);

            Predicate predicate = builder.conjunction();

            Join<BorrowReceipt, Reader> borrowReceiptReaderJoin = root.join(BorrowReceipt_.reader, JoinType.INNER);
            predicate = builder.and(predicate, builder.equal(borrowReceiptReaderJoin.get(Reader_.cardNumber), cardNumber));

            return predicate;
        };
    }

}
