package com.restaurant.Restaurant.Controller;

import com.restaurant.Restaurant.Model.ImageCard;
import com.restaurant.Restaurant.Service.ImageGalleryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class ImageCardController {


    @Autowired
    private ImageGalleryService imageCardService;

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String ID_PREFIX = "i";


    @GetMapping("/ImageCards")
    public String showImageGallery(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId"); // Ensure this is Integer
        String userType = (String) session.getAttribute("role");

        // Check if userType is neither Admin nor Staff
        if (userType == null || (!userType.equals("Admin") && !userType.equals("Staff"))) {
            model.addAttribute("accessDenied", true);
            return "admin/adminHome"; // Redirect to log in if session is not set
        }

        if (userId != null) {
            // Fetch all image cards from the MongoDB collection
            List<ImageCard> imageCards = imageCardService.getAllImageCards();
            // Add the imageCards list to the model, so it can be accessed in the view
            model.addAttribute("imageCards", imageCards);
            return "admin/ImageCards";
        } else {
            return "redirect:/login"; // Redirect to log in if session is not set
        }
    }


    @PostMapping("/addImage")
    public String addImage(@RequestParam("imageId") String imageId,
                           @RequestParam("image") MultipartFile image) {
        try {
            System.out.println("Received image upload request with ID: " + imageId);
            imageCardService.saveImage(imageId, image);
            return "redirect:/admin/ImageCards?message=success&operation=add";
        } catch (IOException e) {
            System.err.println("IOException during image upload: " + e.getMessage());
            return "redirect:/admin/ImageCards?message=error&operation=add";
        } catch (RuntimeException e) {
            System.err.println("RuntimeException during image upload: " + e.getMessage());
            if (e.getMessage().equals("Image ID already exists in the database")) {
                return "redirect:/admin/ImageCards?message=duplicate&operation=add";
            }
            return "redirect:/admin/ImageCards?message=error&operation=add";
        }
    }


    @PostMapping("/deleteLocation")
    public String deleteLocation(@RequestParam("id") String id) {
        imageCardService.deleteLocationById(id);
        return "redirect:/admin/ImageCards"; // Redirect to the updated list of locations
    }




}
