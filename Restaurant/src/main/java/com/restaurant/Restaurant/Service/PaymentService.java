package com.restaurant.Restaurant.Service;

import com.restaurant.Restaurant.Model.Bill;
import com.restaurant.Restaurant.Model.Payment;
import com.restaurant.Restaurant.Model.User;
import com.restaurant.Restaurant.Repository.BillRepository;
import com.restaurant.Restaurant.Repository.PaymentRepository;
import com.restaurant.Restaurant.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private MongoTemplate mongoTemplate;
    // Fetch all payments
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    // Fetch payments by type "Food-Order"
    public List<Payment> getPaymentsByType(String type) {
        return paymentRepository.findByType(type);
    }

    // Fetch a payment by its ID
    public Optional<Payment> getPaymentById(String id) {
        return paymentRepository.findById(id);
    }



    // Update the status of a payment
    public void updatePaymentStatus(String id, String status) {
        Optional<Payment> paymentOpt = paymentRepository.findById(id);
        if (paymentOpt.isPresent()) {
            Payment payment = paymentOpt.get();
            payment.setStatus(status); // Assuming 'type' field represents status
            paymentRepository.save(payment);
        }
    }

    // Save or update a payment
    public Payment saveOrUpdatePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    // Delete a payment by its ID
    public void deletePayment(String id) {
        paymentRepository.deleteById(id);
    }



    public Payment findById(String id) {
        return paymentRepository.findById(id)
                .orElse(null); // or throw an exception if not found
    }
}
