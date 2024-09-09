package com.restaurant.Restaurant.Service;


import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.restaurant.Restaurant.Model.HomeImageGallery;
import com.restaurant.Restaurant.Model.Product;
import com.restaurant.Restaurant.Repository.ProductRepository;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.bson.types.ObjectId;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.core.query.Query;
@Service
public class ProductService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ProductRepository productRepository;

    public void addProduct(String productName, double price, String category, String description, MultipartFile image) throws IOException {
        System.out.println("Adding product: " + productName);

        Product product = new Product();
        product.setProductName(productName);
        product.setPrice(price);
        product.setCategory(category);
        product.setDescription(description);

        // Save the product without the image
        mongoTemplate.save(product);
        //System.out.println("Product saved: " + product);

        // Save the image in GridFS
        GridFSBucket gridFSBucket = GridFSBuckets.create(mongoTemplate.getDb());
        InputStream inputStream = image.getInputStream();
        GridFSUploadOptions options = new GridFSUploadOptions().metadata(new Document("type", "image"));
        ObjectId fileId = gridFSBucket.uploadFromStream(image.getOriginalFilename(), inputStream, options);

        // Set the file ID in the product and update the product
        product.setImageId(fileId.toString());
        mongoTemplate.save(product);
        System.out.println("Product updated with image ID: " + fileId);
    }


    public Optional<byte[]> getImage(String id) {
        GridFSBucket gridFSBucket = GridFSBuckets.create(mongoTemplate.getDb());
        ObjectId fileId = new ObjectId(id);

        try (var downloadStream = gridFSBucket.openDownloadStream(fileId)) {
            return Optional.of(downloadStream.readAllBytes());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public Optional<Product> getProductById(String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, Product.class));
    }

    public void deleteProduct(String id) {
        mongoTemplate.remove(mongoTemplate.findById(id, Product.class));
    }
    public void updateProduct(String id, String productName, double price, String category, String description, MultipartFile image) throws IOException {
        Optional<Product> optionalProduct = Optional.ofNullable(mongoTemplate.findById(id, Product.class));

        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setProductName(productName);
            product.setPrice(price);
            product.setCategory(category);
            product.setDescription(description);

            if (image != null && !image.isEmpty()) {
                // Save new image and update the imageId
                GridFSBucket gridFSBucket = GridFSBuckets.create(mongoTemplate.getDb());
                InputStream inputStream = image.getInputStream();
                GridFSUploadOptions options = new GridFSUploadOptions()
                        .metadata(new Document("type", "image"));
                ObjectId fileId = gridFSBucket.uploadFromStream(image.getOriginalFilename(), inputStream, options);
                product.setImageId(fileId.toString());
            }

            // Save the updated product
            mongoTemplate.save(product);
        }
    }

    public List<Product> getAllProducts() {
        // Create a new Query object
        Query query = new Query();
        // Sort by '_id' field in descending order
        query.with(Sort.by(Sort.Direction.DESC, "_id"));
        // Execute the query and return the results
        return mongoTemplate.find(query, Product.class);
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> getAllProductsReport() {
        return productRepository.findAll();
    }
}


