package com.example.librarymanager.constant;

public enum CardStatus {
    ACTIVE("Đã kích hoạt"),
    INACTIVE("Chưa kích hoạt"),
    SUSPENDED("Tạm dừng"),
    REVOKED("Thu hồi thẻ");

    private final String name;

    CardStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
