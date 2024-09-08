package com.restaurant.Restaurant.Repository;

import com.restaurant.Restaurant.Model.ContactMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactMessageRepository extends MongoRepository<ContactMessage, String> {


}
