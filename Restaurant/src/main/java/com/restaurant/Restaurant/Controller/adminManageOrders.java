package com.restaurant.Restaurant.Controller;

import com.restaurant.Restaurant.Model.Product;
import com.restaurant.Restaurant.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class adminManageOrders {

    @Autowired
    private ProductService productService;

    @GetMapping("/orders")
    public String products(Model model) {
        return "admin/orders";
    }
}
