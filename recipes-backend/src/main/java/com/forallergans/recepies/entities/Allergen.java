package com.forallergans.recepies.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "allergens")
public class Allergen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name; 
    
    private String description;
}

// https://gemini.google.com/share/57c57f73e6f7