package com.restaurant.Restaurant.Controller;

import com.restaurant.Restaurant.Model.User;
import com.restaurant.Restaurant.Service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;
import java.util.Random;

@Controller
public class RegisterController {
    @Autowired
    private RestaurantService userService;

    @GetMapping("/Register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("User", new User());
        return "Register"; // Ensure this matches your HTML filename
    }

    @PostMapping("/Register")
    public String registerUser(@ModelAttribute("user") User user, String confirmPassword, Model model) {
        // Check if email already exists
        Optional<User> existingUserByEmail = userService.findUserByEmail(user.getEmail());
        if (existingUserByEmail.isPresent()) {
            model.addAttribute("error", "Email already registered.");
            return "Register";
        } else if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match.");
            return "Register";
        } else {
            // Generate random userId
            Random random = new Random();
            user.setUserId(random.nextInt(Integer.MAX_VALUE));
            user.setRole("User");
            // Save the user
            userService.addUser(user);
            return "redirect:/login";
        }
    }


}
