package com.restaurant.Restaurant.Repository;

import com.restaurant.Restaurant.Model.Bill;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillRepository extends MongoRepository<Bill, String> {
    List<Bill> findByBillId(String paymentId);
    long countByStatus(String status);

}

