package com.example.theaterbooking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.theaterbooking.api.BookingResponse;
import com.example.theaterbooking.api.SeatResponse;
import com.example.theaterbooking.domain.PricingTier;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TheaterServiceTest {
    private TheaterService theaterService;

    @BeforeEach
    void setUp() {
        theaterService = new TheaterService();
    }

    @Test
    void initializesSixtySeatsAcrossSixRowsAndThreePricingTiers() {
        List<SeatResponse> seats = theaterService.listSeats();

        assertThat(seats).hasSize(60);
        assertThat(seats.get(0))
                .extracting(SeatResponse::id, SeatResponse::rowNumber, SeatResponse::columnNumber, SeatResponse::tier, SeatResponse::price)
                .containsExactly(1, 1, 1, PricingTier.SILVER, 100);
        assertThat(seats.get(14))
                .extracting(SeatResponse::id, SeatResponse::rowNumber, SeatResponse::columnNumber, SeatResponse::tier, SeatResponse::price)
                .containsExactly(15, 2, 5, PricingTier.SILVER, 100);
        assertThat(seats.get(34))
                .extracting(SeatResponse::id, SeatResponse::rowNumber, SeatResponse::columnNumber, SeatResponse::tier, SeatResponse::price)
                .containsExactly(35, 4, 5, PricingTier.GOLD, 150);
        assertThat(seats.get(59))
                .extracting(SeatResponse::id, SeatResponse::rowNumber, SeatResponse::columnNumber, SeatResponse::tier, SeatResponse::price)
                .containsExactly(60, 6, 10, PricingTier.PLATINUM, 200);
    }

    @Test
    void booksSeatsFromDifferentRowsAndReturnsTotalPrice() {
        BookingResponse response = theaterService.bookSeats(" Ada ", List.of(15, 35, 55));

        assertThat(response.userName()).isEqualTo("Ada");
        assertThat(response.totalPrice()).isEqualTo(450);
        assertThat(response.seats()).extracting(SeatResponse::id).containsExactly(15, 35, 55);
        assertThat(response.seats()).allMatch(SeatResponse::booked);
    }

    @Test
    void rejectsAlreadyBookedSeatsWithoutChangingOtherSeats() {
        theaterService.bookSeats("Ada", List.of(11, 12));

        assertThatThrownBy(() -> theaterService.bookSeats("Grace", List.of(12, 13)))
                .isInstanceOf(BookingException.class)
                .hasMessage("Seat 12 is already booked");

        assertThat(theaterService.listSeats())
                .filteredOn(SeatResponse::booked)
                .extracting(SeatResponse::id)
                .containsExactly(11, 12);
    }

    @Test
    void rejectsInvalidSeatSelections() {
        assertThatThrownBy(() -> theaterService.bookSeats("Ada", List.of(0)))
                .isInstanceOf(BookingException.class)
                .hasMessage("Seat ID must be between 1 and 60");
        assertThatThrownBy(() -> theaterService.bookSeats("Ada", List.of(1, 1)))
                .isInstanceOf(BookingException.class)
                .hasMessage("Seat IDs must be unique");
        assertThatThrownBy(() -> theaterService.bookSeats("Ada", List.of(1, 2, 3, 4, 5, 6, 7)))
                .isInstanceOf(BookingException.class)
                .hasMessage("A booking can contain at most 6 seats");
    }

    @Test
    void initializeResetsBookedSeats() {
        theaterService.bookSeats("Ada", List.of(1, 2));

        theaterService.initialize();

        assertThat(theaterService.listSeats()).noneMatch(SeatResponse::booked);
    }
}
