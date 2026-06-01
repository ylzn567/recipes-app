import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RecipeService } from '../../services/recipe';
import { Recipe } from '../../models/recipe.model';

import { AuthService } from '../../services/auth';
import { Router } from '@angular/router';

@Component({
  selector: 'app-recipe-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './recipe-list.html',
  styleUrl: './recipe-list.css'
})
export class RecipeListComponent implements OnInit {
  allRecipes: Recipe[] = []; // השמירה של כל המתכונים המקוריים מהשרת
  filteredRecipes: Recipe[] = []; // המתכונים שמוצגים בפועל לאחר סינון
  
  errorMessage: string | null = null;
  loading: boolean = true;

  // משתני סינון מרובים
  filterKashrut: string = '';
  filterDifficulty: string = '';
  
  // רשימת כל האלרגנים הקיימים במערכת (נחלץ אותם מתוך המתכונים)
  availableAllergens: string[] = [];
  // אלרגנים שהמשתמש סימן ב-Checkbox (אלרגנים שהוא *לא* רוצה)
  excludedAllergens: Set<string> = new Set<string>();

  constructor(
    private recipeService: RecipeService, 
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadAllRecipes();
  }

  loadAllRecipes(): void {
    this.loading = true;
    this.errorMessage = null;
    
    // מושכים את כל המתכונים פעם אחת
    this.recipeService.getRecipes().subscribe({
      next: (data) => {
        this.allRecipes = data || [];
        this.filteredRecipes = [...this.allRecipes];
        this.extractAllergens();
        this.loading = false;
        this.errorMessage = null;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.message || JSON.stringify(err);
        console.error('שגיאה בשליפת המתכונים:', err);
        this.cdr.detectChanges();
      }
    });
  }

  // חילוץ כל האלרגנים מכל המתכונים כדי להציג אותם כצ'קבוקסים
  private extractAllergens(): void {
    const allergenSet = new Set<string>();
    for (const recipe of this.allRecipes) {
      if (recipe.allergens && recipe.allergens.length > 0) {
        for (const alg of recipe.allergens) {
          allergenSet.add(alg);
        }
      }
    }
    this.availableAllergens = Array.from(allergenSet).sort();
  }

  // טיפול בשינוי מצב של Checkbox אלרגן
  toggleAllergen(allergen: string, event: any): void {
    if (event.target.checked) {
      this.excludedAllergens.add(allergen);
    } else {
      this.excludedAllergens.delete(allergen);
    }
    this.applyCombinedFilters();
  }

  // הפעלת כל הסינונים (קשרות + קושי + אלרגנים) על רשימת המתכונים המקומית
  applyCombinedFilters(): void {
    this.filteredRecipes = this.allRecipes.filter(recipe => {
      // 1. סינון כשרות
      if (this.filterKashrut && recipe.kashrut !== this.filterKashrut) {
        return false;
      }
      
      // 2. סינון קושי
      if (this.filterDifficulty && recipe.difficulty !== this.filterDifficulty) {
        return false;
      }
      
      // 3. סינון אלרגנים (אם למתכון יש אלרגן שנמצא ברשימת "לא רוצה")
      if (this.excludedAllergens.size > 0 && recipe.allergens) {
        const hasExcludedAllergen = recipe.allergens.some(alg => this.excludedAllergens.has(alg));
        if (hasExcludedAllergen) {
          return false;
        }
      }
      
      return true;
    });
  }

  clearFilters(): void {
    this.filterKashrut = '';
    this.filterDifficulty = '';
    this.excludedAllergens.clear();
    
    // איפוס ה-checkboxes ב-DOM
    const checkboxes = document.querySelectorAll('input[type="checkbox"]');
    checkboxes.forEach((cb: any) => cb.checked = false);

    this.applyCombinedFilters();
  }

  canEdit(recipe: Recipe): boolean {
    if (this.authService.isAdmin()) return true;
    const currentUser = this.authService.getUsername();
    return currentUser !== null && currentUser === recipe.createdByUsername;
  }

  editRecipe(recipe: Recipe): void {
    if (recipe.id) {
      this.router.navigate(['/edit-recipe', recipe.id]);
    }
  }

  deleteRecipe(recipe: Recipe): void {
    if (!recipe.id) return;
    if (confirm(`האם אתה בטוח שברצונך למחוק את המתכון "${recipe.title}"?`)) {
      this.recipeService.deleteRecipe(recipe.id).subscribe({
        next: () => {
          this.allRecipes = this.allRecipes.filter(r => r.id !== recipe.id);
          this.applyCombinedFilters();
          this.extractAllergens();
          this.cdr.detectChanges();
        },
        error: (err) => {
          const msg = typeof err.error === 'string' ? err.error : (err.error?.message || err.message);
          alert('שגיאה במחיקת המתכון: ' + msg);
        }
      });
    }
  }

  getKashrutHebrew(kashrut: string): string {
    const mapping: { [key: string]: string } = {
      'PARVE': 'פרווה',
      'DAIRY': 'חלבי',
      'MEAT': 'בשרי',
      'INVALID': 'לא כשר'
    };
    return mapping[kashrut] || kashrut;
  }

  getDifficultyHebrew(difficulty: string): string {
    const mapping: { [key: string]: string } = {
      'EASY': 'קל',
      'MEDIUM': 'בינוני',
      'HARD': 'קשה'
    };
    return mapping[difficulty] || difficulty;
  }

  getUnitHebrew(unit: string): string {
    const mapping: { [key: string]: string } = {
      'GRAM': 'גרם',
      'KG': 'ק"ג',
      'KILOGRAM': 'ק"ג',
      'ML': 'מ"ל',
      'MILLILITER': 'מ"ל',
      'LITER': 'ליטר',
      'CUP': 'כוס',
      'TABLESPOON': 'כף',
      'TEASPOON': 'כפית',
      'UNIT': 'יחידה',
      'PINCH': 'קורט'
    };
    return mapping[unit] || unit;
  }
}