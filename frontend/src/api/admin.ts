import { api } from "./client";
import type {
  AppointmentRecord,
  MonthlyWasteReport,
  ResidentSummary,
  WarehouseRecord,
} from "./types";

export const adminApi = {
  getWarehouseRecords: (token: string) => api.get<WarehouseRecord[]>("/admin/warehouse", token),

  getTodaysAppointments: (token: string) =>
    api.get<AppointmentRecord[]>("/admin/appointments/today", token),

  getMonthlyWasteReport: (token: string) =>
    api.get<MonthlyWasteReport>("/admin/reports/monthly-waste", token),

  getContributors: (token: string) =>
    api.get<ResidentSummary[]>("/admin/reports/contributors", token),

  findResidentByName: (token: string, name: string) =>
    api.get<ResidentSummary[]>(`/admin/residents?name=${encodeURIComponent(name)}`, token),

  deleteResident: (token: string, phone: number) =>
    api.del<{ message: string }>(`/admin/residents/${phone}`, token),

  resetMonthlyWaste: (token: string) =>
    api.post<{ message: string }>("/admin/reset-monthly-waste", undefined, token),
};
