package com.swedenrosca.controller;

import com.swedenrosca.model.Role;
import com.swedenrosca.model.User;
import com.swedenrosca.repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;

public class UserController {

    private final UserRepository userRepository;

    public UserController( UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // 🔹 Create a new user
    public String createUser(String userName, String password, String email, String personalNumber, String firstName, String lastName, String mobileNumber, String bankAccount, String clearingNumber, BigDecimal monthlyContribution, int numberOfMembers, Role role) {
         User user=new User();
         user.setUsername(userName);
        if (userRepository.getByUsername(user.getUsername()) != null) {
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

        userRepository.save(user);
        return "✅ User created successfully.";
    }

    // 🔹 Find a user by username
    public User getUserByUsername(String username) {
        return userRepository.getByUsername(username);
    }

    // 🔹 List all users
    public List<User> getAllUsers() {
        return userRepository.getAll();
    }

    // 🔹 Delete a user by username
    public String deleteUser(String username) {
        User user = userRepository.getByUsername(username);
        if (user == null) {
            return "❌ User not found.";
        }
        userRepository.delete(user);
        return "✅ User deleted.";
    }

}

