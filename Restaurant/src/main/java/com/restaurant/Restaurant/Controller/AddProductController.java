package com.restaurant.Restaurant.Controller;

import com.restaurant.Restaurant.Model.Product;
import com.restaurant.Restaurant.Service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Controller
@RequestMapping("/admin")
public class AddProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("productsPage")
    public String showProductPage(HttpServletRequest request,Model model) {

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
            List<Product> products = productService.getAllProducts();
            model.addAttribute("products", products);
            return "admin/productsPage";
        } else {
            return "redirect:/login"; // Redirect to log in if session is not set
        }

    }

    @PostMapping("/addProduct")
    @ResponseBody
    public Map<String, Object> addProduct(@RequestParam("productName") String productName,
                                          @RequestParam("price") double price,
                                          @RequestParam("category") String category,
                                          @RequestParam("description") String description,
                                          @RequestParam("image") MultipartFile image,
                                          RedirectAttributes redirectAttributes) {
        Map<String, Object> response = new HashMap<>();
        try {
            productService.addProduct(productName, price, category, description, image);
            response.put("success", true);
            response.put("message", "Product added successfully!");
        } catch (IOException e) {
            response.put("success", false);
            response.put("message", "Failed to add product.");
            e.printStackTrace();
        }
        return response;
    }


    @PostMapping("/deleteProduct/{id}")
    public String deleteProduct(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            Optional<Product> product = productService.getProductById(id);
            if (product.isPresent()) {
                productService.deleteProduct(id);
                redirectAttributes.addFlashAttribute("message", "Product deleted successfully.");
            } else {
                redirectAttributes.addFlashAttribute("error", "Product not found.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An error occurred while deleting the product.");
        }
        return "redirect:/admin/productsPage";
    }



    @GetMapping("/image/{id}")
    public ResponseEntity<ByteArrayResource> getImage(@PathVariable String id) {
        Optional<byte[]> image = productService.getImage(id);
        if (image.isPresent()) {
            ByteArrayResource resource = new ByteArrayResource(image.get());
            return ResponseEntity.ok()
                    .contentLength(image.get().length)
                    .header("Content-type", "image/jpeg")
                    .body(resource);
        }
        return ResponseEntity.notFound().build();
    }
    @PostMapping("/updateProduct")
    public String updateProduct(@RequestParam("id") String id,
                                @RequestParam("productName") String productName,
                                @RequestParam("price") double price,
                                @RequestParam("category") String category,
                                @RequestParam("description") String description,
                                @RequestParam("image") MultipartFile image,
                                RedirectAttributes redirectAttributes) {
        try {
            productService.updateProduct(id, productName, price, category, description, image);
            redirectAttributes.addFlashAttribute("message", "Product updated successfully!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("message", "Failed to update product.");
            e.printStackTrace();
        }
        return "redirect:/admin/productsPage";
    }




}

