package com.forallergans.recepies.mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.forallergans.recepies.dtos.IngredientDTO;
import com.forallergans.recepies.dtos.RecipeDTO;
import com.forallergans.recepies.entities.Recipe;

public class RecipeMapper {

    public static RecipeDTO toDTO(Recipe recipe) {
        if (recipe == null) {
            return null;
        }

        RecipeDTO dto = new RecipeDTO();
        dto.setId(recipe.getId());
        dto.setTitle(recipe.getTitle());
        dto.setInstructions(recipe.getInstructions());
        dto.setPreparationTimeMinutes(recipe.getPreparationTimeMinutes());
        dto.setDifficulty(recipe.getDifficulty());
        dto.setKashrut(recipe.getKashrut());
        
        // חילוץ שם המשתמש בצורה בטוחה כדי למנוע קריסה אם אין יוצר
        if (recipe.getCreatedBy() != null) {
            dto.setCreatedByUsername(recipe.getCreatedBy().getUsername());
        }

        // 1. המרת רשימת הרכיבים המלאה ל-DTOs
        List<IngredientDTO> ingredientDTOs = new ArrayList<>();
        if (recipe.getIngredients() != null) {
            ingredientDTOs = recipe.getIngredients().stream()
                    .map(IngredientMapper::toDTO)
                    .collect(Collectors.toList());
        }
        dto.setIngredients(ingredientDTOs);

        // 2. ריכוז ואיסוף של כל האלרגנים מכל הרכיבים במתכון (ללא כפילויות!)
        List<String> allAllergens = ingredientDTOs.stream()
                .filter(ing -> ing.getProduct() != null && ing.getProduct().getAllergens() != null)
                .flatMap(ing -> ing.getProduct().getAllergens().stream())
                .distinct() // מונע מאותו אלרגן להופיע פעמיים בטקסט המרכזי
                .collect(Collectors.toList());
                
        dto.setAllergens(allAllergens);

        return dto;
    }
}