package com.flightBooking.dto;

import com.flightBooking.model.Gender;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PassengerDTO {
	@NotBlank(message = "Passenger name is required")
    private String name;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @Min(value = 1, message = "Passenger age must be at least 1")
    private int age;

    @NotBlank(message = "Seat number is required")
    private String seatNumber;
}
