// package com.forallergans.recepies;

// import org.springframework.boot.SpringApplication;
// import org.springframework.boot.autoconfigure.SpringBootApplication;

// @SpringBootApplication
// public class RecepiesApplication {
//     public static void main(String[] args) {
//         SpringApplication.run(RecepiesApplication.class, args);
//     }
// }


// //http://localhost:8080/api/recipes

// //http://localhost:8080/h2-console

// //http://localhost:8080/api/swagger-ui.html

package com.forallergans.recepies;

import com.forallergans.recepies.entities.Role;
import com.forallergans.recepies.entities.User;
import com.forallergans.recepies.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class RecepiesApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecepiesApplication.class, args);
    }

    /**
     * קוד זה ירוץ פעם אחת באופן אוטומטי מיד לאחר ש-Spring Boot מסיים לעלות.
     * הוא יבדוק האם קיים משתמש ADMIN במסד הנתונים H2, ואם לא - ייצור אותו במקום.
     */
    @Bean
    public CommandLineRunner initAdminUser(UserRepository userRepository) {
        return args -> {
            String adminUsername = "admin";
            String adminEmail = "admin@recipes.com";

            // בדיקה האם המנהל כבר קיים במערכת כדי למנוע כפילויות
            if (!userRepository.existsByUsername(adminUsername) && !userRepository.existsByEmail(adminEmail)) {
                
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
                
                User admin = new User();
                admin.setUsername(adminUsername);
                admin.setEmail(adminEmail);
                
                // הגדרת סיסמה מאובטחת והצפנתה בעזרת ה-encoder
                String rawPassword = "Admin123!";
                admin.setPassword(encoder.encode(rawPassword));
                
                // הגדרת התפקיד כמנהל באופן ידני ומאובטח בקוד השרת
                admin.setRole(Role.ADMIN);
                
                userRepository.save(admin);
                
                System.out.println("====================================================");
                System.out.println("🚀 משתמש ADMIN נוצר אוטומטית במסד הנתונים H2!");
                System.out.println("👤 שם משתמש: " + adminUsername);
                System.out.println("🔑 סיסמה גלויה (לפני הצפנה): " + rawPassword);
                System.out.println("====================================================");
            } else {
                System.out.println("ℹ️ משתמש ADMIN כבר קיים במערכת, אין צורך ביצירה מחדש.");
            }
        };
    }
}