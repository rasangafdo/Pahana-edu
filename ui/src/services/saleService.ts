
import api from './api';
import { PaginatedResponse } from '../types/PaginatedResponse';
import { Sale } from '../types/Sale';
import { Customer } from '../types/Customer';
import { SaleItem } from '../types/SaleItem';

export interface SaleRequestDTO {
  customer: Customer;
  saleItems: SaleItem[];
  paid: number;
  balance: number;
}

//   Get all sales (paginated)
export const getSales = async (page: number = 1): Promise<PaginatedResponse<Sale>> => {
  const response = await api.get<PaginatedResponse<Sale>>('/api/sales', {
    params: { page },
  });
  return response.data;
};

//   Get sale by ID
export const getSaleById = async (id: number): Promise<Sale> => {
  const response = await api.get<Sale>(`/api/sales/${id}`);
  return response.data;
};
//   Get sale by ID or Telephone number
export const getSaleByIdorTele = async (q: string,page:number = 1): Promise<PaginatedResponse<Sale>> => {
  const response = await api.get<PaginatedResponse<Sale>>('/api/sales/search',{
    params: { q ,page},
  });
  return response.data;
};

//   Search sales by customer telephone
export const searchSalesByCustomer = async (customerTele: string, page: number = 1): Promise<Sale[]> => {
  const response = await api.get<Sale[]>('/api/sales/tele', {
    params: { customerTele, page },
  });
  return response.data;
};

//   Create a new sale
export const createSale = async (saleRequest: SaleRequestDTO): Promise<Sale> => {
  const response = await api.post<Sale>('/api/sales', saleRequest);
  return response.data;
};

//   Update payment for a sale
export const updateSalePayment = async (id: number, paid: number, balance: number): Promise<{ message: string }> => {
  const response = await api.put<{ message: string }>('/api/sales', null, {
    params: { id, paid, balance },
  });
  return response.data;
};
