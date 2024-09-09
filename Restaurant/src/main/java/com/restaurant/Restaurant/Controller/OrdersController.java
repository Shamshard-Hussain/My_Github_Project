package com.restaurant.Restaurant.Controller;

import com.restaurant.Restaurant.Model.Bill;
import com.restaurant.Restaurant.Model.Payment;
import com.restaurant.Restaurant.Model.Product;
import com.restaurant.Restaurant.Model.User;
import com.restaurant.Restaurant.Repository.UserRepository;
import com.restaurant.Restaurant.Service.PaymentService;
import com.restaurant.Restaurant.Service.RestaurantService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class OrdersController {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private RestaurantService billService;

    @Autowired
    private UserRepository userRepository;



    @GetMapping("/orders")
    public String products(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId"); // Ensure this is Integer
        String userType = (String) session.getAttribute("role");

        // Check if userType is neither Admin nor Staff
        if (userType == null || (!userType.equals("Admin") && !userType.equals("Staff"))) {
            model.addAttribute("accessDenied", true);
            return "redirect:/login"; // Redirect to log in if session is not set
        }

        if (userId != null) {
            model.addAttribute("userId", userId);
            List<Payment> payments = paymentService.getPaymentsByType("Food-Order");
            model.addAttribute("payments", payments);

            return "admin/orders";
        } else {
            return "redirect:/login"; // Redirect to log in if session is not set
        }

    }

    @PostMapping("/changeOrderStatus")
    public String changeOrderStatus(@RequestParam String reservationId,
                                    @RequestParam String status,
                                    Model model) {
        paymentService.updatePaymentStatus(reservationId, status);
        return "redirect:/admin/orders"; // Redirect to avoid form resubmission
    }



    @GetMapping("/user/{userId}")
    public ResponseEntity<User> getUser(@PathVariable int userId) {
        User user = billService.getUserById(userId);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @GetMapping("/bills/{paymentId}")
    public ResponseEntity<List<Bill>> getBills(@PathVariable String paymentId) {
        List<Bill> bills = billService.getBillsByPaymentId(paymentId);
        return ResponseEntity.ok(bills);
    }

}
