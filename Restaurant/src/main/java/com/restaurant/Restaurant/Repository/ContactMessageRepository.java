package com.restaurant.Restaurant.Repository;

import com.restaurant.Restaurant.Model.ContactMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactMessageRepository extends MongoRepository<ContactMessage, String> {

    long countByStatus(String status);

    // Method to fetch messages with status 'new'
    List<ContactMessage> findByStatus(String status);
}
