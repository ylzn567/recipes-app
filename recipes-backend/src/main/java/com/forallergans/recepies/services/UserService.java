package com.forallergans.recepies.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.forallergans.recepies.entities.Role;
import com.forallergans.recepies.entities.User;
import com.forallergans.recepies.repositories.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        // יוצרים מופע של המצפין ישירות
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // רישום משתמש חדש עם הצפנה מובנית מהרגע הראשון!
    public User registerUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("שם המשתמש כבר קיים במערכת");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("כתובת האימייל כבר קיימת במערכת");
        }
        
        // 1. לוקחים את הסיסמה הגלויה, מצפינים אותה ומחליפים את הישנה
        String securePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(securePassword);
        
        // 2. כברירת מחדל, משתמש חדש נרשם כמשתמש רגיל
        user.setRole(Role.USER);
        
        return userRepository.save(user);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> getAllUsers(User currentUser) {
        if (currentUser.getRole() != Role.ADMIN) {
            throw new SecurityException("רק מנהל מערכת רשאי לצפות ברשימת המשתמשים");
        }
        return userRepository.findAll();
    }
}