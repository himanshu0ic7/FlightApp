package com.flightBooking.service;

import java.util.List;

import com.flightBooking.dto.BookingHistoryResponse;
import com.flightBooking.dto.BookingRequest;
import com.flightBooking.dto.FlightInventoryRequest;
import com.flightBooking.dto.FlightSearchRequest;
import com.flightBooking.dto.FlightSearchResult;
import com.flightBooking.dto.TicketDetailsResponse;

public interface FlightService {

    //adds a new flight or flight schedule to the inventory
    void addFlightInventory(FlightInventoryRequest request);

    //searches for available flights based on user criteria
    List<FlightSearchResult> searchFlights(FlightSearchRequest request);

    /*
     Books a ticket for a user.
     This method handles user creation/lookup, passenger saving,PNR generation, and linking them all together in a Booking.
     */
    String bookTicket(Long flightId, BookingRequest request);

    // Retrieves the full details of a booked ticket using its PNR
    TicketDetailsResponse getTicketDetails(String pnr);

    //Retrieves the booking history for a specific user via their email id
    List<BookingHistoryResponse> getBookingHistory(String emailId);

    // Cancels a booking using its PNR number.(with 24 hour rule)
     
    void cancelBooking(String pnr);
}