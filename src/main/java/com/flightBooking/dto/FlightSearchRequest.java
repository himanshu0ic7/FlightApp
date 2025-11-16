package com.flightBooking.dto;

import lombok.Data;
import java.time.LocalDate;

import com.flightBooking.model.TripType;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class FlightSearchRequest {
	@NotBlank(message = "From place is required")
    private String fromPlace;

    @NotBlank(message = "To place is required")
    private String toPlace;

    @NotNull(message = "Journey date is required")
    @FutureOrPresent(message = "Journey date must be today or in the future")
    private LocalDate journeyDate;

    @NotNull(message = "Trip type is required")
    private TripType tripType;
}
