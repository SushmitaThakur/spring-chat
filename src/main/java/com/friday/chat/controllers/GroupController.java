package com.friday.chat.controllers;

import com.friday.chat.models.Group;
import com.friday.chat.models.User;
import com.friday.chat.repositories.GroupRepository;
import com.friday.chat.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/group")
public class GroupController {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/list")
    private ResponseEntity<?> getAllGroups(@RequestParam(required = false) Long user_id) {
        if (user_id != null) {
            Optional<User> userOptional = userRepository.findById(user_id);
            if (userOptional.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Return 404 if user doesn't exist
            }

            User user = userOptional.get();
            return ResponseEntity.ok(new ArrayList<>(user.getGroups())); // Return groups the user is part of
        }
        return ResponseEntity.ok(groupRepository.findAll()); // Return all groups if no user_id is provided
    }

    // POST /group/create - Create a new group
    @PostMapping("/create")
    public ResponseEntity<Group> createGroup(@RequestParam String name) {
        if (name.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Invalid Request
        }
        Group group = new Group(name);
        group = groupRepository.save(group);
        return new ResponseEntity<>(group, HttpStatus.CREATED); // Return created group
    }

    // POST /group/join/{group_id}
    @PostMapping("/join/{group_id}")
    public ResponseEntity<String> joinGroup(@PathVariable Long group_id, @RequestParam Long user_id) {
        Optional<Group> groupOptional = groupRepository.findById(group_id);
        Optional<User> userOptional = userRepository.findById(user_id);

        if(groupOptional.isEmpty() || userOptional.isEmpty()) {
            return new ResponseEntity<>("Group or User not found", HttpStatus.NOT_FOUND);
        }

        Group group = groupOptional.get();
        User existingUser = userOptional.get();

        group.getUsers().add(existingUser); // Add user to group
        groupRepository.save(group); // Save updated group

        return new ResponseEntity<>("User added to group", HttpStatus.OK);
    }

}
