package com.restaurant.Restaurant.Controller;


import com.restaurant.Restaurant.Model.Locations;
import com.restaurant.Restaurant.Model.Payment;
import com.restaurant.Restaurant.Model.Product;
import com.restaurant.Restaurant.Service.LocationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class locationController {
    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private LocationService service;




    @GetMapping("/locations")
    public String Services(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId"); // Ensure this is Integer
        String userType = (String) session.getAttribute("role");

        // Check if userType is neither Admin nor Staff
        if (userType == null || (!userType.equals("Admin") && !userType.equals("Staff"))) {
            model.addAttribute("accessDenied", true);
            return "admin/adminHome"; // Redirect to Home in if session is not set
        }

        if (userId != null) {
            model.addAttribute("userId", userId);
            List<Locations> locationsList = service.getAllLocations();
            model.addAttribute("locations", locationsList);
            return "admin/locations";
        } else {
            return "redirect:/login"; // Redirect to log in if session is not set
        }
    }


    @PostMapping("/addLocations")
    public String addLocation(
            @RequestParam("id") String id,
            @RequestParam("City") String city,
            @RequestParam("Address") String address,
            @RequestParam("Phone") String phone,
            @RequestParam("Email") String email,
            @RequestParam("image") MultipartFile image,
            RedirectAttributes redirectAttributes) {
     try{
         service.saveLocation(id, city, address, phone, email, image); return "redirect:/admin/locations?message=success&operation=add";
     } catch (Exception e) {
         return "redirect:/admin/locations?message=error&operation=add";// Redirect to a locations page
     }

    }


    @DeleteMapping("/deleteLocation/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable String id) {
        try {
            service.deleteLocationById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/updateLocation")
    public String updateLocation(@RequestParam("id") String id,
                                               @RequestParam("city") String city,
                                               @RequestParam("address") String address,
                                               @RequestParam("phone") String phone,
                                               @RequestParam("email") String email,
                                               @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            service.updateLocation(id, city, address, phone, email, image);
            return "redirect:/admin/locations?message=success&operation=update";
        } catch (Exception e) {
            return "redirect:/admin/locations?message=error&operation=update";
        }
    }



    }
