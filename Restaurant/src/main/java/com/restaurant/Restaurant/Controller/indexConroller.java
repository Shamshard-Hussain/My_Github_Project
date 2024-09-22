package com.restaurant.Restaurant.Controller;

import com.restaurant.Restaurant.Model.*;
import com.restaurant.Restaurant.Service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
@Controller
@RequestMapping("/user")
public class indexConroller {

    @Autowired
    private ImageGalleryService imageGalleryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private Services service;

    @Autowired
    private RestaurantService restaurantService;


    @GetMapping("/index")
    public String userHome(HttpServletRequest request, Model model) {


            List<HomeImageGallery> gallery = imageGalleryService.getAllImage();
            model.addAttribute("gallery", gallery);

            List<Product> products = productService.getAllProducts();
            model.addAttribute("products", products);

            List<Locations> locationsList = locationService.getAllLocations();
            model.addAttribute("locations", locationsList);

            List<ServicesClass> services = service.getAllImage();
            model.addAttribute("services", services);

            List<ImageCard> imageCards = imageGalleryService.getAllImageCards();
            model.addAttribute("imageCards", imageCards);

            return "user/Index"; 

    }

    @PostMapping("/contacts")
    public ResponseEntity<String> saveContactMessage(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("message") String message) throws IOException {

        restaurantService.saveContactMessage(name, email, message);
        return ResponseEntity.ok("Message received, thank you!");
    }

}
