interface AuthContxtType {
    isAuthenticated: boolean 
    loading: boolean;
    login: (jwtToken: string) => void;
    logout: () => void; 
    jwtToken: string | null;
    isManager: boolean;
    isCashier: boolean;
    username: string;
}

export default AuthContxtType;