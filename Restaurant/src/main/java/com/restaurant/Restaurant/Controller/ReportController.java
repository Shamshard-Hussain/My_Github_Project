package com.restaurant.Restaurant.Controller;

import com.restaurant.Restaurant.Model.Payment;
import com.restaurant.Restaurant.Model.Product;
import com.restaurant.Restaurant.Model.Reservation;
import com.restaurant.Restaurant.Repository.ProductRepository;
import com.restaurant.Restaurant.Repository.ReservationRepository;
import com.restaurant.Restaurant.Service.PaymentService;
import com.restaurant.Restaurant.Service.ProductService;
import com.restaurant.Restaurant.Service.RestaurantService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class ReportController {

    @Autowired
    private ProductService productService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ReservationRepository reservationRepository;

    @GetMapping("/adminReport")
    public String report(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId"); // Ensure this is Integer
        String userType = (String) session.getAttribute("role");

        // Check if userType is neither Admin nor Staff
        if (userType == null || (!userType.equals("Admin") && !userType.equals("Staff"))) {
            model.addAttribute("accessDenied", true);
            return "redirect:/admin/adminHome"; // Redirect to log in if session is not set
        }

        if (userId != null) {
            model.addAttribute("userId", userId);

            return "admin/adminReport";
        } else {
            return "redirect:/login"; // Redirect to log in if session is not set
        }
    }

    @PostMapping("/filterReservations")
    public ResponseEntity<List<Reservation>> filterReservations(@RequestBody Map<String, String> filters) {
        String dateFrom = filters.get("dateFrom");
        String dateTo = filters.get("dateTo");

        System.out.println("Filtering Reservations from " + dateFrom + " to " + dateTo);

        List<Reservation> filteredReservations = reservationRepository.findByDateBetween(dateFrom, dateTo);

        return ResponseEntity.ok(filteredReservations);
    }

    @PostMapping("/downloadReservationsExcel")
    public ResponseEntity<byte[]> downloadFilteredReservationsExcel(@RequestBody Map<String, String> filters) throws IOException {
        String dateFrom = filters.get("dateFrom");
        String dateTo = filters.get("dateTo");

        // Fetching filtered reservations
        List<Reservation> filteredReservations = reservationRepository.findByDateBetween(dateFrom, dateTo);

        // Log the size of the filtered reservations
        System.out.println("Number of reservations fetched: " + filteredReservations.size());

        // Check if any data is fetched
        for (Reservation reservation : filteredReservations) {
            System.out.println(reservation.toString());  // Log the data
        }

        // Proceed with generating Excel if data exists
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Reservations");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Date");
            headerRow.createCell(2).setCellValue("Time");
            headerRow.createCell(3).setCellValue("Name");
            headerRow.createCell(4).setCellValue("Phone");
            headerRow.createCell(5).setCellValue("Persons");
            headerRow.createCell(6).setCellValue("Status");

            // Create data rows
            int rowIdx = 1;
            for (Reservation reservation : filteredReservations) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(reservation.getId());
                row.createCell(1).setCellValue(reservation.getDate());
                row.createCell(2).setCellValue(reservation.getTime());
                row.createCell(3).setCellValue(reservation.getName());
                row.createCell(4).setCellValue(reservation.getPhone());
                row.createCell(5).setCellValue(reservation.getPersons());
                row.createCell(6).setCellValue(reservation.getStatus());
            }

            workbook.write(outputStream);
        }

        byte[] excelFile = outputStream.toByteArray();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "reservations.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelFile);
    }


    @PostMapping("/downloadProductsExcel")
    public ResponseEntity<byte[]> downloadFilteredProductsExcel(@RequestBody Map<String, String> filters) throws IOException {
        String category = filters.get("category");

        List<Product> filteredProducts;

        if ("All".equals(category)) {
            // Fetch all products if category is 'All'
            filteredProducts = productService.getAllProducts();
        } else {
            // Fetch products by category
            filteredProducts = productService.getProductsByCategory(category);
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Products");

            // Create header row
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Product ID");
            headerRow.createCell(1).setCellValue("Product Name");
            headerRow.createCell(2).setCellValue("Category");
            headerRow.createCell(3).setCellValue("Price");

            // Create data rows
            int rowIdx = 1;
            for (Product product : filteredProducts) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(product.getId());
                row.createCell(1).setCellValue(product.getProductName());
                row.createCell(2).setCellValue(product.getCategory());
                row.createCell(3).setCellValue(product.getPrice());
            }

            workbook.write(outputStream);
        }

        byte[] excelFile = outputStream.toByteArray();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "products.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .body(excelFile);
    }


    @PostMapping("/filterProducts")
    public ResponseEntity<?> filterByCategory(@RequestBody Map<String, String> filters) {
        try {
            // Extract category from the filters map
            String category = filters.get("category");

            // Check if the category is null or empty
            if (category == null || category.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Category must not be null or empty");
            }

            List<Product> products;
            if ("All".equals(category)) {
                // Fetch all products if category is 'All'
                products = productService.getAllProductsReport();
            } else {
                // Fetch products by category
                products = productService.getProductsByCategory(category);
            }

            // Check if any products were found
            if (products.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No products found for the selected category");
            }

            // Return the list of products
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            // Log the error for debugging purposes
            e.printStackTrace();

            // Return a generic error message
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No products found for the selected category");
        }
    }



    @PostMapping("/filterPayments")
    public ResponseEntity<List<Payment>> filterPayments(@RequestBody Map<String, String> filters) {
        try {
            // Validate if dateFrom and dateTo are present
            if (!filters.containsKey("dateFrom") || !filters.containsKey("dateTo")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            // Parse dates with proper error handling
            LocalDate dateFrom;
            LocalDate dateTo;
            try {
                dateFrom = LocalDate.parse(filters.get("dateFrom"));
                dateTo = LocalDate.parse(filters.get("dateTo"));
            } catch (DateTimeParseException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            List<Payment> payments = paymentService.getPaymentsByDateRange(dateFrom, dateTo);


            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/downloadPaymentsExcel")
    public ResponseEntity<byte[]> downloadPaymentsExcel(@RequestBody Map<String, String> filters) throws IOException {
        try {
            String type = filters.get("type");
            String dateFromStr = filters.get("dateFrom");
            String dateToStr = filters.get("dateTo");
            

            if (type == null || dateFromStr == null || dateToStr == null) {
                return ResponseEntity.badRequest().body(null);
            }

            LocalDate dateFrom = LocalDate.parse(dateFromStr);
            LocalDate dateTo = LocalDate.parse(dateToStr);

            List<Payment> payments = paymentService.getPaymentsByTypeAndDateRange(type, dateFrom, dateTo);


            if (payments.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Payments");

                // Define styles
                CellStyle headerStyle = workbook.createCellStyle();
                headerStyle.setBorderBottom(BorderStyle.THIN);
                headerStyle.setBorderLeft(BorderStyle.THIN);
                headerStyle.setBorderRight(BorderStyle.THIN);
                headerStyle.setBorderTop(BorderStyle.THIN);

                CellStyle borderStyle = workbook.createCellStyle();
                borderStyle.setBorderBottom(BorderStyle.DOUBLE);
                borderStyle.setBorderTop(BorderStyle.THIN);

                CellStyle totalStyle = workbook.createCellStyle();
                totalStyle.setBorderTop(BorderStyle.THIN);
                totalStyle.setBorderBottom(BorderStyle.THICK);

                // Create header row
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("Payment ID");
                headerRow.createCell(1).setCellValue("User ID");
                headerRow.createCell(2).setCellValue("Type");
                headerRow.createCell(3).setCellValue("Bill Number");
                headerRow.createCell(4).setCellValue("Date");
                headerRow.createCell(5).setCellValue("Status");
                headerRow.createCell(6).setCellValue("Amount");
                // Apply header style
                for (Cell cell : headerRow) {
                    cell.setCellStyle(headerStyle);
                }

                // Create data rows and calculate total
                int rowIdx = 1;
                BigDecimal totalAmount = BigDecimal.ZERO;

                for (Payment payment : payments) {
                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(payment.getId());
                    row.createCell(1).setCellValue(payment.getUserId());
                    row.createCell(2).setCellValue(payment.getType());
                    row.createCell(3).setCellValue(payment.getDescription());
                    row.createCell(4).setCellValue(payment.getDate().toString());
                    row.createCell(5).setCellValue(payment.getStatus());
                    row.createCell(6).setCellValue(payment.getAmount().toString()+"0");

                    totalAmount = totalAmount.add(payment.getAmount());
                }

                // Create total row
                Row totalRow = sheet.createRow(rowIdx);
                totalRow.createCell(5).setCellValue("Total:");
                totalRow.createCell(6).setCellValue(totalAmount.toString()+"0");

                // Apply styles to total row
                for (Cell cell : totalRow) {
                    cell.setCellStyle(totalStyle);
                }

                // Apply border style to the last row
                for (Cell cell : totalRow) {
                    cell.setCellStyle(borderStyle);
                }

                workbook.write(outputStream);
            }

            byte[] excelFile = outputStream.toByteArray();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", "payments.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelFile);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }




}
