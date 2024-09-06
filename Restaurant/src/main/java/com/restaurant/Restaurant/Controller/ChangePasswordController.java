package com.restaurant.Restaurant.Controller;

import com.restaurant.Restaurant.Model.User;
import com.restaurant.Restaurant.Repository.ReservationRepository;
import com.restaurant.Restaurant.Repository.UserRepository;
import com.restaurant.Restaurant.Service.RestaurantService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class ChangePasswordController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private HttpSession session;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/changePassword")
    public String showForgotPasswordPage(Model model) {
        String email = (String) session.getAttribute("email");
        if (email == null || session.getAttribute("verificationCode") == null) {
            return "redirect:/forgotPassword";
        }
        model.addAttribute("email", email);
        return "changePassword";
    }

    @PostMapping("/changePassword")
    public String changePassword(@RequestParam("email") String email,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Model model) {

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("message", "Passwords do not match!");
            return "changePassword";
        }

        // Fetch the user by email using findByEmail method
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            restaurantService.updatePassword(user, newPassword);
            model.addAttribute("message", "Password updated successfully!");
            return "redirect:/login";
        } else {
            model.addAttribute("message", "User not found!");
            return "changePassword";
        }
    }



}