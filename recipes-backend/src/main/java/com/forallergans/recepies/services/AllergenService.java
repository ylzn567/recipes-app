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
        
        allergenRepository.deleteById(allergenId);
    }

    private void validateAdmin(User currentUser) {
        if (currentUser.getRole() != Role.ADMIN) {
            throw new SecurityException("פעולה זו מורשית למנהלי מערכת בלבד.");
        }
    }
}