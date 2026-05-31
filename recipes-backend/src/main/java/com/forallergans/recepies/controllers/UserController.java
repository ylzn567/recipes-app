package com.forallergans.recepies.controllers;

import com.forallergans.recepies.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.forallergans.recepies.dtos.UserDTO;
import com.forallergans.recepies.dtos.UserRegisterDTO;
import com.forallergans.recepies.entities.User;
import com.forallergans.recepies.services.UserService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    // מתקנים את הקונסטרקטור ומזריקים את ה-passwordEncoder בצורה מסודרת
    public UserController(UserService userService, JwtUtils jwtUtils, BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    // א. הרשמת משתמש חדש
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterDTO registerDTO) {
        try {
            User newUser = new User();
            newUser.setUsername(registerDTO.getUsername());
            newUser.setPassword(registerDTO.getPassword());
            newUser.setEmail(registerDTO.getEmail());

            User savedUser = userService.registerUser(newUser);

            UserDTO responseDTO = new UserDTO();
            responseDTO.setUsername(savedUser.getUsername());
            responseDTO.setEmail(savedUser.getEmail());
            responseDTO.setRole(savedUser.getRole());

            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // ב. התחברות (Login) והפקת טוקן JWT ל-Angular
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        // שולפים את המשתמש מה-DB לפי שם המשתמש
        User user = userService.getUserByUsername(username).orElse(null);

        // בודקים האם המשתמש קיים והאם הסיסמה המוצפנת ב-DB מתאימה
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("שם משתמש או סיסמה שגויים");
        }

        // מייצרים טוקן שמכיל את שם המשתמש והתפקיד שלו (USER/ADMIN)
        String token = jwtUtils.generateToken(user.getUsername(), user.getRole().name());

        // מחזירים ל-Angular אובייקט שמכיל את הטוקן ואת פרטי המשתמש הבסיסיים
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("username", user.getUsername());
        response.put("role", user.getRole());

        return ResponseEntity.ok(response);
    }
}