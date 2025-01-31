package com.example.librarymanager.constant;

public enum BookCondition {
    AVAILABLE("Sách có sẵn"),
    ON_LOAN("Sách đang mượn"),
    LOST("Sách bị mất");

    private final String name;

    BookCondition(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
