import { Routes } from '@angular/router';
import { RecipeListComponent } from './components/recipe-list/recipe-list';
import { LoginComponent } from './components/login/login';
import { RegisterComponent } from './components/register/register';
import { RecipeFormComponent } from './components/recipe-form/recipe-form';
import {ProductFormComponent} from './components/product-form/product-form'
import { AllergenFormComponent } from './components/allergen-form/allergen-form';
import { ProductListComponent } from './components/product-list/product-list';

import { AllergenListComponent } from './components/allergen-list/allergen-list';

export const routes: Routes = [
  { path: '', component: RecipeListComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'add-recipe', component: RecipeFormComponent },
  { path: 'edit-recipe/:id', component: RecipeFormComponent },
  { path: 'admin/add-product', component: ProductFormComponent },
  { path: 'admin/add-allergen', component: AllergenFormComponent },
  { path: 'admin/products', component: ProductListComponent },
  { path: 'admin/allergens', component: AllergenListComponent }
];
