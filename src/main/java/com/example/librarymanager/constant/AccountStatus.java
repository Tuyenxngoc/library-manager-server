package com.example.librarymanager.constant;

public enum AccountStatus {
    ACTIVATED("Đã kích hoạt"),
    DEACTIVATED("Chưa kích hoạt"),
    SUSPENDED("Tạm dừng");

    private final String name;

    AccountStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}