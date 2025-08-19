// Mock API service to simulate backend calls with sample data

export interface Customer {
  id: number;
  name: string;
  telephone: string;
  address: string;
  role: string;
  isActive: boolean;
  lastUpdated: string;
}

export interface Item {
  itemId: number;
  name: string;
  unitPrice: number;
  stockAvailable: number;
  discount: number;
  qtyToAllowDiscount: number;
  categoryId: number;
  lastUpdatedAt: string;
}

export interface Category {
  categoryId: number;
  name: string;
  lastUpdatedAt: string;
}

export interface Staff {
  id: number;
  name: string;
  telephone: string;
  address: string;
  username: string;
  email: string;
  role: string;
  isActive: boolean;
  lastUpdated: string;
}

export interface Sale {
  saleId: number;
  customerId: number;
  customerName: string;
  totalAmount: number;
  totalDiscount: number;
  subTotal: number;
  paid: number;
  balance: number;
  saleDate: string;
  saleTime: string;
  lastUpdatedAt: string;
  saleItems: SaleItem[];
}

export interface SaleItem {
  saleItemId: number;
  item: Item;
  qty: number;
  discountAmount: number;
  itemTotal: number;
}

// Sample data
let customers: Customer[] = [
  {
    id: 1,
    name: "Nimal Perera",
    telephone: "0771234567",
    address: "123 Galle Road, Colombo 03",
    role: "CUSTOMER",
    isActive: true,
    lastUpdated: "2025-08-19T10:30:00"
  },
  {
    id: 2,
    name: "Kamala Silva",
    telephone: "0712345678",
    address: "456 Kandy Road, Colombo 07",
    role: "CUSTOMER",
    isActive: true,
    lastUpdated: "2025-08-19T11:15:00"
  },
  {
    id: 3,
    name: "Sunil Fernando",
    telephone: "0723456789",
    address: "789 High Level Road, Nugegoda",
    role: "CUSTOMER",
    isActive: true,
    lastUpdated: "2025-08-19T09:45:00"
  }
];

let categories: Category[] = [
  {
    categoryId: 1,
    name: "Textbooks",
    lastUpdatedAt: "2025-08-19T08:00:00"
  },
  {
    categoryId: 2,
    name: "Stationery",
    lastUpdatedAt: "2025-08-19T08:00:00"
  },
  {
    categoryId: 3,
    name: "Reference Books",
    lastUpdatedAt: "2025-08-19T08:00:00"
  }
];

let items: Item[] = [
  {
    itemId: 1,
    name: "Grade 10 Mathematics",
    unitPrice: 750.00,
    stockAvailable: 25,
    discount: 50.00,
    qtyToAllowDiscount: 3,
    categoryId: 1,
    lastUpdatedAt: "2025-08-19T12:00:00"
  },
  {
    itemId: 2,
    name: "A4 Exercise Book",
    unitPrice: 120.00,
    stockAvailable: 100,
    discount: 10.00,
    qtyToAllowDiscount: 5,
    categoryId: 2,
    lastUpdatedAt: "2025-08-19T12:00:00"
  },
  {
    itemId: 3,
    name: "Oxford English Dictionary",
    unitPrice: 2500.00,
    stockAvailable: 8,
    discount: 200.00,
    qtyToAllowDiscount: 1,
    categoryId: 3,
    lastUpdatedAt: "2025-08-19T12:00:00"
  }
];

let staff: Staff[] = [
  {
    id: 1,
    name: "Priya Rajapaksha",
    telephone: "0771111111",
    address: "321 Temple Road, Colombo 10",
    username: "priya",
    email: "priya@pahanaedu.lk",
    role: "MANAGER",
    isActive: true,
    lastUpdated: "2025-08-19T08:00:00"
  }
];

let sales: Sale[] = [
  {
    saleId: 1,
    customerId: 1,
    customerName: "Nimal Perera",
    totalAmount: 2200.00,
    totalDiscount: 50.00,
    subTotal: 2250.00,
    paid: 2200.00,
    balance: 0.00,
    saleDate: "2025-08-19",
    saleTime: "10:30:00",
    lastUpdatedAt: "2025-08-19T10:30:00",
    saleItems: [
      {
        saleItemId: 1,
        item: items[0],
        qty: 3,
        discountAmount: 50.00,
        itemTotal: 2200.00
      }
    ]
  }
];

// Utility functions
const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

