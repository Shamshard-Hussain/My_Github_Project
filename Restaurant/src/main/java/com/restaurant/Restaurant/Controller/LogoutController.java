package com.restaurant.Restaurant.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class LogoutController {

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        // Invalidate the session
        request.getSession().invalidate();
        // Redirect to the login page or any other page
        return "redirect:/login";
    }
}
