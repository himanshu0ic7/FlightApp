package com.flightBooking.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.flightBooking.dto.BookingHistoryResponse;
import com.flightBooking.dto.BookingRequest;
import com.flightBooking.dto.PassengerDTO;
import com.flightBooking.model.Booking;
import com.flightBooking.model.BookingStatus;
import com.flightBooking.model.Flight;
import com.flightBooking.model.Gender;
import com.flightBooking.model.User;
import com.flightBooking.repo.BookingRepository;
import com.flightBooking.repo.FlightRepository;
import com.flightBooking.repo.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FlightServiceImplTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private FlightServiceImpl flightService;

    private Flight testFlight;
    private User testUser;
    private Booking testBooking;
    private BookingRequest bookingRequest;

    @BeforeEach
    void setUp() {
        // Flight
        testFlight = new Flight();
        testFlight.setId(1L);
        testFlight.setFlightDateTime(LocalDateTime.now().plusDays(10));
        testFlight.setAvailableSeats(10);
        testFlight.setTotalSeats(100);

        // User
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmailId("test@example.com");
        testUser.setName("Test User");

        // Booking
        testBooking = new Booking();
        testBooking.setPnrNumber("PNR123");
        testBooking.setFlight(testFlight);
        testBooking.setUser(testUser);
        testBooking.setStatus(BookingStatus.CONFIRMED);
        testBooking.setNumberOfSeats(1);
        testBooking.setJourneyDateTime(testFlight.getFlightDateTime());

        // FULL Passenger DTO (Validation-Safe)
        PassengerDTO passengerDTO = new PassengerDTO();
        passengerDTO.setName("Test Passenger");
        passengerDTO.setGender(Gender.MALE);
        passengerDTO.setAge(25);
        passengerDTO.setSeatNumber("12A");

        // Booking Request
        bookingRequest = new BookingRequest();
        bookingRequest.setEmailId("test@example.com");
        bookingRequest.setName("Test User");
        bookingRequest.setNumberOfSeats(1);
        bookingRequest.setPassengers(List.of(passengerDTO));
    }

    @Test
    void testBookTicket_Success() {
        // Given
        given(flightRepository.findById(1L)).willReturn(Optional.of(testFlight));
        given(userRepository.findByEmailId("test@example.com")).willReturn(Optional.of(testUser));
        given(bookingRepository.save(any(Booking.class))).willAnswer(invocation -> invocation.getArgument(0));

        // When
        String pnr = flightService.bookTicket(1L, bookingRequest);

        // Then
        assertThat(pnr).isNotNull();
        assertThat(testFlight.getAvailableSeats()).isEqualTo(9); // seats decremented
        verify(flightRepository).save(testFlight);
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void testBookTicket_Fail_NotEnoughSeats() {
        // Given
        testFlight.setAvailableSeats(0);
        given(flightRepository.findById(1L)).willReturn(Optional.of(testFlight));

        // When & Then
        assertThatThrownBy(() -> flightService.bookTicket(1L, bookingRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Not enough available seats for this booking.");
    }

    @Test
    void testCancelBooking_Success() {
        // Given
        given(bookingRepository.findByPnrNumber("PNR123")).willReturn(Optional.of(testBooking));
        given(flightRepository.save(any(Flight.class))).willReturn(testFlight);
        given(bookingRepository.save(any(Booking.class))).willReturn(testBooking);

        // When
        flightService.cancelBooking("PNR123");

        // Then
        assertThat(testBooking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        assertThat(testFlight.getAvailableSeats()).isEqualTo(11); // seats returned
        verify(bookingRepository).save(testBooking);
        verify(flightRepository).save(testFlight);
    }

    @Test
    void testCancelBooking_Fail_Within24Hours() {
        // Given
        testBooking.setJourneyDateTime(LocalDateTime.now().plusHours(10));
        given(bookingRepository.findByPnrNumber("PNR123")).willReturn(Optional.of(testBooking));

        // When & Then
        assertThatThrownBy(() -> flightService.cancelBooking("PNR123"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cannot cancel ticket. Journey is within 24 hours.");
    }

    @Test
    void testCancelBooking_Fail_AlreadyCancelled() {
        // Given
        testBooking.setStatus(BookingStatus.CANCELLED);
        given(bookingRepository.findByPnrNumber("PNR123")).willReturn(Optional.of(testBooking));

        // When & Then
        assertThatThrownBy(() -> flightService.cancelBooking("PNR123"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("This ticket is already cancelled.");
    }

    @Test
    void testGetBookingHistory_Success() {
        // Given
        given(bookingRepository.findByUser_EmailId("test@example.com"))
                .willReturn(List.of(testBooking));

        // When
        List<BookingHistoryResponse> history = flightService.getBookingHistory("test@example.com");

        // Then
        assertThat(history).hasSize(1);
        assertThat(history.get(0).getPnrNumber()).isEqualTo("PNR123");
    }
}
