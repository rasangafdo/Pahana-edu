export interface Item {
  itemId?: number; // optional for creation
  name: string;
  unitPrice: number;
  stockAvailable: number;
  discount: number;
  qtyToAllowDiscount: number;
  lastUpdatedAt?: string; // ISO string from backend
  categoryId: number;
}