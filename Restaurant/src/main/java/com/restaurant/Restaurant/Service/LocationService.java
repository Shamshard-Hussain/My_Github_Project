package com.restaurant.Restaurant.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.restaurant.Restaurant.Model.HomeImageGallery;
import com.restaurant.Restaurant.Model.Locations;
import com.restaurant.Restaurant.Model.Product;
import com.restaurant.Restaurant.Model.ServicesClass;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.UUID;
@Service
public class LocationService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private GridFsTemplate gridFsTemplate;



    public void saveLocation(String id, String city, String address, String phone, String email, MultipartFile image) throws IOException {
        // Generate a unique ID for the location entry
        String uniqueId = UUID.randomUUID().toString();

        // Save image to GridFS
        DBObject metaData = new BasicDBObject();
        metaData.put("type", "image");
        ObjectId imageId = gridFsTemplate.store(image.getInputStream(), image.getOriginalFilename(), image.getContentType(), metaData);

        // Save Locations
        Locations location = new Locations();
        location.setId(uniqueId); // Set the generated ID
        location.setCity(city);
        location.setAddress(address);
        location.setPhone(phone);
        location.setEmail(email);
        location.setImageId(imageId.toString());
        mongoTemplate.save(location);
    }

    public List<Locations> getAllLocations() {
        // Create a new Query object
        Query query = new Query();
        // Sort by '_id' field in descending order
        query.with(Sort.by(Sort.Direction.DESC, "_id"));
        // Execute the query and return the results
        return mongoTemplate.find(query, Locations.class);
    }

    private static final Logger logger = LoggerFactory.getLogger(LocationService.class);

    public void deleteLocationById(String id) {
        Locations location = mongoTemplate.findById(id, Locations.class);
        if (location != null) {
            mongoTemplate.remove(location);
            logger.info("Location deleted: {}", id);
        } else {
            logger.error("Location not found with id: {}", id);
            throw new RuntimeException("Location not found with id: " + id);
        }
    }



    public void updateLocation(String id, String city, String address, String phone, String email, MultipartFile image) throws IOException {
        // Find the existing location entry
        Locations location = mongoTemplate.findById(id, Locations.class);

        if (location != null) {
            location.setCity(city);
            location.setAddress(address);
            location.setPhone(phone);
            location.setEmail(email);

            // If a new image is provided, save it and update the imageId
            if (image != null && !image.isEmpty()) {
                // Save new image to GridFS
                GridFSBucket gridFSBucket = GridFSBuckets.create(mongoTemplate.getDb());
                InputStream inputStream = image.getInputStream();
                GridFSUploadOptions options = new GridFSUploadOptions()
                        .metadata(new Document("type", "image"));
                ObjectId newImageId = gridFSBucket.uploadFromStream(image.getOriginalFilename(), inputStream, options);

                // Update the imageId in the location entry
                location.setImageId(newImageId.toString());
            }

            // Save the updated entry
            mongoTemplate.save(location);
        } else {
            throw new IllegalArgumentException("Location with ID " + id + " not found.");
        }
    }



}
