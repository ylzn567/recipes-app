package com.forallergans.recepies.dtos;

import lombok.Data;
import java.util.List;

import com.forallergans.recepies.entities.Difficulty;
import com.forallergans.recepies.entities.Kashrut;

@Data
public class RecipeDTO {
    private Long id;
    private String title;
    private String instructions;
    private int preparationTimeMinutes;
    private Difficulty difficulty;
    private Kashrut kashrut;
    private String createdByUsername;
    
private List<IngredientDTO> ingredients;
private List<String> allergens;
}