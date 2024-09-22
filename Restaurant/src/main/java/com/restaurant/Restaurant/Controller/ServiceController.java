package com.restaurant.Restaurant.Controller;

import com.restaurant.Restaurant.Model.ServicesClass;
import com.restaurant.Restaurant.Service.Services;
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
public class ServiceController {


    @Autowired
    private Services imageGalleryService;

    @GetMapping("/Services")
    public String Services(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId"); 
        String userType = (String) session.getAttribute("role");

        // Check if userType is neither Admin nor Staff
        if (userType == null || (!userType.equals("Admin") && !userType.equals("Staff"))) {
            model.addAttribute("accessDenied", true);
            return "admin/adminHome"; // Redirect to log in if session is not set
        }
        if (userId != null) {
            model.addAttribute("userId", userId);
            List<ServicesClass> gallery = imageGalleryService.getAllImage();
            model.addAttribute("Services", gallery);
            return "admin/Services";
        } else {
            return "redirect:/login"; // Redirect to log in if session is not set
        }
    }



    @PostMapping("/addServices")
    public String addNewServices(@RequestParam("headingName") String headingName,
                                  @RequestParam("description") String description,
                                  @RequestParam("image") MultipartFile image,
                                  RedirectAttributes redirectAttributes) {
        try {
            imageGalleryService.addServicesGallery(headingName, description, image);
            return "redirect:/admin/Services?message=success&operation=add";
        } catch (Exception e) {
            return "redirect:/admin/Services?message=error&operation=add";
        }
    }

    @DeleteMapping("/deleteServices/{id}")
    public ResponseEntity<String> deleteServices(@PathVariable String id) {
        try {
            imageGalleryService.deleteServicesGallery(id);
            return ResponseEntity.noContent().build(); //  successful delete
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete Services Gallery.");
        }
    }


    @PostMapping("/updateServicesGallery")
    public String updateServices(@RequestParam("id") String id,
                                @RequestParam("headingName") String headingName,
                                @RequestParam("description") String description,
                                @RequestParam("image") MultipartFile image,
                                RedirectAttributes redirectAttributes) {
        try {
            imageGalleryService.updateServicesGallery(id, headingName, description, image);
            return "redirect:/admin/Services?message=success&operation=update";
        } catch (Exception e) {
            return "redirect:/admin/Services?message=error&operation=update";
        }


    }


}
