package com.example.librarymanager.constant;

public class UrlConstant {

    public static final String ADMIN_URL = "/admin";

    public static class Auth {
        private static final String PRE_FIX = "/auth";

        public static final String LOGIN = PRE_FIX + "/login";
        public static final String LOGOUT = PRE_FIX + "/logout";
        public static final String FORGET_PASSWORD = PRE_FIX + "/forget-password";
        public static final String CHANGE_PASSWORD = PRE_FIX + "/change-password";
        public static final String REFRESH_TOKEN = PRE_FIX + "/refresh-token";

        public static final String ADMIN_LOGIN = ADMIN_URL + LOGIN;
        public static final String ADMIN_FORGET_PASSWORD = ADMIN_URL + FORGET_PASSWORD;
        public static final String ADMIN_CHANGE_PASSWORD = ADMIN_URL + CHANGE_PASSWORD;
    }

    public static class User {
        private static final String PRE_FIX = "/users";

        public static final String CREATE = PRE_FIX;
        public static final String GET_ALL = PRE_FIX;
        public static final String GET_BY_ID = PRE_FIX + "/{id}";
        public static final String UPDATE = PRE_FIX + "/{id}";
        public static final String DELETE = PRE_FIX + "/{id}";
        public static final String UPLOAD_IMAGES = PRE_FIX + "/upload-images";

        public static final String GET_CURRENT_USER = PRE_FIX + "/current";
    }

    public static class Role {
        private static final String PRE_FIX = "/roles";

        public static final String CREATE = PRE_FIX;
        public static final String GET_ALL = PRE_FIX;
        public static final String GET_BY_ID = PRE_FIX + "/{id}";
        public static final String UPDATE = PRE_FIX + "/{id}";
        public static final String DELETE = PRE_FIX + "/{id}";
    }

    public static class Author {
        private static final String PRE_FIX = "/authors";

        public static final String CREATE = PRE_FIX;
        public static final String GET_ALL = PRE_FIX;
        public static final String GET_BY_ID = PRE_FIX + "/{id}";
        public static final String UPDATE = PRE_FIX + "/{id}";
        public static final String DELETE = PRE_FIX + "/{id}";
        public static final String TOGGLE_ACTIVE = PRE_FIX + "/{id}/toggle-active";
    }

    public static class Category {
        private static final String PRE_FIX = "/categories";

        public static final String CREATE = PRE_FIX;
        public static final String GET_ALL = PRE_FIX;
        public static final String GET_BY_ID = PRE_FIX + "/{id}";
        public static final String UPDATE = PRE_FIX + "/{id}";
        public static final String DELETE = PRE_FIX + "/{id}";
        public static final String TOGGLE_ACTIVE = PRE_FIX + "/{id}/toggle-active";
    }

    public static class CategoryGroup {
        private static final String PRE_FIX = "/category-groups";

        public static final String CREATE = PRE_FIX;
        public static final String GET_ALL = PRE_FIX;
        public static final String GET_BY_ID = PRE_FIX + "/{id}";
        public static final String UPDATE = PRE_FIX + "/{id}";
        public static final String DELETE = PRE_FIX + "/{id}";
        public static final String TOGGLE_ACTIVE = PRE_FIX + "/{id}/toggle-active";
        public static final String GET_TREE = PRE_FIX + "/tree";
    }

    public static class BookSet {
        private static final String PRE_FIX = "/book-sets";

        public static final String CREATE = PRE_FIX;
        public static final String GET_ALL = PRE_FIX;
        public static final String GET_BY_ID = PRE_FIX + "/{id}";
        public static final String UPDATE = PRE_FIX + "/{id}";
        public static final String DELETE = PRE_FIX + "/{id}";
        public static final String TOGGLE_ACTIVE = PRE_FIX + "/{id}/toggle-active";
    }

    public static class BorrowReceipt {
        private static final String PRE_FIX = "/borrow-receipts";

        public static final String GET_BY_READER = PRE_FIX;

