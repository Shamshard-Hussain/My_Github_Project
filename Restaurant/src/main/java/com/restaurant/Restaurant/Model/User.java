package com.restaurant.Restaurant.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "User")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private Integer userId;
    private String FirstName;
    private String LastName;
    private String password;
    private String role;
    private String email;
    private String phone;
    private String Date;


}