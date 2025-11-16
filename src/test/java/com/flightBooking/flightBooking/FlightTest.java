package com.flightBooking.flightBooking;

import org.junit.jupiter.api.Test;

import com.flightBooking.model.Flight;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class FlightTest {

    @Test
    void testFlightSettersAndGetters() {
        //arrange
        Flight flight = new Flight();
        LocalDateTime flightTime = LocalDateTime.of(2025, 11, 20, 14, 30);
        BigDecimal price = new BigDecimal("4500.00");

        //act
        flight.setId(101L);
        flight.setAirlineName("TestAir");
        flight.setAirlineLogoUrl("logo.png");
        flight.setFromPlace("Origin");
        flight.setToPlace("Destination");
        flight.setFlightDateTime(flightTime);
        flight.setPrice(price);
        flight.setTotalSeats(180);
        flight.setAvailableSeats(180);

        // Assert
        assertThat(flight.getId()).isEqualTo(101L);
        assertThat(flight.getAirlineName()).isEqualTo("TestAir");
        assertThat(flight.getAirlineLogoUrl()).isEqualTo("logo.png");
        assertThat(flight.getFromPlace()).isEqualTo("Origin");
        assertThat(flight.getToPlace()).isEqualTo("Destination");
        assertThat(flight.getFlightDateTime()).isEqualTo(flightTime);
        assertThat(flight.getPrice()).isEqualTo(price);
        assertThat(flight.getTotalSeats()).isEqualTo(180);
        assertThat(flight.getAvailableSeats()).isEqualTo(180);
    }

    @Test
    void testFlightAllArgsConstructor() {
        //arrange
        LocalDateTime flightTime = LocalDateTime.of(2025, 11, 20, 14, 30);
        BigDecimal price = new BigDecimal("4500.00");

        Flight flight = new Flight(
                101L,
                "TestAir",
                "logo.png",
                "Origin",
                "Destination",
                flightTime,
                price,
                180,
                180
        );

        //assert
        assertThat(flight.getAirlineName()).isEqualTo("TestAir");
        assertThat(flight.getFromPlace()).isEqualTo("Origin");
        assertThat(flight.getFlightDateTime()).isEqualTo(flightTime);
        assertThat(flight.getPrice()).isEqualTo(price);
        assertThat(flight.getTotalSeats()).isEqualTo(180);
    }
}
