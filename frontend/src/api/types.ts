export interface AuthResponse {
  token: string;
  role: "resident" | "collector" | "admin";
  phone: number | null;
  username: string | null;
}

export interface ResidentReportItem {
  product: string;
  amount: number;
}

export interface WarehouseRecord {
  wasteId: number;
  wasteName: string;
  amountKg: number;
}

export interface AppointmentRecord {
  id: number;
  phone: number;
  companyName: string;
  wasteId: number;
  wasteName: string;
  amountKg: number;
  time: string;
}

export interface MonthlyWasteReport {
  totalPlasticKg: number;
  totalGlassKg: number;
  totalElectronicKg: number;
}

export interface ResidentSummary {
  phone: number;
  name: string;
  surname: string;
}
