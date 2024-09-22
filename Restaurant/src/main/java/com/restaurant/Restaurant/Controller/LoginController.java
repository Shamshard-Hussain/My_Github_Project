package com.restaurant.Restaurant.Controller;

import java.util.Optional;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.restaurant.Restaurant.Model.User;
import com.restaurant.Restaurant.Service.RestaurantService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private RestaurantService restaurantService;

    @GetMapping("/login")
    public String loginForm() {
        return "login"; 
    }

    @PostMapping("/login")
    public String loginSubmit(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        Optional<User> userOptional = restaurantService.findUserByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (BCrypt.checkpw(password, user.getPassword())) {
                // Set user ID in session
                HttpSession session = request.getSession();
                session.setAttribute("userId", user.getUserId());
                session.setAttribute("role", user.getRole());

                // Redirect based on user role
                if (user.getRole().equals("User")) {
                    return "redirect:/user/userHome"; // Redirect to user homepage
                } else if (user.getRole().equals("Admin")) {
                    return "redirect:/admin/adminHome"; // Redirect to admin homepage
                }else if (user.getRole().equals("Staff")) {
                    return "redirect:/admin/adminHome"; // Redirect to admin homepage
                } else {
                    model.addAttribute("error", "Unsupported user role.");
                    return "login"; // Return to login page with error
                }
            } else {
                // Handle invalid password
                model.addAttribute("error", "Invalid email or password.");
                return "login"; // Return the login.html template with error message
            }
        } else {
            // Handle user not found
            model.addAttribute("error", "User not found with this email: " + email);
            return "login"; // Return the login.html template with error message
        }
    }
}
