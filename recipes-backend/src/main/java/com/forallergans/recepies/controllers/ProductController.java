package com.forallergans.recepies.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.forallergans.recepies.dtos.ProductDTO;
import com.forallergans.recepies.entities.Product;
import com.forallergans.recepies.mappers.ProductMapper;

import com.forallergans.recepies.services.ProductService;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // שליפת כל המוצרים - פתוח לכולם
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts().stream()
                .map(ProductMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
         return productService.getProductById(id) 
             .map(product -> ResponseEntity.ok(ProductMapper.toDTO(product)))
             .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    // יצירת מוצר חדש - חסום ב-SecurityConfig רק ל-ADMIN
    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody Product product) {
        // ה-Service עצמו מקבל אובייקט מלא, אבל הקונטרולר מחזיר DTO נקי
        Product created = productService.createProduct(product, null); 
        return new ResponseEntity<>(ProductMapper.toDTO(created), HttpStatus.CREATED);
    }

    // עדכון מוצר - חסום ב-SecurityConfig רק ל-ADMIN
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody Product productData) {
        Product updated = productService.updateProduct(id, productData, null);
        return ResponseEntity.ok(ProductMapper.toDTO(updated));
    }

    // מחיקת מוצר - חסום ב-SecurityConfig רק ל-ADMIN
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id, null);
        return ResponseEntity.ok("המוצר נמחק בהצלחה");
    }
}