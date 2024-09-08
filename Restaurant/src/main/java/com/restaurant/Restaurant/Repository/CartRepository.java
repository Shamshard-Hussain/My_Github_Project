package com.restaurant.Restaurant.Repository;

import com.restaurant.Restaurant.Model.CartItem;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends MongoRepository<CartItem, String> {
    Optional<CartItem> findByProductIdAndUserId(String productId, String userId);

    List<CartItem> findByUserId(String userId);

    CartItem findItemByProductIdAndUserId(String productId, String userId);

}