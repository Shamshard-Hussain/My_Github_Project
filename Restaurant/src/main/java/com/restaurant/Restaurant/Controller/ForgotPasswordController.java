package com.restaurant.Restaurant.Controller;

import com.restaurant.Restaurant.Model.User;
import com.restaurant.Restaurant.Service.RestaurantService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Controller
public class ForgotPasswordController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private HttpSession session;

    @GetMapping("/forgotPassword")
    public String showForgotPasswordPage(Model model) {
        model.addAttribute("errorMessage", "");
        return "forgotPassword";
    }

    @PostMapping("/sendCode")
    @ResponseBody
    public Map<String, Object> sendVerificationCode(@RequestParam("email") String email) {
        Map<String, Object> response = new HashMap<>();

        // Check if the email exists in the database
        Optional<User> userOptional = restaurantService.findUserByEmail(email);
        if (userOptional.isEmpty()) {
            response.put("success", false);
            response.put("message", "Email does not exist.");
            return response;
        }

        // Generate and send the verification code
        String verificationCode = generateRandomCode();
        sendEmailWithCode(email, verificationCode);

        // Save the verification code and email in the session
        session.setAttribute("verificationCode", verificationCode);
        session.setAttribute("email", email);

        response.put("success", true);
        response.put("message", "Verification code sent to your email.");
        return response;
    }
    @PostMapping("/verifyCode")
    @ResponseBody
    public Map<String, Object> verifyCode(@RequestParam("code") String code) {
        Map<String, Object> response = new HashMap<>();

        String sessionCode = (String) session.getAttribute("verificationCode");
        if (sessionCode != null && sessionCode.equals(code)) {
            response.put("success", true);
            response.put("message", "Verification code confirmed.");
        } else {
            response.put("success", false);
            response.put("message", "Invalid verification code.");
        }

        return response;
    }
    // Generate a random 6-digit code
    private String generateRandomCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    private void sendEmailWithCode(String email, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Verification Code");
        message.setText("Your verification code is: " + code);
        mailSender.send(message);
    }
}