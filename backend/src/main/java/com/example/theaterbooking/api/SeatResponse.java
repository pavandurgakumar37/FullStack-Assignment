package com.example.theaterbooking.api;

import com.example.theaterbooking.domain.PricingTier;
import com.example.theaterbooking.domain.Seat;

public record SeatResponse(
        int id,
        int rowNumber,
        int columnNumber,
        PricingTier tier,
        int price,
        boolean booked
) {
    public static SeatResponse from(Seat seat) {
        return new SeatResponse(
                seat.getId(),
                seat.getRowNumber(),
                seat.getColumnNumber(),
                seat.getTier(),
                seat.getPrice(),
                seat.isBooked()
        );
    }
}