        public static final String CREATE = ADMIN_URL + PRE_FIX;
        public static final String GET_ALL = ADMIN_URL + PRE_FIX;
        public static final String GET_BY_ID = ADMIN_URL + PRE_FIX + "/{id}";
        public static final String UPDATE = ADMIN_URL + PRE_FIX + "/{id}";
        public static final String DELETE = ADMIN_URL + PRE_FIX + "/{id}";
        public static final String PRINT = ADMIN_URL + PRE_FIX + "/print";
        public static final String PRINT_OVERDUE_LIST = ADMIN_URL + PRE_FIX + "/print-overdue";
        public static final String GET_BY_CART_ID = ADMIN_URL + PRE_FIX + "/cart/{id}";
        public static final String GET_DETAILS_BY_ID = ADMIN_URL + PRE_FIX + "/details/{id}";
        public static final String GENERATE_RECEIPT_NUMBER = ADMIN_URL + PRE_FIX + "/generate-receipt-number";
        public static final String CANCEL_RETURN = ADMIN_URL + PRE_FIX + "/cancel-return";
        public static final String EXPORT_RETURN_DATA = ADMIN_URL + PRE_FIX + "/export-return-data";
    }

    public static class BookBorrow {
        private static final String PRE_FIX = "/book-borrows";

        public static final String CREATE = ADMIN_URL + PRE_FIX;
        public static final String GET_ALL = ADMIN_URL + PRE_FIX;
        public static final String GET_BY_ID = ADMIN_URL + PRE_FIX + "/{id}";
        public static final String UPDATE = ADMIN_URL + PRE_FIX + "/{id}";
        public static final String DELETE = ADMIN_URL + PRE_FIX + "/{id}";

        public static final String RETURN_BOOKS = ADMIN_URL + PRE_FIX + "/return-books";
        public static final String REPORT_LOST = ADMIN_URL + PRE_FIX + "/report-lost";
    }

    public static class Publisher {
        private static final String PRE_FIX = "/publishers";

        public static final String CREATE = PRE_FIX;
        public static final String GET_ALL = PRE_FIX;
        public static final String GET_BY_ID = PRE_FIX + "/{id}";
        public static final String UPDATE = PRE_FIX + "/{id}";
        public static final String DELETE = PRE_FIX + "/{id}";
        public static final String TOGGLE_ACTIVE = PRE_FIX + "/{id}/toggle-active";
    }

    public static class BookDefinition {
        private static final String PRE_FIX = "/book-definitions";

        public static final String CREATE = ADMIN_URL + PRE_FIX;
        public static final String GET_ALL = ADMIN_URL + PRE_FIX;
        public static final String GET_BY_IDS = ADMIN_URL + PRE_FIX + "/by-ids";
        public static final String GET_BY_ID = ADMIN_URL + PRE_FIX + "/{id}";
        public static final String UPDATE = ADMIN_URL + PRE_FIX + "/{id}";
        public static final String DELETE = ADMIN_URL + PRE_FIX + "/{id}";
        public static final String TOGGLE_ACTIVE = ADMIN_URL + PRE_FIX + "/{id}/toggle-active";
        public static final String GET_BOOKS = ADMIN_URL + PRE_FIX + "/books";

        public static final String GET_BOOKS_FOR_USER = PRE_FIX + "/books";
        public static final String GET_BOOK_DETAIL_FOR_USER = PRE_FIX + "/books/{id}";
        public static final String ADVANCED_SEARCH = PRE_FIX + "/advanced-search";
        public static final String SEARCH = PRE_FIX + "/search";

        public static final String BOOK_PDF = PRE_FIX + "/pdf";
        public static final String BOOK_LABEL_TYPE_1_PDF = PRE_FIX + "/pdf/label-type-1";
        public static final String BOOK_LABEL_TYPE_2_PDF = PRE_FIX + "/pdf/label-type-2";
        public static final String GET_BOOK_LIST_PDF = PRE_FIX + "/pdf/book-list";
    }

    public static class ClassificationSymbol {
        private static final String PRE_FIX = "/classification-symbols";

