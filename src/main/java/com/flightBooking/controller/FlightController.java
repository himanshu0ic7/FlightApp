package com.flightBooking.controller;

import com.flightBooking.dto.*;
import com.flightBooking.service.FlightService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flight")
public class FlightController {

    @Autowired
    private FlightService flightService;

    //for admin to add flights
    @PostMapping("/airline/inventory/add")
    public ResponseEntity<ApiResponse> addFlightInventory(@Valid @RequestBody FlightInventoryRequest request) {
        flightService.addFlightInventory(request); 
        return new ResponseEntity<>(new ApiResponse("Flight inventory added successfully"), HttpStatus.CREATED);
    }

    //for user to seach flight
    @PostMapping("/search")
    public ResponseEntity<List<FlightSearchResult>> searchFlights(@Valid @RequestBody FlightSearchRequest request) {
        List<FlightSearchResult> results = flightService.searchFlights(request);
        if (results.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    //for user to book a ticket for a specific flight
    @PostMapping("/booking/{flightid}")
    public ResponseEntity<BookingResponse> bookTicket(
            @PathVariable("flightid") Long flightId,
            @Valid @RequestBody BookingRequest request) {
        String pnr = flightService.bookTicket(flightId, request); 
        
        BookingResponse response = new BookingResponse("Booking successful", pnr);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    //for user to get ticket details using PNR
    @GetMapping("/ticket/{pnr}")
    public ResponseEntity<TicketDetailsResponse> getTicketDetails(@PathVariable("pnr") String pnr) {
        TicketDetailsResponse ticketDetails = flightService.getTicketDetails(pnr);
        return new ResponseEntity<>(ticketDetails, HttpStatus.OK);
    }

    //for user to get all booking history using email id 
    @GetMapping("/booking/history/{emailId}")
    public ResponseEntity<List<BookingHistoryResponse>> getBookingHistory(@PathVariable("emailId") String emailId) {
        List<BookingHistoryResponse> history = flightService.getBookingHistory(emailId);
        if (history.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(history, HttpStatus.OK);
    }

    //for user to cancel a ticket using PNR 
    @DeleteMapping("/booking/cancel/{pnr}")
    public ResponseEntity<ApiResponse> cancelBooking(@PathVariable("pnr") String pnr) {
        flightService.cancelBooking(pnr);
        return new ResponseEntity<>(new ApiResponse("Ticket " + pnr + " cancelled successfully"), HttpStatus.OK);
    }
}