package com.forallergans.recepies.dtos;

import com.forallergans.recepies.entities.MeasurementUnit;

import lombok.Data;

@Data
public class IngredientDTO {
    private ProductDTO product;             // פרטי המוצר (שם ואלרגנים בלבד)
    private double quantity;                // הכמות המספרית
    private MeasurementUnit measurementUnit; // שימוש ב-Enum המקורי והמדויק שלכן!
}