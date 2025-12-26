package com.teamsphere;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for TeamSphere.
 * TeamSphere is an employee and project management system.
 */
@SpringBootApplication
public class TeamSphereApplication {

    /**
     * Main method to start the Spring Boot application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(TeamSphereApplication.class, args);
    }

}
