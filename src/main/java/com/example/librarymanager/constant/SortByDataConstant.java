package com.example.librarymanager.constant;

public enum SortByDataConstant implements SortByInterface {

    USER {
        @Override
        public String getSortBy(String sortBy) {
            return switch (sortBy) {
                case "username" -> "username";
                case "fullName" -> "fullName";
                case "email" -> "email";
                case "phoneNumber" -> "phoneNumber";
                case "status" -> "status";
                case "address" -> "address";
                default -> "id";
            };
        }
    },

    AUTHOR {
        @Override
        public String getSortBy(String sortBy) {
            return switch (sortBy) {
                case "code" -> "code";
                case "fullName" -> "fullName";
                case "penName" -> "penName";
                case "gender" -> "gender";
                case "dateOfBirth" -> "dateOfBirth";
                case "address" -> "address";
                default -> "id";
            };
        }
    },

    CATEGORY_GROUP {
        @Override
        public String getSortBy(String sortBy) {
            return switch (sortBy) {
                case "groupName" -> "groupName";
                case "activeFlag" -> "activeFlag";
                default -> "id";
            };
        }
    },

    CATEGORY {
        @Override
        public String getSortBy(String sortBy) {
            return switch (sortBy) {
                case "categoryName" -> "categoryName";
                case "categoryCode" -> "categoryCode";
                case "activeFlag" -> "activeFlag";
                default -> "id";
            };
        }
    },

    BOOK_SET {
        @Override
        public String getSortBy(String sortBy) {
            return switch (sortBy) {
                case "name" -> "name";
                case "activeFlag" -> "activeFlag";
                default -> "id";
            };
        }
    },

    PUBLISHER {
        @Override
        public String getSortBy(String sortBy) {
            return switch (sortBy) {
                case "code" -> "code";
                case "name" -> "name";
                case "address" -> "address";
                case "city" -> "city";
                case "notes" -> "notes";
                case "activeFlag" -> "activeFlag";
                default -> "id";
            };
        }
    },

    BOOK_DEFINITION {
        @Override
        public String getSortBy(String sortBy) {
            return switch (sortBy) {
                case "title" -> "title";
                case "bookCode" -> "bookCode";
                case "activeFlag" -> "activeFlag";
                default -> "id";
            };
        }
    },

    CLASSIFICATION_SYMBOL {
        @Override
        public String getSortBy(String sortBy) {
            return switch (sortBy) {
                case "name" -> "name";
                case "code" -> "code";
                case "level" -> "level";
                case "activeFlag" -> "activeFlag";
                default -> "id";
            };
        }
    },

    LOG {
        @Override
        public String getSortBy(String sortBy) {
            return switch (sortBy) {
                case "id" -> "id";
                case "feature" -> "feature";
                case "event" -> "event";
                case "content" -> "content";
                default -> "timestamp";
            };
        }
    },

    IMPORT_RECEIPT {
        @Override
        public String getSortBy(String sortBy) {
            return switch (sortBy) {
                case "receiptNumber" -> "receiptNumber";
                case "importDate" -> "importDate";
                case "fundingSource" -> "fundingSource";
                default -> "id";
            };
        }
    },

    EXPORT_RECEIPT {
        @Override
        public String getSortBy(String sortBy) {
            return switch (sortBy) {
                case "receiptNumber" -> "receiptNumber";
                default -> "id";
            };
        }
    },

    BOOK {
        @Override
        public String getSortBy(String sortBy) {
            return switch (sortBy) {
                case "bookCode" -> "bookCode";
                case "bookCondition" -> "bookCondition";
                case "bookStatus" -> "bookStatus";
                default -> "id";
            };
        }
    },

    NEWS_ARTICLE {
        @Override
        public String getSortBy(String sortBy) {
            return switch (sortBy) {
                case "title" -> "title";
                case "newsType" -> "newsType";
                default -> "id";
            };
        }
    },

    USER_GROUP {
        @Override
        public String getSortBy(String sortBy) {
            return switch (sortBy) {
                case "code" -> "code";
                case "name" -> "name";
                default -> "id";
            };
        }
    },

    READER {
        @Override
        public String getSortBy(String sortBy) {
            return switch (sortBy) {
                case "cardNumber" -> "cardNumber";
                case "cardType" -> "cardType";
                case "fullName" -> "fullName";
                case "dateOfBirth" -> "dateOfBirth";
                case "gender" -> "gender";
                case "status" -> "status";
                default -> "id";
            };
        }
    },

    LIBRARY_VISIT {
        @Override
        public String getSortBy(String sortBy) {
            return switch (sortBy) {
                case "cardNumber" -> "cardNumber";
                case "exitTime" -> "exitTime";
                default -> "entryTime";
            };
        }
    },

    READER_VIOLATION {
        @Override
        public String getSortBy(String sortBy) {
            return switch (sortBy) {
                case "violationDetails" -> "violationDetails";
                case "penaltyForm" -> "penaltyForm";
                case "penaltyDate" -> "penaltyDate";
                case "endDate" -> "endDate";
                case "fineAmount" -> "fineAmount";
                default -> "id";
            };
        }
    },

    BORROW_RECEIPT {
        @Override
        public String getSortBy(String sortBy) {
            return switch (sortBy) {
                case "receiptNumber" -> "receiptNumber";
                case "borrowDate" -> "borrowDate";
                case "dueDate" -> "dueDate";
                case "returnDate" -> "returnDate";
                case "status" -> "status";
                case "note" -> "note";
                default -> "id";
            };
        }
    },

    BORROW_REQUEST {
        @Override
        public String getSortBy(String sortBy) {
            return "id";
        }
    },

    BOOK_BORROW {
        @Override
        public String getSortBy(String sortBy) {
            return "id";
        }
    },

}
