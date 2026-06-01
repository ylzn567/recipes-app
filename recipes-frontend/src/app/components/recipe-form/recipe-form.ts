// import { Component, OnInit } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { ReactiveFormsModule, FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
// import { Router } from '@angular/router';
// import { RecipeService } from '../../services/recipe';

// @Component({
//   selector: 'app-recipe-form',
//   standalone: true,
//   imports: [CommonModule, ReactiveFormsModule],
//   templateUrl: './recipe-form.html',
//   styleUrl: './recipe-form.css'
// })
// export class RecipeFormComponent implements OnInit {
//   recipeForm!: FormGroup;
//   isSubmitting = false;
//   errorMessage = '';

//   constructor(
//     private fb: FormBuilder,
//     private recipeService: RecipeService,
//     private router: Router
//   ) {}

//   ngOnInit(): void {
//     this.recipeForm = this.fb.group({
//       title: ['', Validators.required],
//       instructions: ['', Validators.required],
//       preparationTimeMinutes: [0, [Validators.required, Validators.min(1)]],
//       difficulty: ['EASY', Validators.required],
//       kashrut: ['PARVE', Validators.required],
//       ingredients: this.fb.array([])
//     });

//     // נוסיף מצרך אחד ריק כברירת מחדל
//     this.addIngredient();
//   }

//   get ingredients(): FormArray {
//     return this.recipeForm.get('ingredients') as FormArray;
//   }

//   addIngredient(): void {
//     const ingredientForm = this.fb.group({
//       productName: ['', Validators.required],
//       quantity: [1, [Validators.required, Validators.min(0.1)]],
//       measurementUnit: ['UNIT', Validators.required]
//     });
//     this.ingredients.push(ingredientForm);
//   }

//   removeIngredient(index: number): void {
//     if (this.ingredients.length > 1) {
//       this.ingredients.removeAt(index);
//     }
//   }

//   onSubmit(): void {
//     if (this.recipeForm.invalid) {
//       this.recipeForm.markAllAsTouched();
//       return;
//     }

//     this.isSubmitting = true;
//     this.errorMessage = '';

//     const formValue = this.recipeForm.value;
    
//     // המרה לפורמט שהשרת מצפה לו
//     const newRecipe = {
//       title: formValue.title,
//       instructions: formValue.instructions,
//       preparationTimeMinutes: formValue.preparationTimeMinutes,
//       difficulty: formValue.difficulty,
//       kashrut: formValue.kashrut,
//       ingredients: formValue.ingredients.map((ing: any) => ({
//         quantity: ing.quantity,
//         measurementUnit: ing.measurementUnit,
//         product: {
//           name: ing.productName
//         }
//       }))
//     };

//     this.recipeService.createRecipe(newRecipe).subscribe({
//       next: () => {
//         this.isSubmitting = false;
//         this.router.navigate(['/']); // חזרה לדף הבית
//       },
//       error: (err) => {
//         this.isSubmitting = false;
//         this.errorMessage = err.error || err.message || 'שגיאה בשמירת המתכון';
//       }
//     });
//   }
// }

import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { RecipeService } from '../../services/recipe';
import { ProductService } from '../../services/product'; // 🌟 ייבוא שירות המוצרים החדש

@Component({
  selector: 'app-recipe-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './recipe-form.html',
  styleUrl: './recipe-form.css'
})
export class RecipeFormComponent implements OnInit {
  recipeForm!: FormGroup;
  isSubmitting = false;
  errorMessage = '';

  // 🌟 רשימת המוצרים שתטען מהשרת
  availableProducts: any[] = []; 
  
  // 🌟 משתנים מחושבים להצגה דינמית במסך
  calculatedKashrut = 'PARVE';
  calculatedAllergens: Set<string> = new Set<string>();

  constructor(
    private fb: FormBuilder,
    private recipeService: RecipeService,
    private productService: ProductService, // 🌟 הזרקת שירות המוצרים בבנאי
    private router: Router
  ) {}

  ngOnInit(): void {
    // טעינת המוצרים מה-DB ברגע שהמסך עולה
    this.loadAvailableProducts();

    this.recipeForm = this.fb.group({
      title: ['', Validators.required],
      instructions: ['', Validators.required],
      preparationTimeMinutes: [0, [Validators.required, Validators.min(1)]],
      difficulty: ['EASY', Validators.required],
      // ⚠️ שימי לב: מחקנו מכאן את ה-kashrut כי הוא כבר לא שדה קלט ידני בטופס
      ingredients: this.fb.array([])
    });

    // נוסיף מצרך אחד ריק כברירת מחדל
    this.addIngredient();
  }

