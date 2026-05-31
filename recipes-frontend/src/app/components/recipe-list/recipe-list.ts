import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RecipeService } from '../../services/recipe';
import { Recipe } from '../../models/recipe.model';

@Component({
  selector: 'app-recipe-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './recipe-list.html',
  styleUrl: './recipe-list.css'
})
export class RecipeListComponent implements OnInit {
  recipes: Recipe[] = [];
  errorMessage: string | null = null;
  loading: boolean = true;

  constructor(private recipeService: RecipeService) {}

  ngOnInit(): void {
    this.loading = true;
    this.recipeService.getRecipes().subscribe({
      next: (data: Recipe[]) => {
        this.recipes = data;
        this.loading = false;
        this.errorMessage = null;
      },
      error: (err: any) => {
        this.loading = false;
        // מציג את השגיאה המדויקת על המסך
        this.errorMessage = err.message || JSON.stringify(err);
        console.error('שגיאה בשליפת המתכונים:', err);
      }
    });
  }
}