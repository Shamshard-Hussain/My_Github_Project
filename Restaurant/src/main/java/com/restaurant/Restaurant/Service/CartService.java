package com.restaurant.Restaurant.Service;

import com.restaurant.Restaurant.Model.CartItem;
import com.restaurant.Restaurant.Repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;

    public boolean isProductInCart(String productId, String userId) {
        return cartRepository.findByProductIdAndUserId(productId, userId).isPresent();
    }

    public void addToCart(CartItem cartItem) {
        cartRepository.save(cartItem);
    }
    public List<CartItem> getCartItemsByUserId(String userId) {
        return cartRepository.findByUserId(userId);
    }

    public boolean removeFromCart(String productId, String userId) {
        Optional<CartItem> optionalCartItem = cartRepository.findByProductIdAndUserId(productId, userId);

        if (optionalCartItem.isPresent()) {
            CartItem cartItem = optionalCartItem.get();
            cartRepository.delete(cartItem);
            return true;
        }

        return false;
    }


    public CartItem getCartItem(String productId, String userId) {
        return cartRepository.findItemByProductIdAndUserId(productId, userId);
    }

    public void updateCartItem(CartItem cartItem) {
        cartRepository.save(cartItem);
    }

}
