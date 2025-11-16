package com.flightBooking.dto;

import lombok.Data;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Data
public class BookingRequest {
	@NotBlank(message = "User name is required")
    private String name;

    @NotBlank(message = "Email ID is required")
    @Email(message = "A valid email address is required")
    private String emailId;

    @Min(value = 1, message = "Must book at least 1 seat")
    private int numberOfSeats;

    @NotEmpty(message = "Passenger list cannot be empty")
    @Size(min = 1, message = "Must have at least 1 passenger")
    private List<PassengerDTO> passengers;
}
