import { createContext, useContext, useMemo, useState, type ReactNode } from "react";
import type { AuthResponse } from "../api/types";

interface AuthState {
  token: string;
  role: AuthResponse["role"];
  phone: number | null;
  username: string | null;
}

interface AuthContextValue {
  auth: AuthState | null;
  login: (response: AuthResponse) => void;
  logout: () => void;
}

const STORAGE_KEY = "recycling-platform-auth";

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

function loadInitialAuth(): AuthState | null {
  const raw = localStorage.getItem(STORAGE_KEY);
  if (!raw) return null;
  try {
    return JSON.parse(raw) as AuthState;
  } catch {
    return null;
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [auth, setAuth] = useState<AuthState | null>(loadInitialAuth);

  const login = (response: AuthResponse) => {
    const state: AuthState = {
      token: response.token,
      role: response.role,
      phone: response.phone,
      username: response.username,
    };
    localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
    setAuth(state);
  };

  const logout = () => {
    localStorage.removeItem(STORAGE_KEY);
    setAuth(null);
  };

  const value = useMemo(() => ({ auth, login, logout }), [auth]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return ctx;
}
