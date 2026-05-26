package com.forallergans.recepies.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.forallergans.recepies.dtos.RecipeDTO;
import com.forallergans.recepies.entities.Difficulty;
import com.forallergans.recepies.entities.Kashrut;
import com.forallergans.recepies.entities.Recipe;
import com.forallergans.recepies.mappers.RecipeMapper;
import com.forallergans.recepies.services.RecipeService;


import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recipes")
@CrossOrigin(origins = "http://localhost:4200") // חיבור ישיר ל-Angular
public class RecipeController {

    private final RecipeService recipeService;

    @Autowired
    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    // 1. שליפת כל המתכונים כ-DTOs (פתוח לכולם - מוגדר ב-SecurityConfig)
    @GetMapping
    public ResponseEntity<List<RecipeDTO>> getAllRecipes() {
        List<RecipeDTO> recipes = recipeService.getAllRecipes().stream()
                .map(RecipeMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(recipes);
    }

    // 2. סינון לפי כשרות (פתוח לכולם)
    @GetMapping("/filter/kashrut")
    public ResponseEntity<List<RecipeDTO>> getRecipesByKashrut(@RequestParam Kashrut kashrut) {
        List<RecipeDTO> recipes = recipeService.getRecipesByKashrut(kashrut).stream()
                .map(RecipeMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(recipes);
    }

    // 3. סינון לפי רמת קושי (פתוח לכולם)
    @GetMapping("/filter/difficulty")
    public ResponseEntity<List<RecipeDTO>> getRecipesByDifficulty(@RequestParam Difficulty difficulty) {
        List<RecipeDTO> recipes = recipeService.getRecipesByDifficulty(difficulty).stream()
                .map(RecipeMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(recipes);
    }

    // 4. סינון מורכב: מתכונים ללא אלרגן מסוים (פתוח לכולם)
    @GetMapping("/filter/without-allergen")
    public ResponseEntity<List<RecipeDTO>> getRecipesWithoutAllergen(@RequestParam String allergenName) {
        List<RecipeDTO> recipes = recipeService.getRecipesWithoutAllergen(allergenName).stream()
                .map(RecipeMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(recipes);
    }

    // 5. יצירת מתכון חדש (דורש משתמש מחובר)
    @PostMapping
    public ResponseEntity<RecipeDTO> createRecipe(@RequestBody Recipe recipe, Principal principal) {
        // Spring Security מזריק אוטומטית ל-principal את שם המשתמש שחולץ מה-JWT
        String loggedInUsername = principal.getName();

        Recipe createdRecipe = recipeService.createRecipe(recipe, loggedInUsername);
        return new ResponseEntity<>(RecipeMapper.toDTO(createdRecipe), HttpStatus.CREATED);
    }

    // 6. עדכון מתכון קיים (דורש משתמש מחובר + בדיקת בעלות בתוך ה-Service)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateRecipe(
            @PathVariable Long id, 
            @RequestBody Recipe recipeData, 
            Principal principal) {
        try {
            String loggedInUsername = principal.getName();
            Recipe updatedRecipe = recipeService.updateRecipe(id, recipeData, loggedInUsername);
            return ResponseEntity.ok(RecipeMapper.toDTO(updatedRecipe));
        } catch (SecurityException e) {
            // אם המשתמש מנסה לערוך מתכון שלא שלו, מוחזר 403 Forbidden
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            // אם המתכון לא נמצא ב-DB
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // 7. מחיקת מתכון (דורש משתמש מחובר + בדיקת הרשאות בתוך ה-Service)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecipe(@PathVariable Long id, Principal principal) {
        try {
            String loggedInUsername = principal.getName();
            recipeService.deleteRecipe(id, loggedInUsername);
            return ResponseEntity.ok("המתכון נמחק בהצלחה");
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}