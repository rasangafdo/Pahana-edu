export interface LoginResponse {
  success: boolean;
  token?: string;
  userId?: number;
  username?: string;
  role?: "MANAGER" | "CASHIER";
  message?: string;
}

export interface LoginDto {
  username: string;
  password: string;
}
