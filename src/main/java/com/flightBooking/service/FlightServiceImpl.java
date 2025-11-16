package com.flightBooking.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flightBooking.dto.BookingHistoryResponse;
import com.flightBooking.dto.BookingRequest;
import com.flightBooking.dto.FlightInventoryRequest;
import com.flightBooking.dto.FlightSearchRequest;
import com.flightBooking.dto.FlightSearchResult;
import com.flightBooking.dto.PassengerDTO;
import com.flightBooking.dto.TicketDetailsResponse;
import com.flightBooking.model.Booking;
import com.flightBooking.model.BookingStatus;
import com.flightBooking.model.Flight;
import com.flightBooking.model.Passenger;
import com.flightBooking.model.User;
import com.flightBooking.repo.BookingRepository;
import com.flightBooking.repo.FlightRepository;
import com.flightBooking.repo.UserRepository;
import com.flightBooking.validation.BookingException;
import com.flightBooking.validation.ResourceNotFoundException;

import jakarta.transaction.Transactional;

@Service
public class FlightServiceImpl implements FlightService {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    //PassengerRepository is not needed here as passengers are saved via the Booking entity (CascadeType.ALL)

    @Override
    @Transactional
    public void addFlightInventory(FlightInventoryRequest request) {
        Flight flight = new Flight();
        flight.setAirlineName(request.getAirlineName());
        flight.setFromPlace(request.getFromPlace());
        flight.setToPlace(request.getToPlace());
        flight.setFlightDateTime(request.getFlightDateTime());
        flight.setPrice(request.getPrice());
        flight.setTotalSeats(request.getTotalSeats());
        flight.setAvailableSeats(request.getTotalSeats()); // Initially, all seats are available

        flightRepository.save(flight);
    }

    @Override
    public List<FlightSearchResult> searchFlights(FlightSearchRequest request) {
        // Creating a date range for the entire day
        LocalDateTime startDateTime = request.getJourneyDate().atStartOfDay();
        LocalDateTime endDateTime = request.getJourneyDate().atTime(LocalTime.MAX);

        List<Flight> flights = flightRepository.findByFromPlaceAndToPlaceAndFlightDateTimeBetween(
                request.getFromPlace(),
                request.getToPlace(),
                startDateTime,
                endDateTime
        );

        return flights.stream()
                .map(this::mapFlightToSearchResult)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public String bookTicket(Long flightId, BookingRequest request) {
        //1. search the flight
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with ID: " + flightId));

        //2. Check for available seats
        if (flight.getAvailableSeats() < request.getNumberOfSeats()) {
            throw new BookingException("Not enough available seats for this booking.");
        }

        //3. Find or create the user
        User user = userRepository.findByEmailId(request.getEmailId())
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmailId(request.getEmailId());
                    newUser.setName(request.getName());
                    return userRepository.save(newUser);
                });

        //4. Create the booking
        Booking booking = new Booking();
        booking.setPnrNumber(generatePnr());
        booking.setBookingDateTime(LocalDateTime.now());
        booking.setJourneyDateTime(flight.getFlightDateTime());
        booking.setNumberOfSeats(request.getNumberOfSeats());
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setUser(user);
        booking.setFlight(flight);

        //5.Create passenger list
        List<Passenger> passengers = request.getPassengers().stream()
                .map(dto -> mapDtoToPassenger(dto, booking))
                .collect(Collectors.toList());

        booking.setPassengers(passengers);

        //6.Update flight's available seats
        flight.setAvailableSeats(flight.getAvailableSeats() - request.getNumberOfSeats());
        flightRepository.save(flight);

        //7. Save the booking (which also saves passengers due to cascade)
        Booking savedBooking = bookingRepository.save(booking);

        return savedBooking.getPnrNumber();
    }

    @Override
    public TicketDetailsResponse getTicketDetails(String pnr) {
        Booking booking = bookingRepository.findByPnrNumber(pnr)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found for PNR: " + pnr));

        return mapBookingToTicketDetails(booking);
    }

    @Override
    public List<BookingHistoryResponse> getBookingHistory(String emailId) {
        List<Booking> bookings = bookingRepository.findByUser_EmailId(emailId);

        return bookings.stream()
                .map(this::mapBookingToHistoryResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void cancelBooking(String pnr) {
        // 1. Find the booking
        Booking booking = bookingRepository.findByPnrNumber(pnr)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found for PNR: " + pnr));

        // 2. Check if already cancelled
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BookingException("This ticket is already cancelled.");
        }

        // 3. Check the 24-hour cancellation rule
        if (booking.getJourneyDateTime().isBefore(LocalDateTime.now().plusHours(24))) {
            throw new BookingException("Cannot cancel ticket. Journey is within 24 hours.");
        }

        // 4. Update booking status
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // 5. Release the seats back to the flight inventory
        Flight flight = booking.getFlight();
        flight.setAvailableSeats(flight.getAvailableSeats() + booking.getNumberOfSeats());
        flightRepository.save(flight);
    }


    //custom methods
    private String generatePnr() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private FlightSearchResult mapFlightToSearchResult(Flight flight) {
        FlightSearchResult dto = new FlightSearchResult();
        dto.setFlightId(flight.getId());
        dto.setAirlineName(flight.getAirlineName());
        dto.setFlightDateTime(flight.getFlightDateTime());
        dto.setPrice(flight.getPrice());
        return dto;
    }

    private Passenger mapDtoToPassenger(PassengerDTO dto, Booking booking) {
        Passenger passenger = new Passenger();
        passenger.setName(dto.getName());
        passenger.setGender(dto.getGender());
        passenger.setAge(dto.getAge());
        passenger.setSeatNumber(dto.getSeatNumber());
        passenger.setBooking(booking); // Link back to the booking
        return passenger;
    }

    private TicketDetailsResponse mapBookingToTicketDetails(Booking booking) {
        TicketDetailsResponse dto = new TicketDetailsResponse();
        dto.setPnrNumber(booking.getPnrNumber());
        dto.setJourneyDateTime(booking.getJourneyDateTime());
        dto.setStatus(booking.getStatus());

        //mapping flight details
        TicketDetailsResponse.FlightDetails flightDetails = new TicketDetailsResponse.FlightDetails();
        flightDetails.setAirlineName(booking.getFlight().getAirlineName());
        flightDetails.setFromPlace(booking.getFlight().getFromPlace());
        flightDetails.setToPlace(booking.getFlight().getToPlace());
        dto.setFlight(flightDetails);

        //mapping passenger details
        List<PassengerDTO> passengerDTOs = booking.getPassengers().stream()
                .map(this::mapPassengerToDto)
                .collect(Collectors.toList());
        dto.setPassengers(passengerDTOs);

        return dto;
    }
    
    private PassengerDTO mapPassengerToDto(Passenger passenger) {
        PassengerDTO dto = new PassengerDTO();
        dto.setName(passenger.getName());
        dto.setGender(passenger.getGender());
        dto.setAge(passenger.getAge());
        dto.setSeatNumber(passenger.getSeatNumber());
        return dto;
    }

    private BookingHistoryResponse mapBookingToHistoryResponse(Booking booking) {
        BookingHistoryResponse dto = new BookingHistoryResponse();
        dto.setPnrNumber(booking.getPnrNumber());
        dto.setJourneyDateTime(booking.getJourneyDateTime());
        dto.setFromPlace(booking.getFlight().getFromPlace());
        dto.setToPlace(booking.getFlight().getToPlace());
        dto.setStatus(booking.getStatus());
        return dto;
    }
}
