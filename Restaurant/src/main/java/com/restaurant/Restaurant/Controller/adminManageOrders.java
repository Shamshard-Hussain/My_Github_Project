package com.restaurant.Restaurant.Controller;

import com.restaurant.Restaurant.Model.Reservation;
import com.restaurant.Restaurant.Service.RestaurantService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class adminManageOrders {

    @Autowired
    private RestaurantService restaurantService;



    @GetMapping("/reservations")
    public String showReservations(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        String userType = (String) session.getAttribute("role");

        // Check if userType is neither Admin nor Staff
        if (userType == null || (!userType.equals("Admin") && !userType.equals("Staff"))) {
            model.addAttribute("accessDenied", true);
            return "admin/adminHome"; // Redirect to Home in if session is not set
        }

        if (userId != null) {
            List<Reservation> reservations = restaurantService.getAllReservations();
            model.addAttribute("reservations", reservations);
            return "/admin/reservations";
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
        return "redirect:/admin/reservations"; 
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
        return "redirect:/admin/reservations";
    }


}
