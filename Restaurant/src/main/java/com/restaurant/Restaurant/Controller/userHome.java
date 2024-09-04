package com.restaurant.Restaurant.Controller;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.restaurant.Restaurant.Model.*;
import com.restaurant.Restaurant.Service.*;
import io.micrometer.core.instrument.util.IOUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Controller
@RequestMapping("/user")
public class userHome {
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


    @GetMapping("/userHome")
    public String userHome(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId"); // Ensure this is Integer

        if (userId != null) {
            model.addAttribute("userId", userId);

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

            return "user/userHome"; // Return the user home page template
        } else {
            return "redirect:/login"; // Redirect to log in if session is not set
        }
    }
    @GetMapping("/image")
    public ResponseEntity<InputStreamResource> getImage(@RequestParam String id) throws IOException {
        GridFSFile gridFSFile = gridFsTemplate.findOne(query(where("_id").is(id)));
        if (gridFSFile == null) {
            return ResponseEntity.notFound().build();
        }
        GridFsResource resource = gridFsTemplate.getResource(gridFSFile);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, resource.getContentType());
        return new ResponseEntity<>(new InputStreamResource(resource.getInputStream()), headers, HttpStatus.OK);
    }


    @PostMapping("/book")
    public String bookTable(@RequestParam String date,
                            @RequestParam String hours,
                            @RequestParam String name,
                            @RequestParam String phone,
                            @RequestParam int persons,
                            @RequestParam String cardHolderName,
                            @RequestParam String cardNumber,
                            @RequestParam String expiryDate,
                            @RequestParam String cvc,
                            @RequestParam String userId,
                            Model model) {

        // Verify payment details
        boolean isPaymentValid = restaurantService.verifyPayment(cardHolderName, cardNumber, expiryDate, cvc);

        if (!isPaymentValid) {
            System.out.println("Payment verification failed.");
            model.addAttribute("error", "Invalid payment details. Please try again.");
            return "redirect:/user/userHome#reservation"; // Return the user home page template
        }

        System.out.println("Payment verified successfully.");

        // Create a reservation object
        Reservation reservation = new Reservation();
        reservation.setDate(date);
        reservation.setHours(hours);
        reservation.setName(name);
        reservation.setPhone(phone);
        reservation.setPersons(persons);
        reservation.setStatus("Pending");
        reservation.setUserId(userId);

        // Save the reservation after successful payment verification
        restaurantService.saveReservation(reservation);
        System.out.println("Reservation saved successfully: " + reservation);

        // Redirect or return a view based on your needs
        model.addAttribute("success", "Reservation successful!");
        model.addAttribute("reservation", reservation);
        return "redirect:/user/userHome#reservation"; // Return the user home page template
    }


}