package com.forallergans.recepies;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.forallergans.recepies.entities.Recipe;
import com.forallergans.recepies.entities.User;
import com.forallergans.recepies.entities.Role;
import com.forallergans.recepies.repositories.RecipeRepository;
import com.forallergans.recepies.repositories.UserRepository;
import com.forallergans.recepies.services.RecipeService;
import com.forallergans.recepies.repositories.ProductRepository;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private RecipeService recipeService;

    private User adminUser;
    private User regularUserOwner;
    private User regularUserStranger;
    private Recipe sampleRecipe;

    @BeforeEach
    void setUp() {
        // הגדרת משתמש מנהל (ADMIN)
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("adminUser");
        adminUser.setRole(Role.ADMIN);

        // הגדרת משתמש רגיל שהוא בעל המקרקעין/המתכון (Owner)
        regularUserOwner = new User();
        regularUserOwner.setId(2L);
        regularUserOwner.setUsername("ownerUser");
        regularUserOwner.setRole(Role.USER); // או כל תפקיד שאינו אדמין

        // הגדרת משתמש רגיל אחר (Stranger)
        regularUserStranger = new User();
        regularUserStranger.setId(3L);
        regularUserStranger.setUsername("strangerUser");
        regularUserStranger.setRole(Role.USER);

        // הגדרת מתכון לדוגמה המשויך למשתמש ה-Owner
        sampleRecipe = new Recipe();
        sampleRecipe.setId(100L);
        sampleRecipe.setTitle("עוגת שוקולד");
        sampleRecipe.setCreatedBy(regularUserOwner);
    }

    // ========================================================
    // בדיקות עבור פונקציית עדכון מתכון (updateRecipe)
    // ========================================================

    @Test
    void updateRecipe_AsOwner_ShouldSuccess() {
        // Arrange - הכנת המוקים (Mock Behavior)
        String username = "ownerUser";
        Recipe updatedData = new Recipe();
        updatedData.setTitle("עוגת שוקולד משודרגת");

        when(recipeRepository.findById(100L)).thenReturn(Optional.of(sampleRecipe));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(regularUserOwner));
        when(recipeRepository.save(any(Recipe.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act - ביצוע הפעולה בפועל
        Recipe result = recipeService.updateRecipe(100L, updatedData, username);

        // Assert - וידוא שהתוצאה נכונה והערכים עודכנו
        assertNotNull(result);
        assertEquals("עוגת שוקולד משודרגת", result.getTitle());
        verify(recipeRepository, times(1)).save(sampleRecipe);
    }

    @Test
    void updateRecipe_AsAdmin_ShouldSuccess() {
        // Arrange
        String username = "adminUser";
        Recipe updatedData = new Recipe();
        updatedData.setTitle("עוגה שערך המנהל");

        when(recipeRepository.findById(100L)).thenReturn(Optional.of(sampleRecipe));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(adminUser));
        when(recipeRepository.save(any(Recipe.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Recipe result = recipeService.updateRecipe(100L, updatedData, username);

        // Assert
        assertNotNull(result);
        assertEquals("עוגה שערך המנהל", result.getTitle());
        verify(recipeRepository, times(1)).save(sampleRecipe);
    }

    @Test
    void updateRecipe_AsStranger_ShouldThrowSecurityException() {
        // Arrange
        String username = "strangerUser";
        Recipe updatedData = new Recipe();

        when(recipeRepository.findById(100L)).thenReturn(Optional.of(sampleRecipe));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(regularUserStranger));

        // Act & Assert - וידוא שנזרקת שגיאת אבטחה בגלל חוסר הרשאות
        assertThrows(SecurityException.class, () -> {
            recipeService.updateRecipe(100L, updatedData, username);
        });

        // וידוא שבסיס הנתונים לא נקרא לשמירה
        verify(recipeRepository, never()).save(any(Recipe.class));
    }

    @Test
    void updateRecipe_RecipeNotFound_ShouldThrowRuntimeException() {
        // Arrange
        when(recipeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert - בדיקה שהמערכת קורסת נכון כשהמתכון לא קיים
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            recipeService.updateRecipe(999L, new Recipe(), "ownerUser");
        });

        assertEquals("המתכון המבוקש לא נמצא במערכת", exception.getMessage());
    }

    // ========================================================
    // בדיקות עבור פונקציית מחיקת מתכון (deleteRecipe)
    // ========================================================

    @Test
    void deleteRecipe_AsOwner_ShouldSuccess() {
        // Arrange
        String username = "ownerUser";
        when(recipeRepository.findById(100L)).thenReturn(Optional.of(sampleRecipe));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(regularUserOwner));

        // Act
        recipeService.deleteRecipe(100L, username);

        // Assert - וידוא שהפונקציה אכן קראה למחיקה מה-Repository
        verify(recipeRepository, times(1)).delete(sampleRecipe);
    }

    @Test
    void deleteRecipe_AsStranger_ShouldThrowSecurityException() {
        // Arrange
        String username = "strangerUser";
        when(recipeRepository.findById(100L)).thenReturn(Optional.of(sampleRecipe));
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(regularUserStranger));

        // Act & Assert
        assertThrows(SecurityException.class, () -> {
            recipeService.deleteRecipe(100L, username);
        });

        verify(recipeRepository, never()).delete(any(Recipe.class));
    }
}