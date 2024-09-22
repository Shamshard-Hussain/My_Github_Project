package com.restaurant.Restaurant.Controller;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.restaurant.Restaurant.Model.*;
import com.restaurant.Restaurant.Repository.BillRepository;
import com.restaurant.Restaurant.Repository.UserRepository;
import com.restaurant.Restaurant.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

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

    @Autowired
    private CartService cartService;
    private MongoTemplate mongoTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BillRepository billRepository;

    @GetMapping("/userHome")
    public String userHome(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId"); 

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
    public String bookTable(HttpServletRequest request, @RequestParam String date,
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

        Random random = new Random();
        int randomId = random.nextInt(1000000); //  range

        // Create a reservation object
        Reservation reservation = new Reservation();
        reservation.setId(String.valueOf(randomId));
        reservation.setDate(date);
        reservation.setTime(hours);
        reservation.setName(name);
        reservation.setPhone(phone);
        reservation.setPersons(persons);
        reservation.setStatus("Pending");
        reservation.setUserId(userId);


        Payment payment = new Payment();
        payment.setUserId(userId);
        payment.setDescription("Reservation Id: " + randomId);
        payment.setAmount(new BigDecimal("200.0"));
        payment.setType("Reservation");
        payment.setDate(LocalDate.now());
        payment.setStatus("Confirmed");


        // Save the reservation after successful payment verification
        restaurantService.saveReservation(reservation);
        restaurantService.savePayment(payment);
        System.out.println("Reservation saved successfully: " + reservation);

  
        model.addAttribute("success", "Reservation successful!");
        model.addAttribute("reservation", reservation);
        return "redirect:/user/userHome#reservation"; 
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


    @PostMapping("/reservations/{id}")
    public ResponseEntity<?> cancelReservation(@PathVariable String id, @RequestBody Map<String, String> requestBody) {
        String status = requestBody.get("status");

        try {
            Reservation reservation = mongoTemplate.findById(id, Reservation.class);

            if (reservation != null) {
                reservation.setStatus(status); // Update status
                mongoTemplate.save(reservation);
                return ResponseEntity.ok("Reservation has been canceled.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reservation with ID " + id + " not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating reservation status.");
        }
    }


    @PostMapping("/addToCart")
    public ResponseEntity<?> addToCart(HttpSession session,
                                       @RequestParam String productId,
                                       @RequestParam String productName,
                                       @RequestParam String image,
                                       @RequestParam double price) {
        try {
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"message\": \"User not logged in\"}");
            }

            String userIdStr = String.valueOf(userId);
            boolean isInCart = cartService.isProductInCart(productId, userIdStr);
            if (isInCart) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"Product already in cart\"}");
            }

            CartItem cartItem = new CartItem();
            cartItem.setProductId(productId);
            cartItem.setUserId(userIdStr);
            cartItem.setProductName(productName);
            cartItem.setImage(image);
            cartItem.setPrice(price);
            cartItem.setQuantity(1); // Set initial quantity to 1

            cartService.addToCart(cartItem);
            return ResponseEntity.ok("{\"message\": \"Product added to cart\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"message\": \"An unexpected error occurred. Please try again.\"}");
        }
    }


    @GetMapping("/cart")
    public ResponseEntity<List<CartItem>> getCartItems(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<CartItem> cartItems = cartService.getCartItemsByUserId(String.valueOf(userId));
        return ResponseEntity.ok(cartItems);
    }

    @PostMapping("/isProductInCart")
    public ResponseEntity<Map<String, Boolean>> isProductInCart(HttpSession session, @RequestParam String productId) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("exists", false));
        }

        String userIdStr = String.valueOf(userId);
        boolean isInCart = cartService.isProductInCart(productId, userIdStr);
        return ResponseEntity.ok(Map.of("exists", isInCart));
    }

    @PostMapping("/updateCart")
    public ResponseEntity<?> updateCart(HttpSession session,
                                        @RequestParam String productId,
                                        @RequestParam int quantity) {
        try {
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("{\"success\": false, \"message\": \"User not logged in\"}");
            }

            String userIdStr = String.valueOf(userId);
            CartItem cartItem = cartService.getCartItem(productId, userIdStr);

            if (cartItem != null) {
                if (quantity <= 0) {
                    cartService.removeFromCart(productId, userIdStr); // Remove item if quantity is zero or less
                } else {
                    cartItem.setQuantity(quantity);
                    cartService.updateCartItem(cartItem);
                }
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"success\": false, \"message\": \"Item not found in cart\"}");
            }

            return ResponseEntity.ok("{\"success\": true, \"message\": \"Cart updated successfully\"}");
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"success\": false, \"message\": \"An unexpected error occurred. Please try again.\"}");
        }
    }


    @DeleteMapping("/removeFromCart")
    public ResponseEntity<String> removeFromCart(HttpSession session, @RequestParam String productId) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        String userIdStr = String.valueOf(userId);
        boolean removed = cartService.removeFromCart(productId, userIdStr);

        if (removed) {
            return ResponseEntity.ok("Product removed from cart");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found in cart");
        }
    }


    @DeleteMapping("/removeAllFromCart")
    public ResponseEntity<String> removeAllFromCart(HttpSession session, @RequestParam String productId) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            System.out.println("User not logged in");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");

        }

        String userIdStr = String.valueOf(userId);
        boolean removed = cartService.removeFromCart(productId, userIdStr);

        if (removed) {
            System.out.println( productId+ "cart items removed successfully");
        } else {
            System.out.println("No items found in the cart for the user");
        }
        return null;
    }



    @PostMapping("/checkout")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkout(@RequestBody Map<String, Object> request, HttpSession session) {
        List<Map<String, Object>> cartItems = (List<Map<String, Object>>) request.get("cartItems");


        Integer userId = (Integer) session.getAttribute("userId");
        String billId = UUID.randomUUID().toString(); // Generate a unique bill ID
        double totalAmount = 0.0;

        // Save each cart item to the database
        StringBuilder orderDetails = new StringBuilder();
        orderDetails.append("Order Details:\n\n");

        for (Map<String, Object> item : cartItems) {
            Bill bill = new Bill();
            bill.setBillId(billId); // Set the same billId for each item
            bill.setProductId((String) item.get("productId"));
            bill.setUserId(userId);
            bill.setProductName((String) item.get("productName"));
            bill.setStatus("Pending");

            double price = ((Number) item.get("price")).doubleValue();
            bill.setPrice(price);

            int quantity = ((Number) item.get("quantity")).intValue();
            bill.setQuantity(quantity);

            totalAmount += price * quantity;

            billRepository.save(bill);

            // Append item details to the email body
            orderDetails.append(String.format("Product: %s\nQuantity: %d\nPrice: %.2f\n\n",
                    bill.getProductName(), quantity, price));
        }

        // Append total amount to the email body
        orderDetails.append(String.format("Total Amount: %.2f", totalAmount));

        // Retrieve user email from the database
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "User not found.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Send confirmation email
        String userEmail = user.getEmail();
        String emailSubject = "Order Confirmation";
        restaurantService.sendEmailWithCode(userEmail, emailSubject, orderDetails.toString());

        // Create payment record with the same billId
        Payment payment = new Payment();
        payment.setUserId(userId);
        payment.setDescription(billId); // Use the same billId
        payment.setAmount(BigDecimal.valueOf(totalAmount));
        payment.setType("Food-Order");
        payment.setDate(LocalDate.now());
        payment.setStatus("New");
        restaurantService.savePayment(payment);

        // Return success response
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Payment processed and cart items saved successfully.");

        return ResponseEntity.ok(response);
    }

}


