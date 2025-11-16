package com.flightBooking.flightBooking;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import com.flightBooking.model.User;
import static org.assertj.core.api.Assertions.assertThat;
class UserTest {

    @Test
    void testUserSettersAndGetters() {
        //arrange
        User user = new User();

        //act
        user.setId(1L);
        user.setName("Test User");
        user.setEmailId("test@example.com");
        user.setBookings(new ArrayList<>());

        //assert
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("Test User");
        assertThat(user.getEmailId()).isEqualTo("test@example.com");
        assertThat(user.getBookings()).isNotNull().isEmpty();
    }

    @Test
    void testUserAllArgsConstructor() {
        //arrange
        User user = new User(1L, "Test User", "test@example.com", null);

        //assert
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("Test User");
        assertThat(user.getEmailId()).isEqualTo("test@example.com");
        assertThat(user.getBookings()).isNull();
    }
}
