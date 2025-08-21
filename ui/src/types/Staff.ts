export interface Staff {
  id?: number;
  name: string;
  telephone: string;
  address?: string;
  username: string;
  password: string;
  email: string;
  role: "MANAGER" | "CASHIER";
  isActive?: boolean;
    lastUpdated?: string;
    
}