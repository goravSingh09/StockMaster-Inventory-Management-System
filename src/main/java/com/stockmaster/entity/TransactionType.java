package com.stockmaster.entity;

public enum TransactionType {
    STOCK_IN("Stock In / Purchase Receipt"),
    STOCK_OUT("Stock Out / Manual Adjustment"),
    SALE("Sale Customer Checkout"),
    RETURN("Customer / Supplier Return"),
    INITIAL("Initial Stock Setup");

    private final String displayName;

    TransactionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
