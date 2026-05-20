package com.example.theaterbooking.api;

import com.example.theaterbooking.service.TheaterService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://127.0.0.1:5173"
})
public class TheaterController {
    private final TheaterService theaterService;

    public TheaterController(TheaterService theaterService) {
        this.theaterService = theaterService;
    }

    @PostMapping("/initialize")
    public List<SeatResponse> initialize() {
        return theaterService.initialize();
    }

    @GetMapping("/seats")
    public List<SeatResponse> seats() {
        return theaterService.listSeats();
    }

    @PostMapping("/book")
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse book(@Valid @RequestBody BookingRequest request) {
        return theaterService.bookSeats(request.userName(), request.seatIds());
    }
}
