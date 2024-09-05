package com.restaurant.Restaurant.Controller;

import com.restaurant.Restaurant.Model.ServicesClass;
import com.restaurant.Restaurant.Model.User;
import com.restaurant.Restaurant.Service.RestaurantService;
import com.restaurant.Restaurant.Service.Services;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class StaffAccountController {
    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private RestaurantService Service;



    @GetMapping("/profile")
    public String getUsersByRole(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId"); // Ensure this is Integer
        String userType = (String) session.getAttribute("role");

        // Check if userType is neither Admin nor Staff
        if (userType == null || (!userType.equals("Admin") )) {
            model.addAttribute("notification", "Access Denied");
         //   model.addAttribute("accessDenied", true);
            return "admin/profile"; // Redirect to log in if session is not set
        }
        if (userId != null) {
            model.addAttribute("userId", userId);
            List<User> users = Service.getUsersByRole("Staff");
            model.addAttribute("user", users);
            return "admin/profile";

        } else {
            return "redirect:/login"; // Redirect to log in if session is not set
        }
    }




    @PostMapping("/registration")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> registerUser(HttpServletRequest request,
                                                            @ModelAttribute("user") User user,
                                                            @RequestParam("confirmPassword") String confirmPassword) {
        HttpSession session = request.getSession();
        String userType = (String) session.getAttribute("role");
        Optional<User> existingUserByEmail = Service.findUserByEmail(user.getEmail());

        Map<String, Object> response = new HashMap<>();
        if (!"Admin".equals(userType)) {
            response.put("success", false);
            response.put("message", "Access Denied.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        } else if (existingUserByEmail.isPresent()) {
            response.put("success", false);
            response.put("message", "Email already registered.");
            return ResponseEntity.badRequest().body(response);
        } else if (!user.getPassword().equals(confirmPassword)) {
            response.put("success", false);
            response.put("message", "Passwords do not match.");
            return ResponseEntity.badRequest().body(response);
        } else {
            // Generate random userId
            Random random = new Random();
            user.setUserId(random.nextInt(Integer.MAX_VALUE));
            user.setRole("Staff");
            user.setDate(LocalDate.now().toString()); // Set date to today's date

            // Save the user
            Service.addUser(user);

            response.put("success", true);
            response.put("message", "User registered successfully.");
            return ResponseEntity.ok(response);
        }
    }






    @ModelAttribute("user")
    public User getUser() {
        return new User();
    }

}

