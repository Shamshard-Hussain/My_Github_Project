package com.restaurant.Restaurant.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

import com.restaurant.Restaurant.Model.Reservation;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.restaurant.Restaurant.Model.User;
import com.restaurant.Restaurant.Repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;

@Service
public class RestaurantService {

    @Autowired
    private UserRepository repository;
    @Autowired
    private MongoTemplate mongoTemplate;

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
        Optional<User> user = repository.findById(userId);
        return user.orElse(null); // Return null if user not found
    }
    public String getUserDetailsByRole(String role) {
        List<User> users = repository.findByRole(role);

        if (users.isEmpty()) {
            return "No user found with role: " + role;
        } else {
            User user = users.get(0); // Assuming you want the first user in the list
            return "User ID: " + user.getUserId() + ", Role: " + user.getRole();
        }
    }




    public User updateUser(User userRequest) {
        Optional<User> optionalExistingUser = repository.findById(userRequest.getUserId());
        if (optionalExistingUser.isPresent()) {
            User existingUser = optionalExistingUser.get();
            existingUser.setFirstName(userRequest.getFirstName());
            existingUser.setLastName(userRequest.getLastName());
            existingUser.setRole(userRequest.getRole());
            existingUser.setEmail(userRequest.getEmail());
            existingUser.setPhone(userRequest.getPhone());
            return repository.save(existingUser);
        } else {
            // Handle case where user to update is not found
            return null;
        }
    }

    public String deleteUser(Integer userId) {
        Optional<User> user = repository.findById(userId);
        if (user.isPresent()) {
            repository.deleteById(userId);
            return userId + " User deleted";
        } else {
            return "User with ID " + userId + " not found";
        }
    }

    // Method to update user password
    public void updatePassword(User user, String newPassword) {
        // Hash the new password using BCrypt directly
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        // Update the password in the user object
        user.setPassword(hashedPassword);
        // Save the updated user back into the in-memory store
        userStore.put(user.getEmail(), user);
    }
    public User getAdminById(Integer userId) {
        return mongoTemplate.findById(userId, User.class);
    }





    public void saveReservation(Reservation reservation) {
        mongoTemplate.save(reservation);
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

}
