package com.friday.chat.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "chat_users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

//    @ManyToMany(mappedBy = "users", cascade = CascadeType.ALL)
    @ManyToMany(mappedBy = "users")
    @JsonIgnore  // Ignore groups for serialization
    private Set<Group> groups = new HashSet<>();

    public User() {}

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getters and Setters
    public Long getId() {
        return  this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return  this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return  this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }
}
