import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface User {
  sub: string; // email
  role: string;
  userId: number;
}

interface AuthState {
  token: string | null;
  user: User | null;
  setToken: (token: string) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      token: null,
      user: null,
      setToken: (token) => {
        // Decode token to get user info (simplified)
        // In real app, use jwt-decode
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(window.atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));
        const decoded = JSON.parse(jsonPayload);
        const user = {
            sub: decoded.sub,
            role: decoded.role || 'USER',
            userId: decoded.userId
        };
        
        set({ token, user });
      },
      logout: () => set({ token: null, user: null }),
    }),
    {
      name: 'auth-storage',
    }
  )
);
