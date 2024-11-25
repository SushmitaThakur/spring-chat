package com.friday.chat.controllers;

import com.friday.chat.models.User;
import com.friday.chat.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll(); // Clean up database before each test
        userRepository.save(new User("John", "john@example.com"));
        userRepository.save(new User("Jane", "jane@example.com"));
    }

    // Test: GET /user/list
    @Test
    public void testGetAllUsers() throws Exception {
        mockMvc.perform(get("/user/list"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2))) // Verify there are 2 users
                .andExpect(jsonPath("$[*].email", containsInAnyOrder(
                        "john@example.com",
                        "jane@example.com"
                )));
    }

    // Test: POST /user/create
    @Test
    public void testCreateUser() throws Exception {
        mockMvc.perform(multipart("/user/create")
                        .param("name", "Alice")
                        .param("email", "alice@example.com")
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));

        // Verify the user was saved to the database
        mockMvc.perform(get("/user/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3))) // Now there are 3 users
                .andExpect(jsonPath("$[*].email", containsInAnyOrder(
                        "john@example.com",
                        "jane@example.com",
                        "alice@example.com"
                )));
    }

    // Test: POST /user/create with missing parameters
    @Test
    public void testCreateUserMissingParameters() throws Exception {

        // Test user without email
        mockMvc.perform(multipart("/user/create")
                        .param("name", "Alice")
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isBadRequest());

        // Test user without name
        mockMvc.perform(multipart("/user/create")
                        .param("email", "alice@example.com")
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isBadRequest());
    }
}