        public static final String CREATE = PRE_FIX;
        public static final String GET_ALL = PRE_FIX;
        public static final String GET_BY_ID = PRE_FIX + "/{id}";
        public static final String UPDATE = PRE_FIX + "/{id}";
        public static final String DELETE = PRE_FIX + "/{id}";
        public static final String TOGGLE_ACTIVE = PRE_FIX + "/{id}/toggle-active";
    }

    public static class Log {
        private static final String PRE_FIX = "/logs";

        public static final String GET_ALL = PRE_FIX;
    }

    public static class ImportReceipt {
        private static final String PRE_FIX = "/import-receipts";

        public static final String CREATE = PRE_FIX;
        public static final String GET_ALL = PRE_FIX;
        public static final String GET_BY_ID = PRE_FIX + "/{id}";
        public static final String UPDATE = PRE_FIX + "/{id}";
        public static final String DELETE = PRE_FIX + "/{id}";
        public static final String GENERATE_RECEIPT_NUMBER = PRE_FIX + "/generate-receipt-number";
    }

    public static class ExportReceipt {
        private static final String PRE_FIX = "/export-receipts";

        public static final String CREATE = PRE_FIX;
        public static final String GET_ALL = PRE_FIX;
        public static final String GET_BY_ID = PRE_FIX + "/{id}";
        public static final String UPDATE = PRE_FIX + "/{id}";
        public static final String DELETE = PRE_FIX + "/{id}";
        public static final String GENERATE_RECEIPT_NUMBER = PRE_FIX + "/generate-receipt-number";
    }

    public static class Book {
        private static final String PRE_FIX = "/books";

        public static final String CREATE = PRE_FIX;
        public static final String GET_ALL = PRE_FIX;
        public static final String GET_BY_CODES = PRE_FIX + "/by-codes";
        public static final String GET_BY_IDS = PRE_FIX + "/by-ids";
        public static final String GET_BY_ID = PRE_FIX + "/{id}";
        public static final String UPDATE = PRE_FIX + "/{id}";
        public static final String UPDATE_STATUS = PRE_FIX + "/{id}/status";
        public static final String DELETE = PRE_FIX + "/{id}";

        public static final String BOOK_PDF = PRE_FIX + "/pdf";
        public static final String BOOK_LABEL_TYPE_1_PDF = PRE_FIX + "/pdf/label-type-1";
        public static final String BOOK_LABEL_TYPE_2_PDF = PRE_FIX + "/pdf/label-type-2";
        public static final String GET_BOOK_LIST_PDF = PRE_FIX + "/pdf/book-list";
    }

    public static class NewsArticle {
        private static final String PRE_FIX = "/news-articles";

        public static final String CREATE = ADMIN_URL + PRE_FIX;
        public static final String GET_ALL = ADMIN_URL + PRE_FIX;
        public static final String GET_BY_ID = ADMIN_URL + PRE_FIX + "/{id}";
        public static final String UPDATE = ADMIN_URL + PRE_FIX + "/{id}";
        public static final String DELETE = ADMIN_URL + PRE_FIX + "/{id}";
        public static final String TOGGLE_ACTIVE = ADMIN_URL + PRE_FIX + "/{id}/toggle-active";

        public static final String GET_ALL_FOR_USER = PRE_FIX;
        public static final String GET_BY_ID_FOR_USER = PRE_FIX + "/{id}";

    }

    public static class UserGroup {
        private static final String PRE_FIX = "/user-groups";

        public static final String CREATE = PRE_FIX;
        public static final String GET_ALL = PRE_FIX;
        public static final String GET_BY_ID = PRE_FIX + "/{id}";
        public static final String UPDATE = PRE_FIX + "/{id}";
        public static final String DELETE = PRE_FIX + "/{id}";
        public static final String TOGGLE_ACTIVE = PRE_FIX + "/{id}/toggle-active";
    }

    public static class Reader {
        private static final String PRE_FIX = "/readers";

