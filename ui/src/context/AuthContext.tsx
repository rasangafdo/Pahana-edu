import { createContext, useContext, useEffect, useState } from "react";
import AuthContxtType from "../types/AuthContextType";
import AuthProviderPropsType from "../types/AuthProviderPropsType";
import axios from "axios";
import { getUser } from "@/services/authService";

export const AuthContxt = createContext<AuthContxtType>({
    isAuthenticated: false, 
    loading: true,
    login: () => { },
    logout: () => { }, 
    jwtToken: null,
    isManager: false,
    isCashier: false,
    username: ""
})

export function AuthProvider({ children }: AuthProviderPropsType) {
    const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
    const [jwtToken, setJwtToken] = useState<string | null>(null)
    const [loading, setLoading] = useState<boolean>(true); 
    const [isManager, setIsManager] = useState<boolean>(false)
    const [isCashier, setIsCashier] = useState<boolean>(false)
    const [username, setUsername] = useState<string>("")



    async function getUserInfo(token: string) {
        try { 
            const staffUser = await getUser();
            const roleName = staffUser.role
            setIsManager(roleName && roleName.toUpperCase() === "MANAGER")
            setIsCashier(roleName && roleName.toUpperCase() === "CASHIER")
            setUsername(staffUser.username)
            setIsAuthenticated(true)
            setJwtToken(token)
            setLoading(false)
        }
        catch (error) {
            setLoading(false)
        }
    }
    function login(jwtToken: string) {
        getUserInfo(jwtToken)
        localStorage.setItem('jwtToken', jwtToken)
    }

    function logout() {
        setIsAuthenticated(false)
        setJwtToken(null)
        localStorage.removeItem('jwtToken')
    }

    useEffect(() => {
        const storedToken = localStorage.getItem('jwtToken')
        if (storedToken) {
            getUserInfo(storedToken)
        } else {
            setLoading(false)
        }
    }, [])

    return (
        <AuthContxt.Provider value={{ isAuthenticated, loading, login, logout, jwtToken, isManager, isCashier, username }}>
            {children}
        </AuthContxt.Provider>
    )
}

export function useAuth() {
    return useContext(AuthContxt)
}