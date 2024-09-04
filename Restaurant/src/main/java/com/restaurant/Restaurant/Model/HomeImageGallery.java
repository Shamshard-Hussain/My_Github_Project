package com.restaurant.Restaurant.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ImageGallery")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeImageGallery {

        @Id
        private String id;
        private String headingName;
        private String description;
        private String imageId;


}
