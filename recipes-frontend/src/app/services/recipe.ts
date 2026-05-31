import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Recipe } from '../models/recipe.model';

@Injectable({
  providedIn: 'root'
})
export class RecipeService {
  // כתובת ה-API של השרת שלכן
  private apiUrl = 'http://localhost:8080/api/recipes';

  constructor(private http: HttpClient) { }

  // פונקציה שמחזירה רשימת מתכונים מהשרת
  getRecipes(): Observable<Recipe[]> {
    return this.http.get<Recipe[]>(this.apiUrl);
  }
}

export type { Recipe };
