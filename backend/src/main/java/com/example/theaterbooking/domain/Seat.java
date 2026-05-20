package com.example.theaterbooking.domain;

public class Seat {
    private final int id;
    private final int rowNumber;
    private final int columnNumber;
    private final PricingTier tier;
    private boolean booked;
    private String bookedBy;

    public Seat(int id, int rowNumber, int columnNumber, PricingTier tier) {
        this.id = id;
        this.rowNumber = rowNumber;
        this.columnNumber = columnNumber;
        this.tier = tier;
        this.booked = false;
    }

    public int getId() {
        return id;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public PricingTier getTier() {
        return tier;
    }

    public int getPrice() {
        return tier.getPrice();
    }

    public boolean isBooked() {
        return booked;
    }

    public String getBookedBy() {
        return bookedBy;
    }

    public void book(String userName) {
        booked = true;
        bookedBy = userName;
    }
}
