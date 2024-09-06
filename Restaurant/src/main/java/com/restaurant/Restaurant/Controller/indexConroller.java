package com.restaurant.Restaurant.Controller;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.restaurant.Restaurant.Model.*;
import com.restaurant.Restaurant.Service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
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
    private GridFsTemplate gridFsTemplate;

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
            // Add the imageCards list to the model, so it can be accessed in the view
            model.addAttribute("imageCards", imageCards);

            return "user/Index"; // Return the user home page template

    }


}
