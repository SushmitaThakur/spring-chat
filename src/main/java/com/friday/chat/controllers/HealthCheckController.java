package com.friday.chat.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;

@RestController
public class HealthCheckController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/health")
    public String healthCheck() {
        // Check database connectivity
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(1)) { // Timeout after 1 second
                return "Application is healthy and database is connected";
            }
        } catch (Exception e) {
            return "Application is running, but database connectivity failed: " + e.getMessage();
        }

        return "Application is running, but database status is unknown";
    }
}
