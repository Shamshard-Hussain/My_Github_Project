package com.restaurant.Restaurant.Controller;

import com.restaurant.Restaurant.Model.User;
import com.restaurant.Restaurant.Service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("user")
public class MainController {

    @Autowired
    private RestaurantService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User addUser(@RequestBody User user) {
        return service.addUser(user);
    }

    @GetMapping
    public List<User> getUser() {
        return service.findAllUsers();
    }

    @GetMapping("/{userId}")
    public User getId(@PathVariable int userId) {
        return service.getUserByUserId(userId);
    }


    @PutMapping
    public User updateUser(@RequestBody User user) {
        return service.updateUser(user);
    }

    @DeleteMapping("/{userId}")
    public String deleteUser(@PathVariable Integer userId) {
        return service.deleteUser(userId);
    }
}
