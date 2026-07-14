namespace RecyclingPlatform.Api.Models;

public record WarehouseRecord(int WasteId, string WasteName, int AmountKg);

public record CreateAppointmentRequest(int WasteId, int Amount, DateTime Time);

public record AppointmentRecord(
    int Id,
    long Phone,
    string CompanyName,
    int WasteId,
    string WasteName,
    int AmountKg,
    DateTime Time);

public record MonthlyWasteReport(long TotalPlasticKg, long TotalGlassKg, long TotalElectronicKg);
