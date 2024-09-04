package com.restaurant.Restaurant.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Locations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Locations {

    @Id
    private String id;
    private String City;
    private String Address;
    private String Phone;
    private String Email;
    private String imageId;
}
