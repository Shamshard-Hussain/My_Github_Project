package com.restaurant.Restaurant.Controller;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.restaurant.Restaurant.Model.*;
import com.restaurant.Restaurant.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
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
    public String bookTable(HttpServletRequest request,@RequestParam String date,
                            @RequestParam String hours,
                            @RequestParam String name,
                            @RequestParam String phone,
                            @RequestParam int persons,
                            @RequestParam String cardHolderName,
                            @RequestParam String cardNumber,
                            @RequestParam String expiryDate,
                            @RequestParam String cvc,
                            Model model) {

        // Verify payment details
        boolean isPaymentValid = restaurantService.verifyPayment(cardHolderName, cardNumber, expiryDate, cvc);

        if (!isPaymentValid) {
            System.out.println("Payment verification failed.");
            model.addAttribute("error", "Invalid payment details. Please try again.");
            return "redirect:/user/userHome#reservation"; // Return the user home page template
        }
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        System.out.println("Payment verified successfully.");

        // Create a reservation object
        Reservation reservation = new Reservation();
        reservation.setDate(date);
        reservation.setTime(hours);
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


    @PostMapping("/contact")
    public ResponseEntity<String> saveContactMessage(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("message") String message) throws IOException {

        restaurantService.saveContactMessage(name, email, message);
        return ResponseEntity.ok("Message received, thank you!");
    }

    @GetMapping("/getDetails")
    @ResponseBody
    public ResponseEntity<?> getUserById(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId != null) {
            User admin = restaurantService.getUserById(userId);
            if (admin != null) {
                return ResponseEntity.ok(admin);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User details not found");
    }

    @GetMapping("/getReservations")
    @ResponseBody
    public ResponseEntity<?> getReservationsByUserId(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");

        if (userId != null) {
            List<Reservation> reservations = restaurantService.getReservationsByUserId(userId);
            if (reservations != null && !reservations.isEmpty()) {
                return ResponseEntity.ok(reservations);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reservations not found");
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<?> deleteReservation(@PathVariable String id) {
        System.out.println("Received request to delete reservation with ID: " + id);
        try {
            restaurantService.deleteReservationById(id);
            return ResponseEntity.ok("Reservation deleted successfully");
        } catch (Exception e) {
            System.out.println("Error deleting reservation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting reservation");
        }
    }

}