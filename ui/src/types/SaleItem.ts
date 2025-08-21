import { Item } from "./Item";

 export interface SaleItem {
  item: Item;  
  saleItemId?: number; // optional for creation
  saleId?: number; // optional for creation
  qty: number;
  discountAmount: number;
  itemTotal: number;
  lastUpdatedAt?: string;  
} 