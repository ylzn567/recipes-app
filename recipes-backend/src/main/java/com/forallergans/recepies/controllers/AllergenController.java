package com.forallergans.recepies.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.forallergans.recepies.services.AllergenService;
import com.forallergans.recepies.entities.Allergen;
import java.util.List;

@RestController
@RequestMapping("/api/allergens")
@CrossOrigin(origins = "http://localhost:4200")
public class AllergenController {

    private final AllergenService allergenService;

    @Autowired
    public AllergenController(AllergenService allergenService) {
        this.allergenService = allergenService;
    }

    // שליפת כל האלרגנים (לצורך הצגת רשימת בחירה בסינונים ב-Angular) - פתוח לכולם
    @GetMapping
    public ResponseEntity<List<Allergen>> getAllAllergens() {
        return ResponseEntity.ok(allergenService.getAllAllergens());
    }

    // הוספת אלרגן חדש למערכת - חסום ב-SecurityConfig רק ל-ADMIN
    @PostMapping
    public ResponseEntity<Allergen> createAllergen(@RequestBody Allergen allergen) {
        Allergen created = allergenService.createAllergen(allergen, null);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // מחיקת אלרגן מהמערכת - חסום ב-SecurityConfig רק ל-ADMIN
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAllergen(@PathVariable Long id) {
        allergenService.deleteAllergen(id, null);
        return ResponseEntity.ok("האלרגן נמחק בהצלחה");
    }
}