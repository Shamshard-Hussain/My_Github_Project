package com.restaurant.Restaurant.Service;

import com.restaurant.Restaurant.Model.IncomeResult;
import com.restaurant.Restaurant.Model.Payment;
import com.restaurant.Restaurant.Repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;


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
            payment.setStatus(status); 
            paymentRepository.save(payment);
        }
    }

    public List<Payment> getPaymentsByTypeAndDateRange(String type, LocalDate dateFrom, LocalDate dateTo) {
        // Define the query criteria
        Query query = new Query();
        query.addCriteria(Criteria.where("type").is(type));
        query.addCriteria(Criteria.where("date").gte(dateFrom).lte(dateTo));

        // Execute the query
        return mongoTemplate.find(query, Payment.class);
    }

    public List<Payment> getPaymentsByDateRange(LocalDate dateFrom, LocalDate dateTo) {
        return paymentRepository.findByDateBetween(dateFrom, dateTo);
    }

    public long getTotalFoodOrders() {
        return paymentRepository.countByType("Food-Order");  // Use 'countByType'
    }

    // Calculate the total amount for all payments
    public BigDecimal getTotalAmount() {
        List<Payment> payments = paymentRepository.findAllAmounts();
        return payments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Calculate the total amount for today's payments
    public BigDecimal getTotalAmountForToday() {
        LocalDate today = LocalDate.now();
        List<Payment> todayPayments = paymentRepository.findAmountsByDate(today);
        return todayPayments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    // Method to get weekly income data
    public Map<String, BigDecimal> getWeeklyIncome() {
        Map<String, BigDecimal> weeklyIncome = new LinkedHashMap<>();
        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));

        for (int i = 0; i < 7; i++) {
            LocalDate date = startOfWeek.plusDays(i);
            BigDecimal totalIncome = getTotalIncomeByDate(date);
            weeklyIncome.put(date.getDayOfWeek().toString(), totalIncome);
        }

        return weeklyIncome;
    }

    private BigDecimal getTotalIncomeByDate(LocalDate date) {
        MatchOperation match = Aggregation.match(Criteria.where("date").is(date.toString()));
        GroupOperation group = Aggregation.group().sum("amount").as("totalIncome");
        Aggregation aggregation = Aggregation.newAggregation(match, group);
        AggregationResults<IncomeResult> results = mongoTemplate.aggregate(aggregation, "Payment", IncomeResult.class);

        IncomeResult result = results.getUniqueMappedResult();
        return result != null ? result.getTotalIncome() : BigDecimal.ZERO;
    }
}

