package com.forallergans.recepies.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.forallergans.recepies.entities.Difficulty;
import com.forallergans.recepies.entities.Kashrut;
import com.forallergans.recepies.entities.Recipe;
import com.forallergans.recepies.entities.Role;
import com.forallergans.recepies.entities.User;
import com.forallergans.recepies.repositories.RecipeRepository;
import com.forallergans.recepies.repositories.UserRepository;


@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    @Autowired
    public RecipeService(RecipeRepository recipeRepository , UserRepository userRepository) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
    }

    // ==========================================
    // חלק 1: פונקציות הסינון והשליפה
    // ==========================================

    // שליפת כל המתכונים ללא סינון
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    // סינון לפי כשרות
    public List<Recipe> getRecipesByKashrut(Kashrut kashrut) {
        return recipeRepository.findByKashrut(kashrut);
    }

    // סינון לפי רמת קושי
    public List<Recipe> getRecipesByDifficulty(Difficulty difficulty) {
        return recipeRepository.findByDifficulty(difficulty);
    }

    // הסינון המורכב: מתכונים ללא אלרגן מסוים
    public List<Recipe> getRecipesWithoutAllergen(String allergenName) {
        return recipeRepository.findRecipesWithoutAllergen(allergenName);
    }

    // ==========================================
    // חלק 2: יצירה ועדכון עם בדיקת הרשאות
    // ==========================================

    // יצירת מתכון חדש - משייכים אותו אוטומטית למשתמש שיוצר אותו
    public Recipe createRecipe(Recipe recipe, String username) {
       // שולפים את המשתמש מה-DB לפי ה-username שהגיע מהטוקן
       User currentUser = userRepository.findByUsername(username)
               .orElseThrow(() -> new RuntimeException("משתמש לא נמצא במערכת"));
       
       // משייכים את המשתמש שמצאנו למתכון החדש
       recipe.setCreatedBy(currentUser);
       
       // שומרים את המתכון בבסיס הנתונים
       return recipeRepository.save(recipe);
    }

    // עדכון מתכון - כאן מתבצעת בדיקת האבטחה שביקשתם!
    public Recipe updateRecipe(Long recipeId, Recipe updatedRecipeData, String username) {
        // 1. מחפשים את המתכון הקיים בבסיס הנתונים
        Recipe existingRecipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("המתכון המבוקש לא נמצא במערכת"));

        // 2. שולפים את המשתמש המחובר מה-DB לפי ה-username שהגיע מהטוקן
         User currentUser = userRepository.findByUsername(username) // או userService.getUserByUsername
            .orElseThrow(() -> new RuntimeException("משתמש לא נמצא במערכת"));
        // 2. בדיקת הרשאה: האם המשתמש הנוכחי הוא מנהל (ADMIN) או שהוא זה שכתב את המתכון?
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isOwner = existingRecipe.getCreatedBy().getId().equals(currentUser.getId());

        if (isAdmin || isOwner) {
            // אם יש הרשאה, מעדכנים את השדות הרלוונטיים
            existingRecipe.setTitle(updatedRecipeData.getTitle());
            existingRecipe.setInstructions(updatedRecipeData.getInstructions());
            existingRecipe.setPreparationTimeMinutes(updatedRecipeData.getPreparationTimeMinutes());
            existingRecipe.setDifficulty(updatedRecipeData.getDifficulty());
            existingRecipe.setKashrut(updatedRecipeData.getKashrut());
            existingRecipe.setIngredients(updatedRecipeData.getIngredients());

            // שומרים את השינויים ומחזירים את המתכון המעודכן
            return recipeRepository.save(existingRecipe);
        } else {
            // אם אין הרשאה, זורקים שגיאה והפעולה נחסמת
            throw new SecurityException("אין לך הרשאה לערוך מתכון זה. רק היוצר או מנהל יכולים לעדכן.");
        }
    }

    // מחיקת מתכון - גם כאן מומלץ לבדוק הרשאות באותו אופן
    public void deleteRecipe(Long recipeId, String username) {
        // 1. מחפשים את המתכון
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("המתכון המבוקש לא נמצא"));
    
        // 2. שולפים את המשתמש המחובר מה-DB
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("משתמש לא נמצא במערכת"));
    
        // 3. בדיקת הרשאה: רק מנהל או היוצר של המתכון יכולים למחוק אותו
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isOwner = recipe.getCreatedBy().getId().equals(currentUser.getId());
    
        if (isAdmin || isOwner) {
            recipeRepository.delete(recipe);
        } else {
            throw new SecurityException("אין לך הרשאה למחוק מתכון זה. רק היוצר או מנהל יכולים למחוק.");
        }
    }
}