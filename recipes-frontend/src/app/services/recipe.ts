import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Recipe } from '../models/recipe.model';

@Injectable({
  providedIn: 'root'
})
export class RecipeService {
  private apiUrl = 'http://127.0.0.1:8080/api/recipes';

  constructor(private http: HttpClient) { }

  getRecipes(): Observable<Recipe[]> {
    return this.http.get<Recipe[]>(this.apiUrl);
  }

  getRecipesByKashrut(kashrut: string): Observable<Recipe[]> {
    return this.http.get<Recipe[]>(`${this.apiUrl}/filter/kashrut?kashrut=${kashrut}`);
  }

  getRecipesByDifficulty(difficulty: string): Observable<Recipe[]> {
    return this.http.get<Recipe[]>(`${this.apiUrl}/filter/difficulty?difficulty=${difficulty}`);
  }

  getRecipesWithoutAllergen(allergen: string): Observable<Recipe[]> {
    return this.http.get<Recipe[]>(`${this.apiUrl}/filter/without-allergen?allergenName=${allergen}`);
  }

  createRecipe(recipe: any): Observable<Recipe> {
    return this.http.post<Recipe>(this.apiUrl, recipe);
  }
}

export type { Recipe };
