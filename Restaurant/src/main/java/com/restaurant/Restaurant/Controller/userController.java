package com.restaurant.Restaurant.Controller;

import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.restaurant.Restaurant.Model.HomeImageGallery;
import com.restaurant.Restaurant.Model.ImageCard;
import com.restaurant.Restaurant.Service.ImageGalleryService;
import jakarta.annotation.Resource;
import org.apache.tomcat.util.http.parser.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@RestController
@RequestMapping("/api/images")
public class userController {

    @Autowired
    private ImageGalleryService imageGalleryService;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @GetMapping
    public List<String> getImageFilenames() {
        List<ImageCard> imageCards = imageGalleryService.getAllImageCards();
        return imageCards.stream()
                .map(ImageCard::getImageId) // Assuming imageId is the filename or ID
                .collect(Collectors.toList());
    }

}