        public static final String CREATE = PRE_FIX;
        public static final String GET_ALL = PRE_FIX;
        public static final String GET_BY_ID = PRE_FIX + "/{id}";
        public static final String GET_BY_CARD_NUMBER = PRE_FIX + "/card-number/{cardNumber}";
        public static final String UPDATE = PRE_FIX + "/{id}";
        public static final String DELETE = PRE_FIX + "/{id}";
        public static final String PRINT_CARDS = PRE_FIX + "/print-cards";

        public static final String GET_DETAILS = PRE_FIX + "/details";
    }

    public static class LibraryVisit {
        private static final String PRE_FIX = "/library-visits";

        public static final String CREATE = PRE_FIX;
        public static final String GET_ALL = PRE_FIX;
        public static final String GET_BY_ID = PRE_FIX + "/{id}";
        public static final String UPDATE = PRE_FIX + "/{id}";
        public static final String DELETE = PRE_FIX + "/{id}";
        public static final String CLOSE = PRE_FIX + "/close";
    }

    public static class ReaderViolation {
        private static final String PRE_FIX = "/reader-violations";

        public static final String CREATE = PRE_FIX;
        public static final String GET_ALL = PRE_FIX;
        public static final String GET_BY_ID = PRE_FIX + "/{id}";
        public static final String UPDATE = PRE_FIX + "/{id}";
        public static final String DELETE = PRE_FIX + "/{id}";
    }

    public static class Stats {
        private static final String PRE_FIX = "/stats";

        public static final String GET_LIBRARY_STATISTICS = PRE_FIX + "/library";
        public static final String GET_BORROW_STATISTICS = PRE_FIX + "/borrow";
        public static final String GET_LOAN_STATUS = PRE_FIX + "/loan-status";
        public static final String GET_MOST_BORROWED = PRE_FIX + "/most-borrowed";
        public static final String GET_PUBLICATION_STATISTICS = PRE_FIX + "/publication-by-category";
    }

    public static class Cart {
        private static final String PRE_FIX = "/carts";

        public static final String GET_DETAILS = PRE_FIX + "/details";
        public static final String ADD = PRE_FIX + "/add";
        public static final String UPDATE = PRE_FIX + "/update";
        public static final String REMOVE = PRE_FIX + "/remove";
        public static final String CLEAR = PRE_FIX + "/clear";
        public static final String PENDING_BORROW_REQUESTS = PRE_FIX + "/pending-borrow-requests";
    }

    public static class SystemSetting {
        public static final String PRE_FIX = "/system-settings";

        public static final String UPDATE_LIBRARY_RULES = PRE_FIX + "/library-rules";
        public static final String GET_LIBRARY_RULES = PRE_FIX + "/library-rules";

        public static final String GET_ALL_HOLIDAYS = PRE_FIX + "/holidays";
        public static final String ADD_HOLIDAY = PRE_FIX + "/holidays";
        public static final String UPDATE_HOLIDAY = PRE_FIX + "/holidays/{id}";
        public static final String DELETE_HOLIDAY = PRE_FIX + "/holidays/{id}";
        public static final String GET_HOLIDAY_BY_ID = PRE_FIX + "/holidays/{id}";

        public static final String GET_LIBRARY_CONFIG = PRE_FIX + "/library-config";
        public static final String UPDATE_LIBRARY_CONFIG = PRE_FIX + "/library-config";

        public static final String ADD_SLIDE = PRE_FIX + "/slides";
        public static final String GET_ALL_SLIDES = PRE_FIX + "/slides";
        public static final String GET_SLIDE_BY_ID = PRE_FIX + "/slides/{id}";
        public static final String UPDATE_SLIDE = PRE_FIX + "/slides/{id}";
        public static final String DELETE_SLIDE = PRE_FIX + "/slides/{id}";
        public static final String TOGGLE_ACTIVE_SLIDE = PRE_FIX + "/slides/{id}/toggle-active";

        public static final String GET_LIBRARY_INFO = PRE_FIX + "/library-info";
        public static final String UPDATE_LIBRARY_INFO = PRE_FIX + "/library-info";
    }
}
