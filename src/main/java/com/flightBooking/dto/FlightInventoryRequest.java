package com.flightBooking.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.validation.constraints.*;

@Data
public class FlightInventoryRequest {
	@NotBlank(message = "Airline name is required")
    @Size(min = 2, max = 50, message = "Airline name must be between 2 and 50 characters")
    private String airlineName;
    
    @NotBlank(message = "From place is required")
    private String fromPlace;

    @NotBlank(message = "To place is required")
    private String toPlace;

    @Future(message = "Flight date/time must be in the future")
    @NotNull(message = "Flight date/time is required")
    private LocalDateTime flightDateTime;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Total seats is required")
    @Min(value = 1, message = "There must be at least 1 seat")
    private Integer totalSeats;
}



