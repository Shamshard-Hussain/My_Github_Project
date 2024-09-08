package com.restaurant.Restaurant.Service;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;


import com.restaurant.Restaurant.Model.*;
import com.restaurant.Restaurant.Repository.ReservationRepository;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.restaurant.Restaurant.Repository.UserRepository;

@Service
public class RestaurantService {

    @Autowired
    private UserRepository repository;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private JavaMailSender mailSender;

    public User addUser(User user) {
        Optional<User> existingUserByEmail = repository.findByEmail(user.getEmail());

        if (existingUserByEmail.isPresent()) {
            User existingUser = existingUserByEmail.get();
            System.err.println("User already exists with email: " + user.getEmail());
            return existingUser;
        } else {
            user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt())); // Hash the password
            return repository.save(user);
        }
    }

    public Optional<User> findUserByEmail(String email) {
        User user = userStore.get(email);
        return repository.findByEmail(email);

    }

    // In-memory storage for demonstration
    private final Map<String, User> userStore = new HashMap<>();




    public List<User> findAllUsers() {
        return repository.findAll();
    }

    public User getUserByUserId(int userId) {
        Optional<User> user = repository.findById(String.valueOf(userId));
        return user.orElse(null); // Return null if user not found
    }

    public List<User> getUsersByRole(String role) {
        return repository.findByRole(role);
    }

    public User updateUser(User userRequest) {
        Optional<User> optionalExistingUser = repository.findById(userRequest.getEmail());
        if (optionalExistingUser.isPresent()) {
            User existingUser = optionalExistingUser.get();

            // Update fields
            existingUser.setFirstName(userRequest.getFirstName());
            existingUser.setLastName(userRequest.getLastName());
            existingUser.setRole(userRequest.getRole());
            existingUser.setEmail(userRequest.getEmail());
            existingUser.setPhone(userRequest.getPhone());

            // Update password if provided
            if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty()) {
                existingUser.setPassword(userRequest.getPassword());
            }

            return repository.save(existingUser);
        } else {
            // Handle case where user to update is not found
            return null;
        }
    }


    public String deleteUser(Integer userId) {
        Optional<User> user = repository.findById(String.valueOf(userId));
        if (user.isPresent()) {
            repository.deleteById(String.valueOf(userId));
            return userId + " User deleted";
        } else {
            return "User with ID " + userId + " not found";
        }
    }



    // Method to update user password
    public void updatePassword(User user, String newPassword) {
        // Hash the new password using BCrypt
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

        // Set the hashed password on the user entity
        user.setPassword(hashedPassword);

        // Save the user with the updated password
        repository.save(user);
    }
    public User getAdminById(Integer userId) {
        return mongoTemplate.findById(userId, User.class);
    }

    public User getUserById(Integer userId) {
        return mongoTemplate.findById(userId, User.class);
    }


    public List<Reservation> getReservationsByUserId(Integer userId) {
        return reservationRepository.findByUserId(userId);
    }
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll(Sort.by(Sort.Direction.DESC, "date"));
    }
    public void saveReservation(Reservation reservation) {
        mongoTemplate.save(reservation);
    }
    public void deleteReservationById(String reservationId) {
        System.out.println("Attempting to delete reservation with ID: " + reservationId);
        if (reservationRepository.existsById(reservationId)) {
            reservationRepository.deleteById(reservationId);
            System.out.println("Reservation with ID " + reservationId + " deleted successfully.");
        } else {
            System.out.println("Reservation with ID " + reservationId + " does not exist.");
        }
    }

    public boolean verifyPayment(String cardHolderName, String cardNumber, String expiryDate, String cvc) {
        // Simple regex patterns for basic validation
        String cardHolderNamePattern = "^[A-Za-z ]{2,30}$";
        String cardNumberPattern = "^\\d{4}-\\d{4}-\\d{4}-\\d{4}$";
        String expiryDatePattern = "^(0[1-9]|1[0-2])/(\\d{2})$";
        String cvcPattern = "^[0-9]{3,4}$";

        boolean isCardHolderNameValid = Pattern.matches(cardHolderNamePattern, cardHolderName);
        boolean isCardNumberValid = Pattern.matches(cardNumberPattern, cardNumber);
        boolean isExpiryDateValid = Pattern.matches(expiryDatePattern, expiryDate);
        boolean isCvcValid = Pattern.matches(cvcPattern, cvc);

        return isCardHolderNameValid && isCardNumberValid && isExpiryDateValid && isCvcValid;
    }

    public void saveContactMessage(String name, String email, String Message) throws IOException {
        String uniqueId = UUID.randomUUID().toString();
        ContactMessage msg = new ContactMessage();
//        msg.setId(uniqueId); // Set the generated ID
        msg.setName(name);
        msg.setEmail(email);
        msg.setMessage(Message);

        mongoTemplate.save(msg);
    }

    public void updateReservationStatus(String reservationId, String newStatus, Integer userId,String date, String time) throws IOException {
        // Find the existing reservation document
        Reservation reservation = mongoTemplate.findById(reservationId, Reservation.class);
        if (reservation != null) {
            // Check current status and update if needed
            String currentStatus = reservation.getStatus();
            if (!currentStatus.equals(newStatus)) {
                // Update the reservation status
                reservation.setStatus(newStatus);
                mongoTemplate.save(reservation);

                // Send email notification if transitioning to "Approved"
                if (newStatus.equals("Approved") && (currentStatus.equals("Pending") || currentStatus.equals("Canceled"))) {
                    User user = getUserById(userId);
                    String userEmail = user.getEmail();

                    String subject = "Reservation Status Updated";
                    String message = "Your reservation on " + date + " at " +time+ "  has been " + newStatus + " by the management. Thank you!.";
                    sendEmailWithCode(userEmail, subject, message);
                }
            } else {
                // Optionally handle the case where the status hasn't changed
                System.out.println("Status is already " + newStatus);
            }
        } else {
            throw new IllegalArgumentException("Reservation with ID " + reservationId + " not found.");
        }
    }

    public void cancelReservationStatus(String reservationId, String status, Integer userId,String date, String time) throws IOException {
        Reservation reservation = mongoTemplate.findById(reservationId, Reservation.class);

        if (reservation != null) {
            reservation.setStatus("Canceled");
            mongoTemplate.save(reservation);

            User user = getUserById(userId);
            String userEmail = user.getEmail();

            String subject = "Reservation Canceled";
            String message = "Your reservation on " + date + " at" +time+ " has been canceled by the management. Thank you!";
            sendEmailWithCode(userEmail, subject, message);
        } else {
            throw new IllegalArgumentException("Reservation with ID " + reservationId + " not found.");
        }
    }

    private void sendEmailWithCode(String email, String subject, String message) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(email);
            mail.setSubject(subject);
            mail.setText(message);
            mailSender.send(mail);
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception or handle it as needed
        }
    }



}
