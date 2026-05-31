export interface Ingredient {
  product: {
    name: string;
    allergens: string[];
  };
  quantity: number;
  measurementUnit: string;
}

export interface Recipe {
  title: string;
  instructions: string;
  preparationTimeMinutes: number;
  difficulty: string;
  kashrut: string;
  createdByUsername: string;
  ingredients: Ingredient[];
  allergens: string[];
}