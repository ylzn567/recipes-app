package com.forallergans.recepies.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.forallergans.recepies.entities.Allergen;
import com.forallergans.recepies.entities.Role;
import com.forallergans.recepies.entities.User;
import com.forallergans.recepies.repositories.AllergenRepository;


@Service
public class AllergenService {

    private final AllergenRepository allergenRepository;

    @Autowired
    public AllergenService(AllergenRepository allergenRepository) {
        this.allergenRepository = allergenRepository;
    }

    // שליפת כל האלרגנים - מותר לכולם כדי שיוכלו לבחור מתוכם בסינון
    public List<Allergen> getAllAllergens() {
        return allergenRepository.findAll();
    }

    // הוספת אלרגן חדש - מנהל בלבד
    public Allergen createAllergen(Allergen allergen, User currentUser) {
        validateAdmin(currentUser);
        
        if (allergenRepository.findByName(allergen.getName()).isPresent()) {
            throw new RuntimeException("אלרגן זה כבר קיים במערכת");
        }
        
        return allergenRepository.save(allergen);
    }

    // מחיקת אלרגן - מנהל בלבד
    public void deleteAllergen(Long allergenId, User currentUser) {
        validateAdmin(currentUser);
        
        if (!allergenRepository.existsById(allergenId)) {
            throw new RuntimeException("האלרגן למחיקה לא נמצא");
        }
        
        try {
            allergenRepository.deleteById(allergenId);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new RuntimeException("לא ניתן למחוק אלרגן זה כי הוא נמצא בשימוש במוצרים קיימים במערכת");
        }
    }

    // עדכון אלרגן - מנהל בלבד
    public Allergen updateAllergen(Long allergenId, Allergen updatedData, User currentUser) {
        validateAdmin(currentUser);
        
        Allergen existing = allergenRepository.findById(allergenId)
            .orElseThrow(() -> new RuntimeException("האלרגן לא נמצא"));
            
        // בדוק שאין אלרגן אחר עם אותו שם
        if (!existing.getName().equals(updatedData.getName()) && 
            allergenRepository.findByName(updatedData.getName()).isPresent()) {
            throw new RuntimeException("אלרגן עם שם כזה כבר קיים");
        }
        
        existing.setName(updatedData.getName());
        return allergenRepository.save(existing);
    }

    private void validateAdmin(User currentUser) {
        if (currentUser != null && currentUser.getRole() != Role.ADMIN) {
            throw new SecurityException("פעולה זו מורשית למנהלי מערכת בלבד.");
        }
    }
}