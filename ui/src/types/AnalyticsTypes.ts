
import { Item } from '@/types/Item';
export interface DashboardAnalytics{
    salesChange: number;
    totalCustomers: number;
    itemsInStock: number;
    customerChange: number;
    stockChange: number;
    todaysSales: number;
}


export interface ItemAnalytics {
    totalItems: number;
    avgPriceTotal: number;
    totalCategories: number;
    inStock: number;
}


export interface DashboardStat {
  title: string;
  value: string;
  change: string;
  icon: any;
  color: string;
}
 