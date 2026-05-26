package com.forallergans.recepies.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "recipes")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String instructions;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeIngredient> ingredients; 

    private Integer preparationTimeMinutes;

    @Enumerated(EnumType.STRING)
    private Difficulty difficulty;

    // הוספת מאפיין הכשרות מה-Enum
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Kashrut kashrut;


}