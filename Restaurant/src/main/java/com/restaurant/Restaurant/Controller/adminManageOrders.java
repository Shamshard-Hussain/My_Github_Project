package com.restaurant.Restaurant.Controller;

import com.restaurant.Restaurant.Model.Locations;
import com.restaurant.Restaurant.Model.Reservation;
import com.restaurant.Restaurant.Service.RestaurantService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class adminManageOrders {

    @Autowired
    private RestaurantService restaurantService;



    @GetMapping("/reservations")
    public String showReservations(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId"); // Ensure this is Integer
        String userType = (String) session.getAttribute("role");

        // Check if userType is neither Admin nor Staff
        if (userType == null || (!userType.equals("Admin") && !userType.equals("Staff"))) {
            model.addAttribute("accessDenied", true);
            return "admin/adminHome"; // Redirect to Home in if session is not set
        }

        if (userId != null) {
            List<Reservation> reservations = restaurantService.getAllReservations();
            model.addAttribute("reservations", reservations);
            return "/admin/reservations"; // return the name of your Thymeleaf template
        } else {
            return "redirect:/login"; // Redirect to log in if session is not set
        }
    }



    @PostMapping("/updateReservationStatus")
    public String updateReservationStatus(@RequestParam("reservationId") String reservationId,
                                          @RequestParam("userId") Integer userId,
                                          @RequestParam("date") String date,
                                          @RequestParam("time") String time,
                                          @RequestParam("status") String status) {
        try {
            restaurantService.updateReservationStatus(reservationId, status,userId,date, time);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/admin/reservations"; // Redirect to the reservations page or any desired page
    }


    @PostMapping("/cancelReservationStatus")
    public String   cancelReservationStatus(@RequestParam("reservationId") String reservationId,
                                            @RequestParam("userId") Integer userId,
                                            @RequestParam("date") String date,
                                            @RequestParam("time") String time,
                                            @RequestParam("status") String status) {
        try {
            restaurantService.updateReservationStatus(reservationId, status,userId,date, time);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/admin/reservations"; // Redirect to the reservations page or any desired page
    }


}
