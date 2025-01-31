package com.example.librarymanager.constant;

public enum BorrowStatus {
    NOT_RETURNED("Chưa trả"),
    RETURNED("Đã trả"),
    PARTIALLY_RETURNED("Chưa trả đủ"),
    OVERDUE("Quá hạn");

    private final String name;

    BorrowStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
