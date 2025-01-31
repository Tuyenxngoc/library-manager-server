package com.example.librarymanager.constant;

public enum CardType {
    STUDENT("Thẻ sinh viên"),
    TEACHER("Thẻ giáo viên"),
    OTHER("Khác");

    private final String displayName;

    CardType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}