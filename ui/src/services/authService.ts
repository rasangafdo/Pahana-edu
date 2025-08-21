
import { LoginDto, LoginResponse } from "@/types/LoginTypes";
import api from "./api";
import { Staff } from "@/types/Staff";




// Login (get JWT)
export const apiLogin = async (
    loginDto: LoginDto
): Promise<LoginResponse> => {
    const response = await api.post<LoginResponse>("/api/login", loginDto);
    if (response.data.token) {
        localStorage.setItem("token", response.data.token);
        localStorage.setItem("role", response.data.role ?? "");
        localStorage.setItem("username", response.data.username ?? "");
    }
    return response.data;
};


// Create staff (Register new staff)
export const createStaff = async (staff: Staff, token: string): Promise<Staff> => {
    const response = await api.post<Staff>("/api/staff", staff, {
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });
    return response.data;
};

//Fetch user
export const getUser = async (token: string): Promise<Staff> => {
    const response = await api.get<Staff>(`/api/staff/user`, {
        headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
    });
    return response.data;
};

 

export const getAllStaff = async (token: string): Promise<Staff[]> => {
    const response = await api.get<Staff[]>('/api/staff', {
        headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
    });
    return response.data;
}

export const getUserByUsername = async (username: string, token: string): Promise<Staff> => {
    const response = await api.get<Staff>(`/api/staff?username=${username}`, {
        headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
    });
    return response.data;
}

export const deleteStaff = async (id: number, token: string): Promise<void> => {
    await api.delete(`/api/staff/${id}`, {
        headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
    });
}