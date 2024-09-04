package com.restaurant.Restaurant.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {
    @Id
    private String id;
    private String date;
    private String hours;
    private String name;
    private String phone;
    private int persons;
    private String Status;
    private String userId;


}
