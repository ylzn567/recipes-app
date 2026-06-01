import { Routes } from '@angular/router';
import { RecipeListComponent } from './components/recipe-list/recipe-list';
import { LoginComponent } from './components/login/login';
import { RegisterComponent } from './components/register/register';
import { RecipeFormComponent } from './components/recipe-form/recipe-form';
import {ProductFormComponent} from './components/product-form/product-form'

export const routes: Routes = [
  { path: '', component: RecipeListComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'add-recipe', component: RecipeFormComponent },
  { path: 'admin/add-product', component: ProductFormComponent }
];
