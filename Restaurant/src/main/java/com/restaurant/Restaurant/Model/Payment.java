package com.restaurant.Restaurant.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;

@Document(collection = "Payment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    private String id;
    private int userId;
    private String type;
    private String description;
    private BigDecimal amount;
    private LocalDate date;  // Changed to LocalDate
    private String status;
}
