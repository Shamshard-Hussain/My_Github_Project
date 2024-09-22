package com.restaurant.Restaurant.Controller;


import com.restaurant.Restaurant.Model.ImageCard;

import com.restaurant.Restaurant.Service.ImageGalleryService;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/images")
public class userController {

    @Autowired
    private ImageGalleryService imageGalleryService;


    @GetMapping
    public List<String> getImageFilenames() {
        List<ImageCard> imageCards = imageGalleryService.getAllImageCards();
        return imageCards.stream()
                .map(ImageCard::getImageId) // Assuming imageId is the filename or ID
                .collect(Collectors.toList());
    }

}
