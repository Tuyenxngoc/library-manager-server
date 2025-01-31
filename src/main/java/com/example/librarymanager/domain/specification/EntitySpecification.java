package com.example.librarymanager.domain.specification;

import com.example.librarymanager.constant.*;
import com.example.librarymanager.domain.dto.filter.LibraryVisitFilter;
import com.example.librarymanager.domain.dto.filter.LogFilter;
import com.example.librarymanager.domain.dto.filter.TimeFilter;
import com.example.librarymanager.domain.entity.*;
import com.example.librarymanager.util.SpecificationsUtil;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.ListJoin;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class EntitySpecification {

    public static Specification<Author> filterAuthors(String keyword, String searchBy, Boolean activeFlag) {
        return (root, query, builder) -> {
            query.distinct(true);

            Predicate predicate = builder.conjunction();

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                switch (searchBy) {
                    case Author_.CODE ->
                            predicate = builder.and(predicate, builder.like(root.get(Author_.code), "%" + keyword + "%"));

                    case Author_.FULL_NAME ->
                            predicate = builder.and(predicate, builder.like(root.get(Author_.fullName), "%" + keyword + "%"));
                }
            }

            if (activeFlag != null) {
                predicate = builder.and(predicate, builder.equal(root.get(Author_.activeFlag), activeFlag));
            }

            return predicate;
        };
    }

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

    public static Specification<CategoryGroup> filterCategoryGroups(String keyword, String searchBy, Boolean activeFlag) {
        return (root, query, builder) -> {
            query.distinct(true);

            Predicate predicate = builder.conjunction();

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                switch (searchBy) {
                    case CategoryGroup_.GROUP_NAME ->
                            predicate = builder.and(predicate, builder.like(root.get(CategoryGroup_.groupName), "%" + keyword + "%"));
                }
            }

            if (activeFlag != null) {
                predicate = builder.and(predicate, builder.equal(root.get(CategoryGroup_.activeFlag), activeFlag));
            }

            return predicate;
        };
    }

    public static Specification<BookSet> filterBookSets(String keyword, String searchBy, Boolean activeFlag) {
        return (root, query, builder) -> {
            query.distinct(true);

            Predicate predicate = builder.conjunction();

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                switch (searchBy) {
                    case BookSet_.NAME ->
                            predicate = builder.and(predicate, builder.like(root.get(BookSet_.name), "%" + keyword + "%"));
                }
            }

            if (activeFlag != null) {
                predicate = builder.and(predicate, builder.equal(root.get(BookSet_.activeFlag), activeFlag));
            }

            return predicate;
        };
    }

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

    public static Specification<Log> filterLogs(String keyword, String searchBy, LogFilter logFilter) {
        return (root, query, builder) -> {
            query.distinct(true);

            Predicate predicate = builder.conjunction();

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                switch (searchBy) {
                    case Log_.USER -> {
                        Join<Log, User> userJoin = root.join(Log_.user);
                        predicate = builder.and(predicate, builder.like(userJoin.get(User_.username), "%" + keyword + "%"));
                    }

                    case Log_.FEATURE ->
                            predicate = builder.and(predicate, builder.like(root.get(Log_.feature), "%" + keyword + "%"));

                    case Log_.EVENT ->
                            predicate = builder.and(predicate, builder.like(root.get(Log_.event), "%" + keyword + "%"));

                    case Log_.CONTENT ->
                            predicate = builder.and(predicate, builder.like(root.get(Log_.content), "%" + keyword + "%"));
                }
            }

            if (logFilter != null) {
                if (logFilter.getStartDate() != null) {
                    predicate = builder.and(predicate, builder.greaterThanOrEqualTo(root.get(Log_.timestamp), logFilter.getStartDate().atStartOfDay()));
                }

                if (logFilter.getEndDate() != null) {
                    predicate = builder.and(predicate, builder.lessThanOrEqualTo(root.get(Log_.timestamp), logFilter.getEndDate().atTime(23, 59, 59)));
                }
            }

            return predicate;
        };
    }

    public static Specification<ImportReceipt> filterImportReceipts(String keyword, String searchBy) {
        return (root, query, builder) -> {
            query.distinct(true);

            Predicate predicate = builder.conjunction();

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                switch (searchBy) {
                    case ImportReceipt_.ID ->
                            predicate = builder.and(predicate, builder.equal(root.get(ImportReceipt_.ID),
                                    SpecificationsUtil.castToRequiredType(root.get(ImportReceipt_.id).getJavaType(), keyword)));

                    case ImportReceipt_.RECEIPT_NUMBER ->
                            predicate = builder.and(predicate, builder.like(root.get(ImportReceipt_.receiptNumber), "%" + keyword + "%"));

                    case ImportReceipt_.FUNDING_SOURCE ->
                            predicate = builder.and(predicate, builder.like(root.get(ImportReceipt_.fundingSource), "%" + keyword + "%"));

                    case ImportReceipt_.IMPORT_REASON ->
                            predicate = builder.and(predicate, builder.like(root.get(ImportReceipt_.importReason), "%" + keyword + "%"));

                    case ImportReceipt_.GENERAL_RECORD_NUMBER ->
                            predicate = builder.and(predicate, builder.like(root.get(ImportReceipt_.generalRecordNumber), "%" + keyword + "%"));
                }
            }
            return predicate;
        };
    }

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
                        Join<Book, BookDefinition> bookDefinitionBookJoin = root.join(Book_.bookDefinition, JoinType.INNER);
                        predicate = builder.and(predicate, builder.like(bookDefinitionBookJoin.get(BookDefinition_.title), "%" + keyword + "%"));
                    }
                }
            }
            return predicate;
        };
    }

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

    public static Specification<UserGroup> filterUserGroups(String keyword, String searchBy, Boolean activeFlag) {
        return (root, query, builder) -> {
            query.distinct(true);

            Predicate predicate = builder.conjunction();

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                switch (searchBy) {
                    case UserGroup_.CODE ->
                            predicate = builder.and(predicate, builder.like(root.get(UserGroup_.code), "%" + keyword + "%"));

                    case UserGroup_.NAME ->
                            predicate = builder.and(predicate, builder.like(root.get(UserGroup_.name), "%" + keyword + "%"));
                }
            }

            if (activeFlag != null) {
                predicate = builder.and(predicate, builder.equal(root.get(UserGroup_.activeFlag), activeFlag));
            }

            return predicate;
        };
    }

    public static Specification<User> filterUsers(String keyword, String searchBy) {
        return (root, query, builder) -> {
            query.distinct(true);

            Predicate predicate = builder.conjunction();

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                switch (searchBy) {
                    case User_.USERNAME ->
                            predicate = builder.and(predicate, builder.like(root.get(User_.username), "%" + keyword + "%"));

                    case User_.FULL_NAME ->
                            predicate = builder.and(predicate, builder.like(root.get(User_.fullName), "%" + keyword + "%"));
                }
            }

            return predicate;
        };
    }

    public static Specification<ExportReceipt> filterExportReceipts(String keyword, String searchBy) {
        return (root, query, builder) -> {
            query.distinct(true);

            Predicate predicate = builder.conjunction();

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                switch (searchBy) {
                    case ExportReceipt_.ID ->
                            predicate = builder.and(predicate, builder.equal(root.get(ExportReceipt_.ID),
                                    SpecificationsUtil.castToRequiredType(root.get(ExportReceipt_.id).getJavaType(), keyword)));

                    case ExportReceipt_.RECEIPT_NUMBER ->
                            predicate = builder.and(predicate, builder.like(root.get(ExportReceipt_.receiptNumber), "%" + keyword + "%"));

                    case ExportReceipt_.EXPORT_REASON ->
                            predicate = builder.and(predicate, builder.like(root.get(ExportReceipt_.exportReason), "%" + keyword + "%"));
                }
            }
            return predicate;
        };
    }

    public static Specification<Reader> filterReaders(String keyword, String searchBy) {
        return (root, query, builder) -> {
            query.distinct(true);

            Predicate predicate = builder.conjunction();

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                switch (searchBy) {
                    case Reader_.CARD_NUMBER ->
                            predicate = builder.and(predicate, builder.like(root.get(Reader_.cardNumber), "%" + keyword + "%"));

                    case Reader_.FULL_NAME ->
                            predicate = builder.and(predicate, builder.like(root.get(Reader_.fullName), "%" + keyword + "%"));

                    case Reader_.STATUS -> {
                        try {
                            CardStatus statusEnum = CardStatus.valueOf(keyword.toUpperCase());
                            predicate = builder.and(predicate, builder.equal(root.get(Reader_.status), statusEnum));
                        } catch (IllegalArgumentException ignored) {
                        }
                    }
                }
            }

            return predicate;
        };
    }

    public static Specification<LibraryVisit> filterLibraryVisits(String keyword, String searchBy, LibraryVisitFilter filter) {
        return (root, query, builder) -> {
            query.distinct(true);

            Predicate predicate = builder.conjunction();

            if (filter == null || (filter.getStartDate() == null && filter.getEndDate() == null)) {
                LocalDate today = LocalDate.now();
                LocalDateTime startOfDay = today.atStartOfDay();
                LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

                // Thêm điều kiện lọc theo thời gian trong ngày hôm nay
                predicate = builder.and(predicate,
                        builder.between(root.get(LibraryVisit_.entryTime), startOfDay, endOfDay)
                );
            } else {
                if (filter.getStartDate() != null) {
                    predicate = builder.and(predicate,
                            builder.greaterThanOrEqualTo(root.get(LibraryVisit_.entryTime), filter.getStartDate().atStartOfDay()));
                }
                if (filter.getEndDate() != null) {
                    predicate = builder.and(predicate,
                            builder.lessThanOrEqualTo(root.get(LibraryVisit_.entryTime), filter.getEndDate().atTime(23, 59, 59)));
                }
            }

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                switch (searchBy) {
                    case "cardNumber" -> {
                        Join<LibraryVisit, Reader> readerJoin = root.join(LibraryVisit_.reader);
                        predicate = builder.and(predicate, builder.like(readerJoin.get(Reader_.cardNumber), "%" + keyword + "%"));
                    }

                    case "fullName" -> {
                        Join<LibraryVisit, Reader> readerJoin = root.join(LibraryVisit_.reader);
                        predicate = builder.and(predicate, builder.like(readerJoin.get(Reader_.fullName), "%" + keyword + "%"));
                    }
                }
            }
            return predicate;
        };
    }

    public static Specification<ReaderViolation> filterReaderViolations(String keyword, String searchBy) {
        return (root, query, builder) -> {
            query.distinct(true);

            Predicate predicate = builder.conjunction();

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                switch (searchBy) {
                    case Reader_.CARD_NUMBER -> {
                        Join<ReaderViolation, Reader> readerJoin = root.join(ReaderViolation_.reader);
                        predicate = builder.and(predicate,
                                builder.like(readerJoin.get(Reader_.cardNumber), "%" + keyword + "%"));
                    }

                    case Reader_.FULL_NAME -> {
                        Join<ReaderViolation, Reader> readerJoin = root.join(ReaderViolation_.reader);
                        predicate = builder.and(predicate,
                                builder.like(readerJoin.get(Reader_.fullName), "%" + keyword + "%"));
                    }

                    case ReaderViolation_.PENALTY_FORM -> {
                        try {
                            PenaltyForm penaltyForm = PenaltyForm.valueOf(keyword.toUpperCase());
                            predicate = builder.and(predicate, builder.equal(root.get(ReaderViolation_.penaltyForm), penaltyForm));
                        } catch (IllegalArgumentException ignored) {
                        }
                    }
                }
            }

            return predicate;
        };
    }

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

    public static Specification<Cart> filterCarts(String keyword, String searchBy) {
        return (root, query, builder) -> {
            query.distinct(true);

            Predicate predicate = builder.conjunction();

            ListJoin<Cart, CartDetail> cartCartDetailListJoin = root.join(Cart_.cartDetails, JoinType.INNER);
            predicate = builder.and(predicate, builder.greaterThan(cartCartDetailListJoin.get(CartDetail_.borrowTo), LocalDateTime.now()));

            if (StringUtils.isNotBlank(keyword) && StringUtils.isNotBlank(searchBy)) {
                Join<Cart, Reader> readerJoin = root.join(Cart_.reader);

                switch (searchBy) {
                    case Reader_.CARD_NUMBER:
                        predicate = builder.and(predicate, builder.like(readerJoin.get(Reader_.cardNumber), "%" + keyword + "%"));
                        break;

                    case Reader_.FULL_NAME:
                        predicate = builder.and(predicate, builder.like(readerJoin.get(Reader_.fullName), "%" + keyword + "%"));
                        break;
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
