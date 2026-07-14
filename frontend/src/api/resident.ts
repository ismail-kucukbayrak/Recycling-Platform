import { api } from "./client";
import type { ResidentReportItem } from "./types";

export const residentApi = {
  addWaste: (token: string, wasteType: string, amount: number) =>
    api.post<{ message: string }>("/resident/waste", { wasteType, amount }, token),

  getReport: (token: string) => api.get<ResidentReportItem[]>("/resident/report", token),
};
