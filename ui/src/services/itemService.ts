// src/api/itemApi.ts
import api from './api';
import { Item } from '../types/Item';
import { PaginatedResponse } from '../types/PaginatedResponse';

// Get all items (paginated)
export const getItems = async (page: number = 1): Promise<PaginatedResponse<Item>> => {
  const response = await api.get<PaginatedResponse<Item>>('/api/items', {
    params: { page },
  });
  return response.data;
};

// Get item by ID
export const getItemById = async (id: number): Promise<Item> => {
  const response = await api.get<Item>(`/api/items/${id}`);
  return response.data;
};

// Search items by name (paginated)
export const searchItems = async (name: string, page: number = 1): Promise<PaginatedResponse<Item>> => {
  const response = await api.get<PaginatedResponse<Item>>('/api/items/search', {
    params: { name, page },
  });
  return response.data;
};

// Get items by category ID (paginated)
export const getItemsByCategory = async (categoryId: number, page: number = 1): Promise<PaginatedResponse<Item>> => {
  const response = await api.get<PaginatedResponse<Item>>('/api/items/category', {
    params: { categoryId, page },
  });
  return response.data;
};

// Get low stock items
export const getLowStockItems = async (threshold: number): Promise<Item[]> => {
  const response = await api.get<Item[]>('/api/items/lowStock', {
    params: { threshold },
  });
  return response.data;
};

// Create a new item
export const createItem = async (item: Item): Promise<Item> => {
  const response = await api.post<Item>('/api/items', item);
  return response.data;
};

// Update general item info by ID
export const updateItem = async (id: number, item: Item): Promise<Item> => {
  const response = await api.put<Item>(`/api/items/${id}`, item);
  return response.data;
};

// Update item stock
export const updateItemStock = async (id: number, stock: number): Promise<Item> => {
  const response = await api.put<Item>('/api/items/updateStock', null, {
    params: { id, stock },
  });
  return response.data;
};

// Update item discount
export const updateItemDiscount = async (id: number, discount: number, qty: number): Promise<Item> => {
  const response = await api.put<Item>('/api/items/updateDiscount', null, {
    params: { id, discount, qty },
  });
  return response.data;
};

// Delete item by ID
export const deleteItem = async (id: number): Promise<void> => {
  await api.delete(`/api/items/${id}`);
};
