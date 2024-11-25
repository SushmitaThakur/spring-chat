package com.friday.chat.controllers;


import com.friday.chat.models.Group;
import com.friday.chat.models.User;
import com.friday.chat.repositories.GroupRepository;
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
public class GroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        // Clean up database before each test
        groupRepository.deleteAll();
        userRepository.deleteAll();

        // Create and save users
        User user1 = userRepository.save(new User("John", "john@test.com"));
        User user2 = userRepository.save(new User("Jane", "jane@test.com"));

        // Create groups
        Group group1 = new Group("Group 1");
        Group group2 = new Group("Group 2");

        // Associate users with groups
        group1.getUsers().add(user1);
        group2.getUsers().add(user2);

        // Save groups
        groupRepository.save(group1);
        groupRepository.save(group2);
    }

    // Test: GET /group/list - Retrieve all groups
    @Test
    public void testGetAllGroups() throws Exception {

        mockMvc.perform(get("/group/list"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder(
                        "Group 1",
                        "Group 2"
                )));
    }

    // Test: GET /group/list with user_id - Retrieve groups a user belongs to
    @Test
    public void testGetGroupsByUserId() throws Exception {
        User user = userRepository.findByEmail("john@test.com").orElseThrow();

        mockMvc.perform(get("/group/list")
                        .param("user_id", String.valueOf(user.getId())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Group 1")));
    }

    // Test: GET /group/list with invalid user_id - Return 404
    @Test
    public void testGetGroupsByNonExistentUserId() throws Exception {
        mockMvc.perform(get("/group/list")
                        .param("user_id", "999"))
                .andExpect(status().isNotFound());
    }

    // Test: POST /group/create - Create a new group with valid name
    @Test
    public void testCreateGroup() throws Exception {
        mockMvc.perform(post("/group/create")
                        .param("name", "New Group"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("New Group")));
    }

    // Test: POST /group/create - Fail to create group with empty name
    @Test
    public void testCreateGroupWithEmptyName() throws Exception {
        mockMvc.perform(post("/group/create")
                        .param("name", ""))
                .andExpect(status().isBadRequest());
    }

    // Test: POST /group/join/{group_id} - User joins a group successfully
    @Test
    public void testJoinGroup() throws Exception {
        User user = userRepository.save(new User("Alice", "alice@test.com"));
        Group group = groupRepository.findAll().get(0);

        mockMvc.perform(post("/group/join/" + group.getId())
                        .param("user_id", String.valueOf(user.getId())))
                .andExpect(status().isOk())
                .andExpect(content().string("User added to group"));
    }

    // Test: POST /group/join/{group_id} - Fail to join with invalid group ID
    @Test
    public void testJoinNonExistentGroup() throws Exception {
        User user = userRepository.findByEmail("john@test.com").orElseThrow();

        mockMvc.perform(post("/group/join/999")
                        .param("user_id", String.valueOf(user.getId())))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Group or User not found"));
    }

    // Test: POST /group/join/{group_id} - Fail to join with invalid user ID
    @Test
    public void testJoinGroupWithNonExistentUser() throws Exception {
        Group group = groupRepository.findAll().get(0);

        mockMvc.perform(post("/group/join/" + group.getId())
                        .param("user_id", "999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Group or User not found"));
    }
}

