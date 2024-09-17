package com.restaurant.Restaurant.Repository;

import com.restaurant.Restaurant.Model.Bill;
import com.restaurant.Restaurant.Model.Payment;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {

    // Method to find payments by type
    List<Payment> findByType(String type);

    List<Payment> findByDateBetween(LocalDate dateFrom, LocalDate dateTo);

    long countByType(String type);  // Rename to match the 'type' field

    long countByTypeAndStatus(String type, String status);

    // Find total sum of all payment amounts
    @Query(value = "{}", fields = "{amount: 1}")
    List<Payment> findAllAmounts();

    // Find total sum of payment amounts for today only
    @Query(value = "{ 'date' : { $eq : ?0 }}", fields = "{amount: 1}")
    List<Payment> findAmountsByDate(LocalDate date);


}
