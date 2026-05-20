package com.example.theaterbooking.api;

import java.util.List;

public record BookingResponse(
        String userName,
        List<SeatResponse> seats,
        int totalPrice
) {
}
