import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AllergenService, Allergen } from '../../services/allergen';

@Component({
  selector: 'app-allergen-list',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './allergen-list.html',
  styleUrl: './allergen-list.css'
})
export class AllergenListComponent implements OnInit {
  allergens: Allergen[] = [];
  loading: boolean = true;
  errorMessage: string | null = null;
  editingId: number | null = null;
  editName: string = '';

  constructor(private allergenService: AllergenService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.loadAllergens();
  }

  loadAllergens(): void {
    this.loading = true;
    this.allergenService.getAllAllergens().subscribe({
      next: (data) => {
        this.allergens = data || [];
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.errorMessage = 'שגיאה בשליפת אלרגנים מהשרת';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  startEdit(allergen: Allergen): void {
    if (allergen.id !== undefined) {
      this.editingId = allergen.id;
      this.editName = allergen.name;
    }
  }

  cancelEdit(): void {
    this.editingId = null;
    this.editName = '';
  }

  saveEdit(allergen: Allergen): void {
    if (!allergen.id || !this.editName.trim()) return;
    
    this.allergenService.updateAllergen(allergen.id, { name: this.editName.trim() }).subscribe({
      next: (updated) => {
        const index = this.allergens.findIndex(a => a.id === updated.id);
        if (index !== -1) {
          this.allergens[index] = updated;
        }
        this.cancelEdit();
        this.cdr.detectChanges();
      },
      error: (err) => {
        const msg = typeof err.error === 'string' ? err.error : (err.error?.message || err.message);
        alert('שגיאה בעדכון אלרגן: ' + msg);
      }
    });
  }

  deleteAllergen(allergen: Allergen): void {
    if (!allergen.id) return;
    
    if (confirm(`האם אתה בטוח שברצונך למחוק את האלרגן "${allergen.name}"?`)) {
      this.allergenService.deleteAllergen(allergen.id).subscribe({
        next: () => {
          this.allergens = this.allergens.filter(a => a.id !== allergen.id);
          this.cdr.detectChanges();
        },
        error: (err) => {
          const msg = typeof err.error === 'string' ? err.error : (err.error?.message || err.message);
          alert('שגיאה במחיקת האלרגן: ' + msg);
        }
      });
    }
  }
}
