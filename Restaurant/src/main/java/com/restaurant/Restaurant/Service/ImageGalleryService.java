package com.restaurant.Restaurant.Service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.restaurant.Restaurant.Model.HomeImageGallery;
import com.restaurant.Restaurant.Model.ImageCard;
import org.apache.commons.io.IOUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.UUID;

import static org.springframework.data.mongodb.core.query.Criteria.where;


@Service
public class ImageGalleryService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    public void saveImage(String imageId, MultipartFile image) throws IOException {
        // Check if the imageId already exists in the database
        Query query = new Query(Criteria.where("id").is(imageId));
        boolean exists = mongoTemplate.exists(query, ImageCard.class);

        if (exists) {
            throw new RuntimeException("Image ID already exists in the database");
        }

        // Get the root directory of the application
        String uploadDirectory = new File("src/main/resources/static/assets/").getAbsolutePath();

        // Ensure the directory exists
        File uploadDir = new File(uploadDirectory);
        if (!uploadDir.exists()) {
            boolean dirsCreated = uploadDir.mkdirs();
            System.out.println("Created directories: " + dirsCreated);
        }

        // Save the image file to the static/assets directory
        String imagePath = uploadDirectory + File.separator + image.getOriginalFilename();
        File dest = new File(imagePath);
        try {
            image.transferTo(dest);
            System.out.println("Image saved to directory: " + imagePath);
        } catch (IOException e) {
            System.err.println("Failed to save the image file to the specified directory: " + e.getMessage());
            throw new IOException("Failed to save the image file to the specified directory", e);
        }

        // Save image details to MongoDB
        ImageCard imageCard = new ImageCard();
        imageCard.setId(imageId);
        imageCard.setImageId(image.getOriginalFilename());
        mongoTemplate.save(imageCard);
        System.out.println("Image details saved to database with ID: " + imageId);
    }





    public void deleteLocationById(String id) {
        // Find the location by ID
        ImageCard location = mongoTemplate.findById(id, ImageCard.class);
        if (location != null) {
            // Delete the location from the database
            mongoTemplate.remove(location);

            // Construct the path to the image file
            String imagePath = "src/main/resources/static/assets/" + location.getImageId() + ".jpg";
            File imageFile = new File(imagePath);

            // Delete the image file if it exists
            if (imageFile.exists()) {
                if (imageFile.delete()) {
                    System.out.println("Image file deleted successfully: " + imageFile.getAbsolutePath());
                } else {
                    System.out.println("Failed to delete image file: " + imageFile.getAbsolutePath());
                }
            }
        } else {
            System.out.println("Location not found with id: " + id);
        }
    }

    public List<ImageCard> getAllImageCards() {
        return mongoTemplate.findAll(ImageCard.class);
    }









    public void addImageGallery(String headingName, String description, MultipartFile image) throws IOException {
        // Generate a unique ID for the gallery entry
        String uniqueId = UUID.randomUUID().toString();

        // Save image to GridFS
        DBObject metaData = new BasicDBObject();
        metaData.put("type", "image");
        ObjectId imageId = gridFsTemplate.store(image.getInputStream(), image.getOriginalFilename(), image.getContentType(), metaData);

        // Save ImageGallery document
        HomeImageGallery imageGallery = new HomeImageGallery();
        imageGallery.setId(uniqueId); // Set the generated ID
        imageGallery.setHeadingName(headingName);
        imageGallery.setDescription(description);
        imageGallery.setImageId(imageId.toString());
        mongoTemplate.save(imageGallery);
    }

    public List<HomeImageGallery> getAllImage() {
        // Create a new Query object
        Query query = new Query();
        // Sort by '_id' field in descending order
        query.with(Sort.by(Sort.Direction.DESC, "_id"));
        // Execute the query and return the results
        return mongoTemplate.find(query, HomeImageGallery.class);
    }


    public List<HomeImageGallery> getAllData() {
        return mongoTemplate.findAll(HomeImageGallery.class);
    }








    public void updateImageGallery(String id, String headingName, String description, MultipartFile image) throws IOException {
        // Find the existing gallery document
        HomeImageGallery existingGallery = mongoTemplate.findById(id, HomeImageGallery.class);

        if (existingGallery != null) {
            // Update heading and description
            existingGallery.setHeadingName(headingName);
            existingGallery.setDescription(description);

            // If a new image is provided, save it and update the imageId
            if (image != null && !image.isEmpty()) {
                // Save new image to GridFS
                GridFSBucket gridFSBucket = GridFSBuckets.create(mongoTemplate.getDb());
                InputStream inputStream = image.getInputStream();
                GridFSUploadOptions options = new GridFSUploadOptions()
                        .metadata(new Document("type", "image"));
                ObjectId newImageId = gridFSBucket.uploadFromStream(image.getOriginalFilename(), inputStream, options);

                // Update the imageId in the gallery document
                existingGallery.setImageId(newImageId.toString());
            }

            // Save the updated gallery document
            mongoTemplate.save(existingGallery);
        } else {
            throw new IllegalArgumentException("Gallery with ID " + id + " not found.");
        }
    }

    public void deleteImageGallery(String id) {
        // Delete the image document from the ImageGallery collection
        mongoTemplate.remove(new Query(where("id").is(id)), HomeImageGallery.class);

    }


}