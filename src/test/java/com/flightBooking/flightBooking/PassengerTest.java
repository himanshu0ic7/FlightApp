package com.flightBooking.flightBooking;

import org.junit.jupiter.api.Test;

import com.flightBooking.model.Gender;
import com.flightBooking.model.Passenger;

import static org.assertj.core.api.Assertions.assertThat;

class PassengerTest {

    @Test
    void testPassengerSettersAndGetters() {
        //arrange
        Passenger passenger = new Passenger();

        //act
        passenger.setId(1L);
        passenger.setName("Test Passenger");
        passenger.setGender(Gender.MALE);
        passenger.setAge(30);
        passenger.setSeatNumber("12A");
        passenger.setBooking(null);

        //assert
        assertThat(passenger.getId()).isEqualTo(1L);
        assertThat(passenger.getName()).isEqualTo("Test Passenger");
        assertThat(passenger.getGender()).isEqualTo(Gender.MALE);
        assertThat(passenger.getAge()).isEqualTo(30);
        assertThat(passenger.getSeatNumber()).isEqualTo("12A");
        assertThat(passenger.getBooking()).isNull();
    }

    @Test
    void testPassengerAllArgsConstructor() {
        //arrange
        Passenger passenger = new Passenger(
                1L,
                "Test Passenger",
                Gender.FEMALE,
                28,
                "12B",
                null
        );

        //assert
        assertThat(passenger.getName()).isEqualTo("Test Passenger");
        assertThat(passenger.getGender()).isEqualTo(Gender.FEMALE);
        assertThat(passenger.getAge()).isEqualTo(28);
        assertThat(passenger.getSeatNumber()).isEqualTo("12B");
    }
}
