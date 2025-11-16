package com.flightBooking.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FlightSearchResult {
    private Long flightId;
    private String airlineName;
    private LocalDateTime flightDateTime;
    private BigDecimal price;
}