export const formatCurrency = (amount: number): string => {
  return `LKR ${amount.toLocaleString('en-LK', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
};

// Mock API functions
export const mockApi = {
  // Customer APIs
  async getCustomers(page: number = 1, limit: number = 10, search?: string) {
    await delay(500);
    let filtered = customers;
    
    if (search) {
      filtered = customers.filter(c => 
        c.name.toLowerCase().includes(search.toLowerCase()) ||
        c.telephone.includes(search)
      );
    }
    
    const total = filtered.length;
    const start = (page - 1) * limit;
    const end = start + limit;
    const data = filtered.slice(start, end);
    
    return {
      data,
      total,
      page,
      limit,
      totalPages: Math.ceil(total / limit)
    };
  },

  async createCustomer(customer: Omit<Customer, 'id' | 'lastUpdated'>) {
    await delay(300);
    const newCustomer: Customer = {
      ...customer,
      id: Math.max(...customers.map(c => c.id)) + 1,
      lastUpdated: new Date().toISOString()
    };
    customers.push(newCustomer);
    return newCustomer;
  },

  async updateCustomer(id: number, updates: Partial<Customer>) {
    await delay(300);
    const index = customers.findIndex(c => c.id === id);
    if (index === -1) throw new Error('Customer not found');
    
    customers[index] = { ...customers[index], ...updates, lastUpdated: new Date().toISOString() };
    return customers[index];
  },

  async deleteCustomer(id: number) {
    await delay(300);
    customers = customers.filter(c => c.id !== id);
    return { success: true };
  },

  // Item APIs
  async getItems(page: number = 1, limit: number = 10, search?: string, categoryId?: number) {
    await delay(500);
    let filtered = items;
    
    if (search) {
      filtered = filtered.filter(i => i.name.toLowerCase().includes(search.toLowerCase()));
    }
    
    if (categoryId) {
      filtered = filtered.filter(i => i.categoryId === categoryId);
    }
    
    const total = filtered.length;
    const start = (page - 1) * limit;
    const end = start + limit;
    const data = filtered.slice(start, end);
    
    return {
      data,
      total,
      page,
      limit,
      totalPages: Math.ceil(total / limit)
    };
  },

  async createItem(item: Omit<Item, 'itemId' | 'lastUpdatedAt'>) {
    await delay(300);
    const newItem: Item = {
      ...item,
      itemId: Math.max(...items.map(i => i.itemId)) + 1,
      lastUpdatedAt: new Date().toISOString()
    };
    items.push(newItem);
    return newItem;
  },

  async updateItem(id: number, updates: Partial<Item>) {
    await delay(300);
    const index = items.findIndex(i => i.itemId === id);
    if (index === -1) throw new Error('Item not found');
    
    items[index] = { ...items[index], ...updates, lastUpdatedAt: new Date().toISOString() };
    return items[index];
  },

  async deleteItem(id: number) {
    await delay(300);
    items = items.filter(i => i.itemId !== id);
    return { success: true };
  },

  // Category APIs
  async getCategories() {
    await delay(300);
    return categories;
  },

  async createCategory(name: string) {
    await delay(300);
    const newCategory: Category = {
      categoryId: Math.max(...categories.map(c => c.categoryId)) + 1,
      name,
      lastUpdatedAt: new Date().toISOString()
    };
    categories.push(newCategory);
    return newCategory;
  },

  // Staff APIs
  async getStaff(page: number = 1, limit: number = 10, search?: string) {
    await delay(500);
    let filtered = staff;
    
    if (search) {
      filtered = staff.filter(s => 
        s.name.toLowerCase().includes(search.toLowerCase()) ||
        s.username.toLowerCase().includes(search.toLowerCase())
      );
    }
    
    const total = filtered.length;
    const start = (page - 1) * limit;
    const end = start + limit;
    const data = filtered.slice(start, end);
    
    return {
      data,
      total,
      page,
      limit,
      totalPages: Math.ceil(total / limit)
    };
  },

  async createStaff(staffMember: Omit<Staff, 'id' | 'lastUpdated'>) {
    await delay(300);
    const newStaff: Staff = {
      ...staffMember,
      id: Math.max(...staff.map(s => s.id)) + 1,
      lastUpdated: new Date().toISOString()
    };
    staff.push(newStaff);
    return newStaff;
  },

  async updateStaff(id: number, updates: Partial<Staff>) {
    await delay(300);
    const index = staff.findIndex(s => s.id === id);
    if (index === -1) throw new Error('Staff not found');
    
    staff[index] = { ...staff[index], ...updates, lastUpdated: new Date().toISOString() };
    return staff[index];
  },

  async deleteStaff(id: number) {
    await delay(300);
    staff = staff.filter(s => s.id !== id);
    return { success: true };
  },

  // Sales APIs
  async getSales(page: number = 1, limit: number = 10, search?: string) {
    await delay(500);
    let filtered = sales;
    
    if (search) {
      filtered = sales.filter(s => 
        s.customerName.toLowerCase().includes(search.toLowerCase()) ||
        s.saleId.toString().includes(search)
      );
    }
    
    const total = filtered.length;
    const start = (page - 1) * limit;
    const end = start + limit;
    const data = filtered.slice(start, end);
    
    return {
      data,
      total,
      page,
      limit,
      totalPages: Math.ceil(total / limit)
    };
  },

  async createSale(saleData: any) {
    await delay(500);
    // Simulate sale creation logic
    const newSale: Sale = {
      saleId: Math.max(...sales.map(s => s.saleId)) + 1,
      customerId: saleData.customer.id || 1,
      customerName: saleData.customer.name,
      totalAmount: saleData.totalAmount,
      totalDiscount: saleData.totalDiscount,
      subTotal: saleData.subTotal,
      paid: 0,
      balance: saleData.totalAmount,
      saleDate: new Date().toISOString().split('T')[0],
      saleTime: new Date().toTimeString().split(' ')[0],
      lastUpdatedAt: new Date().toISOString(),
      saleItems: saleData.saleItems
    };
    sales.push(newSale);
    return newSale;
  }
};