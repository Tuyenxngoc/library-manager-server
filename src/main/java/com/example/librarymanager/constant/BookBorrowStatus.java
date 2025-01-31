package com.example.librarymanager.constant;

public enum BookBorrowStatus {
    NOT_RETURNED("Chưa trả"),
    RETURNED("Đã trả"),
    LOST("Báo mất");

    private final String name;

    BookBorrowStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
