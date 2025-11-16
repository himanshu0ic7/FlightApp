package com.flightBooking.flightBooking;

import org.junit.jupiter.api.Test;

import com.flightBooking.model.Booking;
import com.flightBooking.model.BookingStatus;
import com.flightBooking.model.Flight;
import com.flightBooking.model.Passenger;
import com.flightBooking.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BookingTest {

    @Test
    void testBookingSettersAndGetters() {
        //arrange
        Booking booking = new Booking();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime journeyTime = now.plusDays(5);
        User user = new User();
        user.setId(1L);
        Flight flight = new Flight();
        flight.setId(101L);

        //act
        booking.setId(1L);
        booking.setPnrNumber("PNR123");
        booking.setBookingDateTime(now);
        booking.setJourneyDateTime(journeyTime);
        booking.setNumberOfSeats(2);
        booking.setUser(user);
        booking.setFlight(flight);
        booking.setPassengers(new ArrayList<>());

        //assert
        assertThat(booking.getId()).isEqualTo(1L);
        assertThat(booking.getPnrNumber()).isEqualTo("PNR123");
        assertThat(booking.getBookingDateTime()).isEqualTo(now);
        assertThat(booking.getJourneyDateTime()).isEqualTo(journeyTime);
        assertThat(booking.getNumberOfSeats()).isEqualTo(2);
        assertThat(booking.getUser()).isEqualTo(user);
        assertThat(booking.getFlight()).isEqualTo(flight);
        assertThat(booking.getPassengers()).isNotNull().isEmpty();
    }

    @Test
    void testBookingAllArgsConstructor() {
        //arrange
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime journeyTime = now.plusDays(5);
        User user = new User(1L, "Test User", "test@example.com", null);
        Flight flight = new Flight();
        List<Passenger> passengers = new ArrayList<>();

        Booking booking = new Booking(
                1L,
                "PNR123",
                now,
                journeyTime,
                2,
                BookingStatus.CANCELLED, user,
                flight,
                passengers
        );

        //assert
        assertThat(booking.getPnrNumber()).isEqualTo("PNR123");
        assertThat(booking.getNumberOfSeats()).isEqualTo(2);
        assertThat(booking.getUser().getName()).isEqualTo("Test User");
        assertThat(booking.getFlight()).isEqualTo(flight);
        assertThat(booking.getPassengers()).isNotNull().isEmpty();
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
    }
}
