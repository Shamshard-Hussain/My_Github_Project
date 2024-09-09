package com.restaurant.Restaurant.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Bill")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bill {
    private String billId;
    private String productId;
    private int userId;
    private String productName;
    private double price;
    private int quantity;
    private String status;
}
