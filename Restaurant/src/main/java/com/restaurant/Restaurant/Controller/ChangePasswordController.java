package com.restaurant.Restaurant.Controller;

import com.restaurant.Restaurant.Model.User;
import com.restaurant.Restaurant.Service.RestaurantService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ChangePasswordController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private HttpSession session;

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
    public String changePassword(
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes) {

        // Retrieve email from the session
        String email = (String) session.getAttribute("email");

        // Validate that the new password and confirm password match
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("message", "Passwords do not match.");
            return "redirect:/changePassword";
        }

        // Find user by email
        User user = restaurantService.findUserByEmail(email).orElse(null);
        if (user == null) {
            redirectAttributes.addFlashAttribute("message", "User not found.");
            return "redirect:/changePassword";
        }

        // Update user password
        restaurantService.updatePassword(user, newPassword);

        // Clear email and code from session
        session.removeAttribute("verificationCode");
        session.removeAttribute("email");

        redirectAttributes.addFlashAttribute("message", "Password updated successfully.");
        return "redirect:/login";  // Redirect to log in or another page
    }
}