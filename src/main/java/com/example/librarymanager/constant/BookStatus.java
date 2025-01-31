package com.example.librarymanager.constant;

public enum BookStatus {
    USABLE("Sử dụng được"),
    DAMAGED("Rách nát"),
    OUTDATED("Lạc hậu"),
    INFESTED("Mối mọt"),
    OBSOLETE_PROGRAM("Chương trình cũ");

    private final String name;

    BookStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
