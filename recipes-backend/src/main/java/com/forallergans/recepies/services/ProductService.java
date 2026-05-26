package com.forallergans.recepies.services;

import java.util.Optional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.forallergans.recepies.entities.Product;
import com.forallergans.recepies.entities.User;
import com.forallergans.recepies.entities.Role;
import com.forallergans.recepies.repositories.ProductRepository;


@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
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
        
        return productRepository.save(product);
    }

    // עדכון מוצר קיים - מיועד למנהל בלבד
    public Product updateProduct(Long productId, Product updatedData, User currentUser) {
        validateAdmin(currentUser); // בדיקת הרשאה

        Product existingProduct = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("המוצר לעדכון לא נמצא"));

        existingProduct.setName(updatedData.getName());
        existingProduct.setAllergens(updatedData.getAllergens());

        return productRepository.save(existingProduct);
    }

    // מחיקת מוצר - מיועד למנהל בלבד
    public void deleteProduct(Long productId, User currentUser) {
        validateAdmin(currentUser); // בדיקת הרשאה
        
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("המוצר למחיקה לא נמצא");
        }
        
        productRepository.deleteById(productId);
    }

    // מתודת עזר פרטית לבדיקת תפקיד המנהל
    private void validateAdmin(User currentUser) {
        if (currentUser.getRole() != Role.ADMIN) {
            throw new SecurityException("פעולה זו מורשית למנהלי מערכת בלבד.");
        }
    }
}