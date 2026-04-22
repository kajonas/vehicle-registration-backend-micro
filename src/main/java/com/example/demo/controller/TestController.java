package com.example.demo.controller;


import com.example.demo.model.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/user")
    public User test() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Smith");
        user.setEmail("john.smith@gmail.com");

        return user;
    }
}
