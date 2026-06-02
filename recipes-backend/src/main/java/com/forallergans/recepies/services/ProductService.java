package com.forallergans.recepies.services;

import java.util.Optional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.forallergans.recepies.entities.Product;
import com.forallergans.recepies.entities.Allergen;
import com.forallergans.recepies.entities.User;
import com.forallergans.recepies.entities.Role;
import com.forallergans.recepies.repositories.ProductRepository;
import com.forallergans.recepies.repositories.AllergenRepository;
import java.util.Set;
import java.util.HashSet;


@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final AllergenRepository allergenRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, AllergenRepository allergenRepository) {
        this.productRepository = productRepository;
        this.allergenRepository = allergenRepository;
    }
//לבדוק האם שימושי בכלל ולא מיותר
    // שליפת כל המוצרים - מותר לכולם (משתמשים ומנהלים)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }


    public Optional<Product> getProductById(Long id) {
    return productRepository.findById(id);
    }
    // יצירת מוצר חדש - מיועד למנהל בלבד
    public Product createProduct(Product product, User currentUser) {
        validateAdmin(currentUser); // בדיקת הרשאה
        
        // בדיקה אופציונלית: האם המוצר כבר קיים כדי למנוע כפילויות
        if (productRepository.findByName(product.getName()).isPresent()) {
            throw new RuntimeException("מוצר עם שם זה כבר קיים במערכת");
        }
        
        if (product.getAllergens() != null) {
            Set<Allergen> persistedAllergens = new HashSet<>();
            for (Allergen a : product.getAllergens()) {
                if (a.getName() != null) {
                    allergenRepository.findByName(a.getName()).ifPresent(persistedAllergens::add);
                }
            }
            product.setAllergens(persistedAllergens);
        }
        
        return productRepository.save(product);
    }

    // עדכון מוצר קיים - מיועד למנהל בלבד
    public Product updateProduct(Long productId, Product updatedData, User currentUser) {
        validateAdmin(currentUser); // בדיקת הרשאה

        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("המוצר לעדכון לא נמצא"));

        existingProduct.setName(updatedData.getName());
        existingProduct.setKashrut(updatedData.getKashrut());
        
        if (updatedData.getAllergens() != null) {
            Set<Allergen> persistedAllergens = new HashSet<>();
            for (Allergen a : updatedData.getAllergens()) {
                if (a.getName() != null) {
                    allergenRepository.findByName(a.getName()).ifPresent(persistedAllergens::add);
                }
            }
            existingProduct.setAllergens(persistedAllergens);
        } else {
            existingProduct.setAllergens(null);
        }

        return productRepository.save(existingProduct);
    }

    // מחיקת מוצר - מיועד למנהל בלבד
    public void deleteProduct(Long productId, User currentUser) {
        validateAdmin(currentUser); // בדיקת הרשאה
        
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("המוצר למחיקה לא נמצא");
        }
        
        try {
            productRepository.deleteById(productId);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new RuntimeException("לא ניתן למחוק מוצר זה כי הוא נמצא בשימוש במתכונים קיימים");
        }
    }

    // מתודת עזר פרטית לבדיקת תפקיד המנהל
    private void validateAdmin(User currentUser) {
        if (currentUser != null && currentUser.getRole() != Role.ADMIN) {
            throw new SecurityException("פעולה זו מורשית למנהלי מערכת בלבד.");
        }
    }
}