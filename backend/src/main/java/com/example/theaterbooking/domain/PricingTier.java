package com.example.theaterbooking.domain;

public enum PricingTier {
    SILVER(100),
    GOLD(150),
    PLATINUM(200);

    private final int price;

    PricingTier(int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public static PricingTier fromRow(int rowNumber) {
        if (rowNumber >= 1 && rowNumber <= 2) {
            return SILVER;
        }
        if (rowNumber >= 3 && rowNumber <= 4) {
            return GOLD;
        }
        if (rowNumber >= 5 && rowNumber <= 6) {
            return PLATINUM;
        }
        throw new IllegalArgumentException("Row number must be between 1 and 6");
    }
}
