package com.flightBooking.repo;

import com.flightBooking.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
	
    List<Flight> findByFromPlaceAndToPlaceAndFlightDateTimeBetween(
            String fromPlace,
            String toPlace,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    );
}
