package com.example.theaterbooking.api;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class TheaterControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void resetTheater() throws Exception {
        mockMvc.perform(post("/initialize"));
    }

    @Test
    void returnsAllSeats() throws Exception {
        mockMvc.perform(get("/seats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(60)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].tier").value("SILVER"))
                .andExpect(jsonPath("$[34].id").value(35))
                .andExpect(jsonPath("$[34].tier").value("GOLD"))
                .andExpect(jsonPath("$[59].id").value(60))
                .andExpect(jsonPath("$[59].tier").value("PLATINUM"));
    }

    @Test
    void booksSeatsAndReturnsCreatedResponse() throws Exception {
        mockMvc.perform(post("/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userName": "Ada Lovelace",
                                  "seatIds": [15, 35]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userName").value("Ada Lovelace"))
                .andExpect(jsonPath("$.seats", hasSize(2)))
                .andExpect(jsonPath("$.totalPrice").value(250));
    }

    @Test
    void rejectsBookedSeats() throws Exception {
        mockMvc.perform(post("/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "userName": "Ada",
                          "seatIds": [1]
                        }
                        """));

        mockMvc.perform(post("/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userName": "Grace",
                                  "seatIds": [1]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages[0]").value("Seat 1 is already booked"));
    }

    @Test
    void validatesRequestShape() throws Exception {
        mockMvc.perform(post("/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userName": "",
                                  "seatIds": [1, 2, 3, 4, 5, 6, 7]
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.messages", hasSize(2)))
                .andExpect(jsonPath("$.messages", hasItem(containsString("User name is required"))))
                .andExpect(jsonPath("$.messages", hasItem(containsString("A booking can contain at most 6 seats"))));
    }
}
