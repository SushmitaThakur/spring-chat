package com.friday.chat.controllers;

import com.friday.chat.models.Group;
import com.friday.chat.models.Message;
import com.friday.chat.models.User;
import com.friday.chat.repositories.GroupRepository;
import com.friday.chat.repositories.MessageRepository;
import com.friday.chat.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    private Group group1;

    @BeforeEach
    public void setUp() {
        // Clean up repositories before each test
        messageRepository.deleteAll();
        groupRepository.deleteAll();
        userRepository.deleteAll();

        // Create and save users
        User user1 = userRepository.save(new User("John", "john@test.com"));
        User user2 = userRepository.save(new User("Jane", "jane@test.com"));

        // Create and save groups
         group1 = groupRepository.save(new Group("Group 1"));
        Group group2 = groupRepository.save(new Group("Group 2"));

        // Add messages to groups
        Message message1 = new Message();
        message1.setContent("Hello Group 1!");
        message1.setGroup(group1);
        message1.setUser(user1);

        Message message2 = new Message();
        message2.setContent("Hi Group 1!");
        message2.setGroup(group1);
        message2.setUser(user2);

        Message message3 = new Message();
        message3.setContent("Hello Group 2!");
        message3.setGroup(group2);
        message3.setUser(user1);

        messageRepository.saveAll(List.of(message1, message2, message3));
    }

    // Test: GET /message/list/{group_id} - List all messages in a group
    @Test
    public void testGetMessageByGroup() throws Exception {
        Long groupId = groupRepository.findAll().getFirst().getId(); // Get an existing group ID

        List<Message> messages = messageRepository.findByGroup(group1);
        System.out.println("Fetched Messages: " + messages);

        mockMvc.perform(get("/message/list/" + groupId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].messageContent", containsInAnyOrder(
                        "Hello Group 1!",
                        "Hi Group 1!"
                )));
    }

    // Test: GET /message/list/{group_id} - Group not found
    @Test
    public void testGetMessageByGroupNotFound() throws Exception {
        mockMvc.perform(get("/message/list/9999")) // Non-existing group ID
                .andExpect(status().isNotFound());
    }

    // Test: POST /message/send/{group_id} - Send a message successfully
    @Test
    public void testSendMessage() throws Exception {
        Long groupId = groupRepository.findAll().get(1).getId(); // Get Group 2 ID
        Long userId = userRepository.findAll().get(0).getId(); // Get User 1 ID

        mockMvc.perform(post("/message/send/" + groupId)
                        .param("user_id", String.valueOf(userId))
                        .param("message", "New message for Group 2"))
                .andExpect(status().isCreated())
                .andExpect(content().string("Message Sent."));
    }

    // Test: POST /message/send/{group_id} - Group or User not found
    @Test
    public void testSendMessageGroupOrUserNotFound() throws Exception {
        mockMvc.perform(post("/message/send/9999") // Non-existing group ID
                        .param("user_id", "9999") // Non-existing user ID
                        .param("message", "Message for non-existent entities"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Group or User not found"));
    }

    // Test: POST /message/send/{group_id} - Empty message
    @Test
    public void testSendMessageEmptyMessage() throws Exception {
        Long groupId = groupRepository.findAll().getFirst().getId(); // Get Group 1 ID
        Long userId = userRepository.findAll().getFirst().getId(); // Get User 1 ID

        mockMvc.perform(post("/message/send/" + groupId)
                        .param("user_id", String.valueOf(userId))
                        .param("message", "")) // Empty message
                .andExpect(status().isNotFound())
                .andExpect(content().string("Empty message."));
    }
}
