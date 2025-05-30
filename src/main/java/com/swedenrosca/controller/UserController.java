package com.swedenrosca.controller;

import com.swedenrosca.model.*;
import com.swedenrosca.service.UserService;
import java.math.BigDecimal;
import java.util.List;

public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 🔹 Create a new user
    public String createUser(String userName, String password, String email, String personalNumber, String firstName, String lastName, String mobileNumber, String bankAccount, String clearingNumber, BigDecimal monthlyContribution, int numberOfMembers, Role role) {
        User user = new User();
        user.setUsername(userName);
        if (userService.getUserByUsername(user.getUsername()) != null) {
            return "❌ Username already exists.";
        }
        user.setPassword(password);
        user.setEmail(email);
        user.setPersonalNumber(personalNumber);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setMobileNumber(mobileNumber);
        user.setBankAccount(bankAccount);
        user.setClearingNumber(clearingNumber);
        user.setMonthlyContribution(monthlyContribution);
        user.setNumberOfMembers(numberOfMembers);
        user.setRole(role);

        userService.createUser(user);
        return "✅ User created successfully.";
    }

    // 🔹 Find a user by username
    public User getUserByUsername(String username) {
        return userService.getUserByUsername(username);
    }

    // 🔹 List all users
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // 🔹 Delete a user by username
    public String deleteUser(String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return "❌ User not found.";
        }
        userService.deleteUser(user);
        return "✅ User deleted.";
    }
}

