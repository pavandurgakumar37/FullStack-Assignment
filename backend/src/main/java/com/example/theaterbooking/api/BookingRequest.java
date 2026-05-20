package com.example.theaterbooking.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record BookingRequest(
        @NotBlank(message = "User name is required")
        String userName,

        @NotEmpty(message = "Select at least one seat")
        @Size(max = 6, message = "A booking can contain at most 6 seats")
        List<Integer> seatIds
) {
}
