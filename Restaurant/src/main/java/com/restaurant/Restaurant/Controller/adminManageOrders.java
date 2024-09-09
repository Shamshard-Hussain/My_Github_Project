package com.restaurant.Restaurant.Controller;

import com.restaurant.Restaurant.Model.Reservation;
import com.restaurant.Restaurant.Service.RestaurantService;
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
    public String showReservations(Model model) {
        List<Reservation> reservations = restaurantService.getAllReservations();
        model.addAttribute("reservations", reservations);
        return "/admin/reservations"; // return the name of your Thymeleaf template
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
