package com.restaurant.Restaurant.Repository;

import com.restaurant.Restaurant.Model.Bill;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BillRepository extends MongoRepository<Bill, String> {
}