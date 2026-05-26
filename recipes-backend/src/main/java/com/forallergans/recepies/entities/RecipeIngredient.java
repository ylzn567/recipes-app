package com.forallergans.recepies.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "recipe_ingredients")
public class RecipeIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Double quantity; 

    @Enumerated(EnumType.STRING)
    @Column(name = "measurement_unit", nullable = false)
    private MeasurementUnit measurementUnit;
}