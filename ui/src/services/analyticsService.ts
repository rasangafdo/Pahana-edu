 
import { DashboardAnalytics ,DashboardStat,ItemAnalytics} from '@/types/AnalyticsTypes';
import { Users, Package, DollarSign } from "lucide-react";
import api from './api';
 
// Get dashboard analytics

export const getDashboardAnalytics = async (): Promise<DashboardStat[]> => {
  const response = await api.get<DashboardAnalytics>("/api/analytics/dashboard");
  const data = response.data;

  const stats: DashboardStat[] = [
    {
      title: "Total Customers",
      value: data.totalCustomers.toLocaleString(),
      change: `${data.customerChange >= 0 ? "+" : ""}${data.customerChange}%`,
      icon: Users,
      color: "text-primary",
    },
    {
      title: "Items in Stock",
      value: data.itemsInStock.toLocaleString(),
      change: `${data.stockChange >= 0 ? "+" : ""}${data.stockChange}%`,
      icon: Package,
      color: "text-success",
    },
    {
      title: "Today's Sales (LKR)",
      value: data.todaysSales.toLocaleString(),
      change: `${data.salesChange >= 0 ? "+" : ""}${data.salesChange}%`,
      icon: DollarSign,
      color: "text-warning",
    },
  ];

  return stats;
};

// Get item analytics
export const getItemAnalytics = async (): Promise<ItemAnalytics[]> => {
  const response = await api.get<ItemAnalytics[]>('/api/analytics/items');
  return response.data;
};