package com.restaurant.Restaurant;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.Random;

import com.restaurant.Restaurant.Model.User;
import com.restaurant.Restaurant.Repository.UserRepository;
import com.restaurant.Restaurant.Service.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;


@SpringBootTest
public class ServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private RestaurantService restaurantService;

    @Mock
    private JavaMailSender mailSender;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private User user;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this); // Initialize mocks
        user = new User();
        // Generate a random userId
        int randomUserId = new Random().nextInt(1000); // Generate a random integer
        user.setUserId(randomUserId);

        // Set the rest of the user fields
        user.setFirstName("Test");
        user.setLastName("User");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setRole("Admin");
        user.setPhone("123-456-7890");

        // Set the current date
        user.setDateToToday();
    }

    @Test
    void testAddUser_whenUserAlreadyExists() {
        // Mocking the repository to return an existing user
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // Call the service method
        User result = restaurantService.addUser(user);

        // Verify the repository was called
        verify(userRepository, times(1)).findByEmail(user.getEmail());

        // Assert that the existing user is returned
        assertEquals(user, result);
    }

    @Test
    void testAddUser_whenUserDoesNotExist() {
        // Mocking the repository to return empty when user doesn't exist
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        // Mock saving the user
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Call the service method
        User result = restaurantService.addUser(user);

        // Verify the repository was called
        verify(userRepository, times(1)).findByEmail(user.getEmail());
        verify(userRepository, times(1)).save(any(User.class));

        // Assert the user was returned and the password is hashed
        assertEquals(user, result);
        assertTrue(BCrypt.checkpw("password123", result.getPassword())); // Check if password is hashed
    }

    @Test
    void testVerifyPayment_ValidInput() {
        // Valid input for the test
        String cardHolderName = "John Doe";
        String cardNumber = "1234-5678-9101-1121";
        String expiryDate = "12/25";
        String cvc = "123";

        // Call the method and assert that it returns true for valid input
        assertTrue(restaurantService.verifyPayment(cardHolderName, cardNumber, expiryDate, cvc));
    }

    @Test
    void testVerifyPayment_InvalidCardHolderName() {
        // Invalid cardholder name (numbers are not allowed)
        String cardHolderName = "John Doe 123";
        String cardNumber = "1234-5678-9101-1121";
        String expiryDate = "12/25";
        String cvc = "123";

        // Call the method and assert that it returns false
        assertFalse(restaurantService.verifyPayment(cardHolderName, cardNumber, expiryDate, cvc));
    }

    @Test
    void testVerifyPayment_InvalidCardNumber() {
        // Invalid card number (doesn't match the required pattern)
        String cardHolderName = "John Doe";
        String cardNumber = "1234567891011121";  // Missing hyphens
        String expiryDate = "12/25";
        String cvc = "123";

        // Call the method and assert that it returns false
        assertFalse(restaurantService.verifyPayment(cardHolderName, cardNumber, expiryDate, cvc));
    }

    @Test
    void testVerifyPayment_InvalidExpiryDate() {
        // Invalid expiry date (wrong format)
        String cardHolderName = "John Doe";
        String cardNumber = "1234-5678-9101-1121";
        String expiryDate = "13/25";  // Invalid month
        String cvc = "123";

        // Call the method and assert that it returns false
        assertFalse(restaurantService.verifyPayment(cardHolderName, cardNumber, expiryDate, cvc));
    }

    @Test
    void testVerifyPayment_InvalidCVC() {
        // Invalid CVC (too short)
        String cardHolderName = "John Doe";
        String cardNumber = "1234-5678-9101-1121";
        String expiryDate = "12/25";
        String cvc = "12";  // Too short

        // Call the method and assert that it returns false
        assertFalse(restaurantService.verifyPayment(cardHolderName, cardNumber, expiryDate, cvc));
    }


    @Test
    void testSendEmailWithCode() {
        String email = "test@example.com";
        String subject = "Test Subject";
        String message = "This is a test message";

        // When
        restaurantService.sendEmailWithCode(email, subject, message);

        // Then
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(captor.capture());

        SimpleMailMessage capturedMessage = captor.getValue();

        assertEquals(email, capturedMessage.getTo()[0], "Email address mismatch.");
        assertEquals(subject, capturedMessage.getSubject(), "Subject mismatch.");
        assertEquals(message, capturedMessage.getText(), "Message text mismatch.");
    }

}
