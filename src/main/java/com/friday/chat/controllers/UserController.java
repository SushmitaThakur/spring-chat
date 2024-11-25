package com.friday.chat.controllers;

import com.friday.chat.models.User;
import com.friday.chat.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // GET /user/list - List all users
    @GetMapping("/list")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // POST /user/create - Create a new user
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> createUser( @RequestParam String name, @RequestParam String email) {
        if (name == null || email == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Invalid request
        }

        User user = new User(name, email);
        user = userRepository.save(user); // Save the user to the repository

        return new ResponseEntity<>(user, HttpStatus.CREATED); // Return the created user with 201 status
    }
}
