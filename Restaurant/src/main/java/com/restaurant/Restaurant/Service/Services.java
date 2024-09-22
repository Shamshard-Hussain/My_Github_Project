package com.restaurant.Restaurant.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
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
import java.util.UUID;

@Service
public class Services {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    public void addServicesGallery(String headingName, String description, MultipartFile image) throws IOException {
        // Generate a unique ID for the gallery entry
        String uniqueId = UUID.randomUUID().toString();

        // Save image to GridFS
        DBObject metaData = new BasicDBObject();
        metaData.put("type", "image");
        ObjectId imageId = gridFsTemplate.store(image.getInputStream(), image.getOriginalFilename(), image.getContentType(), metaData);

        // Save ImageGallery document
        ServicesClass imageGallery = new ServicesClass();
        imageGallery.setId(uniqueId); // Set the generated ID
        imageGallery.setHeadingName(headingName);
        imageGallery.setDescription(description);
        imageGallery.setImageId(imageId.toString());
        mongoTemplate.save(imageGallery);
    }


    public List<ServicesClass> getAllImage() {
        // Create a new Query object
        Query query = new Query();
        // Sort by '_id' field in descending order
        query.with(Sort.by(Sort.Direction.DESC, "_id"));
        // Execute the query and return the results
        return mongoTemplate.find(query, ServicesClass.class);
    }

    public void updateServicesGallery(String id, String headingName, String description, MultipartFile image) throws IOException {
        // Find the existing gallery document
        ServicesClass existingGallery = mongoTemplate.findById(id, ServicesClass.class);

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
            throw new IllegalArgumentException("Services Gallery with ID " + id + " not found.");
        }
    }

    public void deleteServicesGallery(String id) {
        // Delete the image document from the ImageGallery collection
        mongoTemplate.remove(new Query(Criteria.where("id").is(id)), ServicesClass.class);

    }
}
