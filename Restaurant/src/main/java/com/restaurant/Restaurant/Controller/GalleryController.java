package com.restaurant.Restaurant.Controller;

import com.restaurant.Restaurant.Model.HomeImageGallery;
import com.restaurant.Restaurant.Service.ImageGalleryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class GalleryController {


    @Autowired
    private ImageGalleryService imageGalleryService;

    @GetMapping("/gallery1")
    public String Gallery(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        String userType = (String) session.getAttribute("role");


        if (userType == null || (!userType.equals("Admin") && !userType.equals("Staff"))) {
            model.addAttribute("accessDenied", true);
            return "admin/adminHome"; // Redirect to log in if session is not set
        }


        if (userId != null) {
            model.addAttribute("userId", userId);
            model.addAttribute("userType", userType); // Add userType to the model
            List<HomeImageGallery> gallery = imageGalleryService.getAllImage();
            model.addAttribute("ImageGallery", gallery);
            return "admin/gallery1";
        } else {
            return "redirect:/login"; // Redirect to log in if session is not set
        }
    }




    @PostMapping("/add")
    public String addImageGallery(@RequestParam("headingName") String headingName,
                                  @RequestParam("description") String description,
                                  @RequestParam("image") MultipartFile image,
                                  RedirectAttributes redirectAttributes) {
        try {
            imageGalleryService.addImageGallery(headingName, description, image);
            return "redirect:/admin/gallery1?message=success&operation=add";
        } catch (Exception e) {
            return "redirect:/admin/gallery1?message=error&operation=add";
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteGallery(@PathVariable String id) {
        try {
            imageGalleryService.deleteImageGallery(id);
            return ResponseEntity.noContent().build(); // 204 No Content for successful delete
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete Image Gallery.");
        }
    }


    @PostMapping("/updateGallery")
    public String updateGallery(@RequestParam("id") String id,
                                @RequestParam("headingName") String headingName,
                                @RequestParam("description") String description,
                                @RequestParam("image") MultipartFile image,
                                RedirectAttributes redirectAttributes) {
        try {
            imageGalleryService.updateImageGallery(id, headingName, description, image);
            return "redirect:/admin/gallery1?message=success&operation=update";
        } catch (Exception e) {
            return "redirect:/admin/gallery1?message=error&operation=update";
        }


    }

}
