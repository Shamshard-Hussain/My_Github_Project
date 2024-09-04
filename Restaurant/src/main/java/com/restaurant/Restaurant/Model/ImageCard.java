package com.restaurant.Restaurant.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "Images")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageCard {

        @Id
        private String id;
        private String imageId;

    }


