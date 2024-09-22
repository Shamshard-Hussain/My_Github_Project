package com.restaurant.Restaurant.Controller;

import com.restaurant.Restaurant.Model.ContactMessage;
import com.restaurant.Restaurant.Service.RestaurantService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class Inquiry {

    @Autowired
    private RestaurantService Service;

    @Autowired
    private JavaMailSender emailSender;

    @GetMapping("Inquiry")
    public String getInquiries(HttpServletRequest request,Model model) {

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
            List<ContactMessage> contactMessages = Service.getAllContactMessages();
            // Sort messages by date
            contactMessages.sort(Comparator.comparing(ContactMessage::getDate).reversed());
            model.addAttribute("contactMessages", contactMessages);
            return "admin/Inquiry";
        } else {
            return "redirect:/login"; // Redirect to log in if session is not set
        }
    }

    @PostMapping("/updateStatus")
    public String updateStatus(@RequestParam("id") int id, @RequestParam("status") String status) {
        Service.updateContactMessageStatus(id, status);
        return "Status updated successfully";
    }




    @PostMapping("/reply/{messageId}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> sendReply(
            @PathVariable int messageId,
            @RequestBody Map<String, String> requestBody) {

        String replyMessage = requestBody.get("replyMessage");
        String recipientEmail = requestBody.get("recipientEmail");

        try {
            // Send the email
            sendEmail(recipientEmail, "Re: Your Inquiry @ ABC Restaurant", replyMessage);

            // Update the contact message status to "Replied"
            Service.updateContactMessageStatus(messageId, "Replied");

            return ResponseEntity.ok(Map.of("message", "Reply sent successfully!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to send reply. Please try again later."));
        }
    }
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }




}
