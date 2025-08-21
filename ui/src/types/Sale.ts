import { Customer } from './Customer';
import { SaleItem } from './SaleItem';

export interface Sale {
  saleId: number;
  customerId: number;
totalAmount: number;
  totalDiscount: number;
    subTotal: number;
    paid: number;
    balance: number;
    saleDate: string;s
    saleTime: string;
    lastUpdatedAt: string;
     
  customerName?: string;   
  saleItems?: SaleItem[];
}
 