package com.restaurant.Restaurant.Controller;

import com.restaurant.Restaurant.Model.Product;
import com.restaurant.Restaurant.Model.User;
import com.restaurant.Restaurant.Service.PaymentService;
import com.restaurant.Restaurant.Service.ProductService;
import com.restaurant.Restaurant.Service.RestaurantService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class adminHome {

    @Autowired
    private ProductService productService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private PaymentService paymentService;



    @GetMapping("/adminHome")
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
            long totalProducts = productService.getTotalProductCount();
            model.addAttribute("totalProducts", totalProducts);

            long totalReservations = restaurantService.getTotalReservationCount();
            model.addAttribute("totalReservations", totalReservations);

            long totalFoodOrders = paymentService.getTotalFoodOrders();
            model.addAttribute("totalFoodOrders", totalFoodOrders);

            long newFoodOrdersCount = restaurantService.countNewFoodOrders();
            model.addAttribute("newFoodOrdersCount", newFoodOrdersCount);

            long newReservations = restaurantService.getNewReservationCount();
            model.addAttribute("newReservations", newReservations);

            long newMessageCount = restaurantService.countNewMessages();
            model.addAttribute("newMessageCount", newMessageCount);

            long totalInquiries = restaurantService.getTotalInquiryCount();
            model.addAttribute("totalInquiries", totalInquiries);

            model.addAttribute("userType", userType);
                BigDecimal totalAmount = paymentService.getTotalAmount(); // Method to calculate total income
                BigDecimal totalAmountToday = paymentService.getTotalAmountForToday(); // Method to calculate today's income

                model.addAttribute("totalAmount", totalAmount);
                model.addAttribute("totalAmountToday", totalAmountToday);


            return "admin/adminHome";
        } else {
            return "redirect:/login"; // Redirect to login if session is not set
        }



    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // Use false to not create a new session
        if (session != null) {
            session.invalidate(); // Invalidate the session
        }
        return "redirect:/login"; // Redirect to login page
    }

    @GetMapping("/getDetails")
    @ResponseBody
    public ResponseEntity<?> getAdminDetails(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId != null) {
            User admin = restaurantService.getAdminById(userId);
            if (admin != null) {
                return ResponseEntity.ok(admin);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Admin details not found");
    }


    @GetMapping("/weekly-income")
    public ResponseEntity<Map<String, BigDecimal>> getWeeklyIncome() {
        Map<String, BigDecimal> weeklyIncome = paymentService.getWeeklyIncome();
        return ResponseEntity.ok(weeklyIncome);
    }
}
