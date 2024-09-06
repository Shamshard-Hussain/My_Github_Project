package com.restaurant.Restaurant.Repository;

import java.util.List;
import java.util.Optional;

import com.restaurant.Restaurant.Model.Reservation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.restaurant.Restaurant.Model.User;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);// This method seems redundant if you have findByEmail already

    List<User> findByRole(String role);

    @Query("{ 'email' : ?0 }")
    Optional<User> findByEmailExist(String email); // Corrected method name and @Query annotation

    // Method to find a user by ID
    Optional<User> findById(String id);

    // Method to delete a user by ID
    List<User> findByUserId(Integer userId);
}

