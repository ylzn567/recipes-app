package com.forallergans.recepies.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username; // שם משתמש ייחודי להתחברות

    @Column(nullable = false)
    private String password; // סיסמה (מוצפנת, בהמשך עם Spring Security)

    @Column(unique = true, nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // תפקיד המשתמש (USER או ADMIN)

    // קשר אופציונלי: מאפשר לראות את כל המתכונים שהמשתמש הזה יצר
    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
    private List<Recipe> recipes;
}