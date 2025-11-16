package com.flightBooking.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

import com.flightBooking.model.BookingStatus;

@Data
public class TicketDetailsResponse {
    private String pnrNumber;
    private LocalDateTime journeyDateTime;
    private FlightDetails flight;
    private List<PassengerDTO> passengers;
    private BookingStatus status; 
    //inner class for flight details
    @Data
    public static class FlightDetails {
        private String airlineName;
        private String fromPlace;
        private String toPlace;
    }
}