  get ingredients(): FormArray {
    return this.recipeForm.get('ingredients') as FormArray;
  }

  // 🌟 משיכת המוצרים הקיימים במערכת מהשירות החדש
  loadAvailableProducts(): void {
    this.productService.getAllProducts().subscribe({
      next: (products) => {
        console.log('Fetched products:', products);
        this.availableProducts = products || [];
      },
      error: (err) => {
        console.error('שגיאה בטעינת רשימת המוצרים מהשרת:', err);
        this.errorMessage = 'לא ניתן לטעון את רשימת המצרכים המאושרים.';
      }
    });
  }

  addIngredient(): void {
    const ingredientForm = this.fb.group({
      productName: ['', Validators.required],
      quantity: [1, [Validators.required, Validators.min(0.1)]],
      measurementUnit: ['UNIT', Validators.required]
    });
    this.ingredients.push(ingredientForm);
  }

  removeIngredient(index: number): void {
    if (this.ingredients.length > 1) {
      this.ingredients.removeAt(index);
      this.recalculateRecipeSpecs(); // חישוב מחדש לאחר מחיקה
    }
  }

  // 🌟 פונקציה שנקרא לה מה-HTML בכל פעם שהמשתמש בוחר מוצר מה-Dropdown
  onProductSelected(): void {
    this.recalculateRecipeSpecs();
  }

  // 🌟 לוגיקה שמחשבת אוטומטית כשרות ואלרגנים על בסיס המוצרים שנבחרו בטופס
  recalculateRecipeSpecs(): void {
    const currentIngredients = this.ingredients.value;
    let hasDairy = false;
    let hasMeat = false;
    
    this.calculatedAllergens.clear();

    for (const ing of currentIngredients) {
      if (!ing.productName) continue;

      // מציאת אובייקט המוצר המלא מתוך המערך שמשכנו מהשרת
      const fullProduct = this.availableProducts.find(p => p.name === ing.productName);
      
      if (fullProduct) {
        // 1. בדיקת סטטוס כשרות המוצר
        if (fullProduct.kashrut === 'DAIRY') hasDairy = true;
        if (fullProduct.kashrut === 'MEAT') hasMeat = true;
        
        // 2. חילוץ האלרגנים של המוצר (תומך גם באובייקטים וגם במחרוזות)
        if (fullProduct.allergens && fullProduct.allergens.length > 0) {
          fullProduct.allergens.forEach((alg: any) => {
            const allergenName = typeof alg === 'string' ? alg : alg.name;
            this.calculatedAllergens.add(allergenName);
          });
        }
      }
    }

    // קביעת הסטטוס הסופי לפי חוקי הכשרות
    if (hasDairy && hasMeat) {
      this.calculatedKashrut = 'INVALID'; // הגנה מפני ערבוב בשר וחלב
    } else if (hasMeat) {
      this.calculatedKashrut = 'MEAT';
    } else if (hasDairy) {
      this.calculatedKashrut = 'DAIRY';
    } else {
      this.calculatedKashrut = 'PARVE';
    }
  }

  onSubmit(): void {
    if (this.recipeForm.invalid) {
      this.recipeForm.markAllAsTouched();
      return;
    }

    // חסימת שמירה אם יש ערבוב אסור של בשר וחלב
    if (this.calculatedKashrut === 'INVALID') {
      this.errorMessage = 'לא ניתן לשמור מתכון המכיל גם רכיבים בשריים וגם רכיבים חלביים!';
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';

    const formValue = this.recipeForm.value;
    
    // המרה לפורמט שהשרת מצפה לו
    const newRecipe = {
      title: formValue.title,
      instructions: formValue.instructions,
      preparationTimeMinutes: formValue.preparationTimeMinutes,
      difficulty: formValue.difficulty,
      kashrut: this.calculatedKashrut, // 🌟 שליחת הכשרות שחושבה אוטומטית במקום קלט ידני
      ingredients: formValue.ingredients.map((ing: any) => ({
        quantity: ing.quantity,
        measurementUnit: ing.measurementUnit,
        product: {
          name: ing.productName
        }
      }))
    };

    this.recipeService.createRecipe(newRecipe).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.router.navigate(['/']); // חזרה לדף הבית
      },
      error: (err) => {
        this.isSubmitting = false;
        this.errorMessage = err.error?.message || err.message || 'שגיאה בשמירת המתכון';
      }
    });
  }
}