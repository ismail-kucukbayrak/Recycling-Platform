import { api } from "./client";
import type { AuthResponse } from "./types";

export const authApi = {
  residentLogin: (phone: number, password: string) =>
    api.post<AuthResponse>("/auth/resident/login", { phone, password }),

  residentRegister: (phone: number, password: string, name: string, surname: string) =>
    api.post<{ message: string }>("/auth/resident/register", { phone, password, name, surname }),

  collectorLogin: (phone: number, password: string) =>
    api.post<AuthResponse>("/auth/collector/login", { phone, password }),

  collectorRegister: (phone: number, password: string, name: string) =>
    api.post<{ message: string }>("/auth/collector/register", { phone, password, name }),

  adminLogin: (username: string, password: string) =>
    api.post<AuthResponse>("/auth/admin/login", { username, password }),
};
