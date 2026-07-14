import { api } from "./client";
import type { WarehouseRecord } from "./types";

export const collectorApi = {
  getWarehouseRecords: (token: string) => api.get<WarehouseRecord[]>("/collector/warehouse", token),

  createAppointment: (token: string, wasteId: number, amount: number, time: string) =>
    api.post<{ message: string }>("/collector/appointments", { wasteId, amount, time }, token),
};
