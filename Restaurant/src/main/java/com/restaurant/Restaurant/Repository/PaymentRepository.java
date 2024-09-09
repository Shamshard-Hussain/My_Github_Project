package com.restaurant.Restaurant.Repository;

import com.restaurant.Restaurant.Model.Bill;
import com.restaurant.Restaurant.Model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {

    // Method to find payments by type
    List<Payment> findByType(String type);
    List<Payment> findByTypeAndDateBetween(String type, LocalDateTime startDate, LocalDateTime endDate);

    List<Payment> findByDateBetween(LocalDate dateFrom, LocalDate dateTo);
}