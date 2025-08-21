 
import api from './api';
import { Category } from '../types/Category';

// Get all categories
export const getCategories = async (): Promise<Category[]> => {
  const response = await api.get<Category[]>('/api/categories');
  return response.data;
};

// Get category by ID
export const getCategoryById = async (id: number): Promise<Category> => {
  const response = await api.get<Category>(`/api/categories/${id}`);
  return response.data;
};

// Search categories by name
export const searchCategories = async (name: string): Promise<Category[]> => {
  const response = await api.get<Category[]>('/api/categories/search', {
    params: { name },
  });
  return response.data;
};

// Get recently updated categories (optional limit)
export const getRecentCategories = async (limit: number = 5): Promise<Category[]> => {
  const response = await api.get<Category[]>('/api/categories/recent', {
    params: { limit },
  });
  return response.data;
};

// Get categories with minimum items
export const getCategoriesWithMinItems = async (min: number): Promise<Category[]> => {
  const response = await api.get<Category[]>('/api/categories/withMinItems', {
    params: { min },
  });
  return response.data;
};

// Create a new category
export const createCategory = async (category: Category): Promise<Category> => {
  const response = await api.post<Category>('/api/categories', category);
  return response.data;
};

// Update a category
export const updateCategory = async (category: Category): Promise<Category> => {
  if (!category.categoryId) {
    throw new Error('Category ID is required for update');
  }
  const response = await api.put<Category>('/api/categories', category);
  return response.data;
};

// Delete category by ID or name
export const deleteCategory = async (identifier: number | string): Promise<void> => {
  await api.delete(`/api/categories/${identifier}`);
};
