import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AllergenService } from '../../services/allergen';

@Component({
  selector: 'app-allergen-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './allergen-form.html',
  styleUrl: './allergen-form.css'
})
export class AllergenFormComponent implements OnInit {
  allergenForm!: FormGroup;
  isSubmitting = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private fb: FormBuilder,
    private allergenService: AllergenService
  ) {}

  ngOnInit(): void {
    this.allergenForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]]
    });
  }

  onSubmit(): void {
    if (this.allergenForm.invalid) {
      this.allergenForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';
    this.successMessage = '';

    const newAllergen = {
      name: this.allergenForm.value.name
    };

    this.allergenService.addAllergen(newAllergen).subscribe({
      next: () => {
        this.isSubmitting = false;
        this.successMessage = '🎉 האלרגן נוסף בהצלחה למערכת!';
        this.allergenForm.reset();
      },
      error: (err) => {
        this.isSubmitting = false;
        this.errorMessage = err.error?.message || err.message || 'שגיאה בהוספת האלרגן';
      }
    });
  }
}
