package com.swedenrosca.controller;

import com.swedenrosca.model.Role;
import com.swedenrosca.model.User;
import com.swedenrosca.repository.UserRepository;

public class AuthController {
    private final UserRepository userRepository = new UserRepository();
    private User currentUser;

    public String login(String username, String password) {
        User user = userRepository.getByUsername(username);
        if (user != null && password.equals(user.getPassword())) {
            this.currentUser = user;
            return "Login successful!";
        }
        return "Invalid username or password.";
    }

    public String register(String username, String password, String personalNumber,
                           String firstName, String lastName, String mobileNumber) {
        if (userRepository.existsByUsername(username)) {
            return "Username already exists.";
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setPersonalNumber(personalNumber);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setMobileNumber(mobileNumber);
        newUser.setRole(Role.USER);

        userRepository.save(newUser);
        return "Registration successful!";
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isAuthenticated() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return isAuthenticated() && currentUser.getRole() == Role.ADMIN;
    }

    public boolean isUser() {
        return isAuthenticated() && currentUser.getRole() == Role.USER;
    }

    public boolean isCustomerService() {
        return isAuthenticated() && currentUser.getRole() == Role.CUSTOMER_SERVICE;
    }
}
