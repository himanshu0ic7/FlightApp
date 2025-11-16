package com.flightBooking.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightBooking.controller.FlightController;
import com.flightBooking.dto.BookingRequest;
import com.flightBooking.dto.FlightSearchRequest;
import com.flightBooking.dto.FlightSearchResult;
import com.flightBooking.dto.PassengerDTO;
import com.flightBooking.dto.TicketDetailsResponse;
import com.flightBooking.service.FlightService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FlightController.class)
class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // For converting objects to JSON

    @MockBean
    private FlightService flightService;

    private FlightSearchRequest searchRequest;
    private BookingRequest bookingRequest;
    private TicketDetailsResponse ticketDetails;

    @BeforeEach
    void setUp() {
        // Common DTOs for tests
        searchRequest = new FlightSearchRequest();
        searchRequest.setFromPlace("Delhi");
        searchRequest.setToPlace("Mumbai");
        searchRequest.setJourneyDate(LocalDate.now().plusDays(10));
        
        bookingRequest = new BookingRequest();
        bookingRequest.setName("Test User");
        bookingRequest.setEmailId("test@example.com");
        bookingRequest.setNumberOfSeats(1);
        bookingRequest.setPassengers(List.of(new PassengerDTO()));
        
        ticketDetails = new TicketDetailsResponse();
        ticketDetails.setPnrNumber("PNR123");
        ticketDetails.setJourneyDateTime(LocalDateTime.now().plusDays(10));
    }

    @Test
    void testSearchFlights_Success() throws Exception {
        // Given: Mock the service to return a list
        FlightSearchResult result = new FlightSearchResult();
        result.setFlightId(1L);
        result.setAirlineName("TestAir");
        given(flightService.searchFlights(any(FlightSearchRequest.class)))
                .willReturn(List.of(result));

        // When & Then
        mockMvc.perform(post("/api/v1.0/flight/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(searchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].airlineName").value("TestAir"));
    }

    @Test
    void testSearchFlights_NoResults() throws Exception {
        // Given: Mock the service to return an empty list
        given(flightService.searchFlights(any(FlightSearchRequest.class)))
                .willReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(post("/api/v1.0/flight/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(searchRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testBookTicket_Success() throws Exception {
        // Given: Mock the service to return a PNR
        given(flightService.bookTicket(eq(1L), any(BookingRequest.class)))
                .willReturn("PNR123");

        // When & Then
        mockMvc.perform(post("/api/v1.0/flight/booking/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Booking successful"))
                .andExpect(jsonPath("$.pnrNumber").value("PNR123"));
    }

    @Test
    void testGetTicketDetails_Success() throws Exception {
        // Given: Mock the service to return ticket details
        given(flightService.getTicketDetails("PNR123")).willReturn(ticketDetails);

        // When & Then
        mockMvc.perform(get("/api/v1.0/flight/ticket/PNR123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pnrNumber").value("PNR123"));
    }

    @Test
    void testCancelBooking_Success() throws Exception {
        // Given: Mock the service to perform cancellation
        doNothing().when(flightService).cancelBooking("PNR123");

        // When & Then
        mockMvc.perform(delete("/api/v1.0/flight/booking/cancel/PNR123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Ticket PNR123 cancelled successfully"));
    }
}
