package com.flightBooking.dto;

import lombok.Data;
import java.time.LocalDateTime;

import com.flightBooking.model.BookingStatus;

@Data
public class BookingHistoryResponse {
    private String pnrNumber;
    private LocalDateTime journeyDateTime;
    private String fromPlace;
    private String toPlace;
    private BookingStatus status; 
}
