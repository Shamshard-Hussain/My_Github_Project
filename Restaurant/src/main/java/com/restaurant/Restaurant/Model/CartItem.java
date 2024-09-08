package com.restaurant.Restaurant.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "Cart")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
    @Id
    private String cartId;
    private String productId;
    private String userId;
    private String productName;
    private String image;
    private double price;
    private int quantity;

}

