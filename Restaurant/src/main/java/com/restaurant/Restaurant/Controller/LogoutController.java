package com.restaurant.Restaurant.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class LogoutController {

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        // Invalidate the session
        request.getSession().invalidate();
        return "redirect:/login";
    }
}
