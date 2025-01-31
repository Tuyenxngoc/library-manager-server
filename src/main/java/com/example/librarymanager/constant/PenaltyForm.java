package com.example.librarymanager.constant;

public enum PenaltyForm {
    CARD_SUSPENSION("Tạm dừng thẻ"),
    CARD_REVOCATION("Thu hồi thẻ"),
    FINE("Phạt tiền");

    private final String description;

    PenaltyForm(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
