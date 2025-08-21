
import { LoginDto, LoginResponse } from "@/types/LoginTypes";
import api from "./api";
import { Staff } from "@/types/Staff";




// Login (get JWT)
export const apiLogin = async (
    loginDto: LoginDto
): Promise<LoginResponse> => {
    const response = await api.post<LoginResponse>("/api/login", loginDto); 
    return response.data;
};


// Create staff (Register new staff)
export const createStaff = async (staff: Staff): Promise<Staff> => {
    const response = await api.post<Staff>("/api/staff", staff);
    return response.data;
};

//Fetch user
export const getUser = async (): Promise<Staff> => {
    const response = await api.get<Staff>(`/api/staff/user` );
    return response.data;
};

 

export const getAllStaff = async ( ): Promise<Staff[]> => {
    const response = await api.get<Staff[]>('/api/staff' );
    return response.data;
}

export const getUserByUsername = async (username: string ): Promise<Staff> => {
    const response = await api.get<Staff>(`/api/staff?username=${username}` );
    return response.data;
}

export const deleteStaff = async (id: number ): Promise<void> => {
    await api.delete(`/api/staff/${id}`);
}

export const updateStaff = async (id:number, staff: Staff ): Promise<Staff> => {
    const response = await api.put<Staff>(`/api/staff/${id}` );
    return response.data;
}