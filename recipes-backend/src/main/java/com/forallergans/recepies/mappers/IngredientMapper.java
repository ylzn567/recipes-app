package com.forallergans.recepies.mappers;

import com.forallergans.recepies.dtos.IngredientDTO;
import com.forallergans.recepies.entities.RecipeIngredient;

public class IngredientMapper {

    public static IngredientDTO toDTO(RecipeIngredient ingredient) {
        if (ingredient == null) {
            return null;
        }

        IngredientDTO dto = new IngredientDTO();
        dto.setQuantity(ingredient.getQuantity());
        dto.setMeasurementUnit(ingredient.getMeasurementUnit());
        
        // משתמשים ב-ProductMapper כדי להמיר את המוצר הפנימי
        dto.setProduct(ProductMapper.toDTO(ingredient.getProduct()));

        return dto;
    }
}