package com.example.theaterbooking.service;

import com.example.theaterbooking.api.BookingResponse;
import com.example.theaterbooking.api.SeatResponse;
import com.example.theaterbooking.domain.PricingTier;
import com.example.theaterbooking.domain.Seat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class TheaterService {
    public static final int ROWS = 6;
    public static final int COLUMNS = 10;
    public static final int TOTAL_SEATS = ROWS * COLUMNS;
    public static final int MAX_SEATS_PER_BOOKING = 6;

    private final Map<Integer, Seat> seats = new LinkedHashMap<>();

    public TheaterService() {
        initialize();
    }

    public synchronized List<SeatResponse> initialize() {
        seats.clear();

        for (int id = 1; id <= TOTAL_SEATS; id++) {
            int rowNumber = ((id - 1) / COLUMNS) + 1;
            int columnNumber = ((id - 1) % COLUMNS) + 1;
            seats.put(id, new Seat(id, rowNumber, columnNumber, PricingTier.fromRow(rowNumber)));
        }

        return listSeats();
    }

    public synchronized List<SeatResponse> listSeats() {
        return seats.values().stream()
                .map(SeatResponse::from)
                .toList();
    }

    public synchronized BookingResponse bookSeats(String userName, List<Integer> seatIds) {
        String normalizedUserName = normalizeUserName(userName);
        List<Integer> normalizedSeatIds = normalizeSeatIds(seatIds);

        List<Seat> selectedSeats = normalizedSeatIds.stream()
                .map(this::getSeatOrThrow)
                .toList();

        selectedSeats.stream()
                .filter(Seat::isBooked)
                .findFirst()
                .ifPresent(seat -> {
                    throw new BookingException("Seat " + seat.getId() + " is already booked");
                });

        selectedSeats.forEach(seat -> seat.book(normalizedUserName));

        List<SeatResponse> bookedSeats = selectedSeats.stream()
                .map(SeatResponse::from)
                .toList();
        int totalPrice = bookedSeats.stream()
                .mapToInt(SeatResponse::price)
                .sum();

        return new BookingResponse(normalizedUserName, bookedSeats, totalPrice);
    }

    private String normalizeUserName(String userName) {
        if (userName == null || userName.isBlank()) {
            throw new BookingException("User name is required");
        }
        return userName.trim();
    }

    private List<Integer> normalizeSeatIds(List<Integer> seatIds) {
        if (seatIds == null || seatIds.isEmpty()) {
            throw new BookingException("Select at least one seat");
        }
        if (seatIds.size() > MAX_SEATS_PER_BOOKING) {
            throw new BookingException("A booking can contain at most 6 seats");
        }

        Set<Integer> uniqueSeatIds = new LinkedHashSet<>(seatIds);
        if (uniqueSeatIds.size() != seatIds.size()) {
            throw new BookingException("Seat IDs must be unique");
        }

        return new ArrayList<>(uniqueSeatIds);
    }

    private Seat getSeatOrThrow(Integer seatId) {
        if (seatId == null || seatId < 1 || seatId > TOTAL_SEATS) {
            throw new BookingException("Seat ID must be between 1 and 60");
        }
        return seats.get(seatId);
    }
}
