package com.forallergans.recepies.services;  
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import com.forallergans.recepies.entities.Product;
import com.forallergans.recepies.entities.User;
import com.forallergans.recepies.entities.Role;
import com.forallergans.recepies.repositories.ProductRepository;
import com.forallergans.recepies.repositories.AllergenRepository;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private AllergenRepository allergenRepository;

    @InjectMocks
    private ProductService productService;

    private User adminUser;
    private User regularUser;
    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        // הגדרת משתמש מנהל לקריאות מורשות
        adminUser = new User();
        adminUser.setId(1L);
        adminUser.setUsername("admin");
        adminUser.setRole(Role.ADMIN);

        // הגדרת משתמש רגיל לבדיקת חסימות אבטחה
        regularUser = new User();
        regularUser.setId(2L);
        regularUser.setUsername("user");
        regularUser.setRole(Role.USER);

        // הגדרת מוצר לדוגמה
        sampleProduct = new Product();
        sampleProduct.setId(10L);
        sampleProduct.setName("קמח מצה");
    }

    // ========================================================
    // בדיקות עבור יצירת מוצר (createProduct)
    // ========================================================

    @Test
    void createProduct_AsAdmin_ShouldSuccess() {
        // Arrange
        when(productRepository.findByName("קמח מצה")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Product result = productService.createProduct(sampleProduct, adminUser);

        // Assert
        assertNotNull(result);
        assertEquals("קמח מצה", result.getName());
        verify(productRepository, times(1)).save(sampleProduct);
    }

    @Test
    void createProduct_AsRegularUser_ShouldThrowSecurityException() {
        // Act & Assert - המערכת חייבת לחסום משתמש רגיל
        assertThrows(SecurityException.class, () -> {
            productService.createProduct(sampleProduct, regularUser);
        });

        // וידוא שלא בוצעה פנייה ל-Repository לשמירה או לבדיקת שם כפול
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void createProduct_AlreadyExists_ShouldThrowRuntimeException() {
        // Arrange - מדמים מצב שהמוצר כבר קיים במערכת
        when(productRepository.findByName("קמח מצה")).thenReturn(Optional.of(sampleProduct));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.createProduct(sampleProduct, adminUser);
        });

        assertEquals("מוצר עם שם זה כבר קיים במערכת", exception.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    // ========================================================
    // בדיקות עבור מחיקת מוצר (deleteProduct)
    // ========================================================

    @Test
    void deleteProduct_AsAdmin_ShouldSuccess() {
        // Arrange
        when(productRepository.existsById(10L)).thenReturn(true);

        // Act
        productService.deleteProduct(10L, adminUser);

        // Assert - וידוא שבוצעה מחיקה בפועל לפי ה-ID
        verify(productRepository, times(1)).deleteById(10L);
    }

    @Test
    void deleteProduct_ProductInUse_ShouldThrowRuntimeException() {
        // Arrange - מדמים מצב שבו המחיקה נכשלת עקב אילוץ בסיס נתונים (המוצר בשימוש במתכונים)
        when(productRepository.existsById(10L)).thenReturn(true);
        doThrow(new DataIntegrityViolationException("FK constraint")).when(productRepository).deleteById(10L);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.deleteProduct(10L, adminUser);
        });

        assertEquals("לא ניתן למחוק מוצר זה כי הוא נמצא בשימוש במתכונים קיימים", exception.getMessage());
    }

    @Test
    void deleteProduct_NotFound_ShouldThrowRuntimeException() {
        // Arrange
        when(productRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            productService.deleteProduct(99L, adminUser);
        });

        assertEquals("המוצר למחיקה לא נמצא", exception.getMessage());
        verify(productRepository, never()).deleteById(anyLong());
    }
}