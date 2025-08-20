import api from './api';
import { Customer } from '../types/Customer';
import { PaginatedResponse } from '@/types/PaginatedResponse';

// Get all customers (paginated)
export const getCustomers = async (page: number = 1): Promise<PaginatedResponse<Customer>> => {
  const response = await api.get<PaginatedResponse<Customer>>('/api/customers', {
    params: { page },
  });
  return response.data;
};

// Search customers by name
export const searchCustomers = async (name: string, page: number = 1): Promise<Customer[]> => {
  const response = await api.get<Customer[]>('/api/customers/search', {
    params: { name, page },
  });
  return response.data;
};

// Get active customers
export const getActiveCustomers = async (page: number = 1): Promise<Customer[]> => {
  const response = await api.get<Customer[]>('/api/customers/active', {
    params: { page },
  });
  return response.data;
};

// Get customer by telephone
export const getCustomerByTelephone = async (number: string): Promise<Customer> => {
  const response = await api.get<Customer>('/api/customers/telephone', {
    params: { number },
  });
  return response.data;
};

// Get customer by ID
export const getCustomerById = async (id: number): Promise<Customer> => {
  const response = await api.get<Customer>(`/api/customers/${id}`);
  return response.data;
};

// Create a new customer
export const createCustomer = async (customer: Customer): Promise<Customer> => {
  const response = await api.post<Customer>('/api/customers', customer);
  return response.data;
};

// Update a customer
export const updateCustomer = async (customer: Customer): Promise<Customer> => {
  if (!customer.id) {
    throw new Error("Customer ID is required for update");
  }
  const response = await api.put<Customer>('/api/customers', customer);
  return response.data;
};
