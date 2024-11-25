package com.friday.chat.controllers;

import com.friday.chat.dto.MessageResponseDTO;
import com.friday.chat.models.Message;
import com.friday.chat.models.Group;
import com.friday.chat.models.User;
import com.friday.chat.repositories.GroupRepository;
import com.friday.chat.repositories.MessageRepository;
import com.friday.chat.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/message")
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    // GET message/list/{group_id} - List all messages in a group
    @GetMapping("/list/{group_id}")
    public ResponseEntity<List<MessageResponseDTO>> getMessageByGroup(@PathVariable Long group_id) {
        Optional<Group> groupOptional = groupRepository.findById(group_id); // Return 404 if group doesn't exist

        if (groupOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Group group = groupOptional.get();
        List<Message> messages = messageRepository.findByGroup(group);

        // Convert messages to MessageResponseDTO
        // so that the response does not contain the group and user relations
        List<MessageResponseDTO> response = messages.stream()
                .map(message -> new MessageResponseDTO(group.getName(), message.getContent())) // Map to DTO
                .toList();

        System.out.println(messageRepository.findAll());
        return ResponseEntity.ok(response);
    }

    // POST message/send/{group_id} - Send a message in a group
    @PostMapping("/send/{group_id}")
    public ResponseEntity<String> sendMessage(
            @PathVariable Long group_id,
            @RequestParam Long user_id,
            @RequestParam String message
    ) {
        Optional<Group> groupOptional = groupRepository.findById(group_id);
        Optional<User> userOptional = userRepository.findById(user_id);

        if (groupOptional.isEmpty() || userOptional.isEmpty()) {
            return new ResponseEntity<>("Group or User not found", HttpStatus.NOT_FOUND);
        }

        if (message.isEmpty()) {
            return new ResponseEntity<>("Empty message.", HttpStatus.NOT_FOUND);
        }

        Group group = groupOptional.get();
        User user = userOptional.get();

        Message newMessage = new Message();
        newMessage.setGroup(group);
        newMessage.setUser(user);
        newMessage.setContent(message);

        // Save the message to the repository
        messageRepository.save(newMessage);

        return new ResponseEntity<>("Message Sent.", HttpStatus.CREATED); // Return success
    }
}
