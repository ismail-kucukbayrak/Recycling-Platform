namespace RecyclingPlatform.Api.Models;

public record AddWasteRequest(string WasteType, int Amount);

public record ResidentReportItem(string Product, int Amount);

public record ResidentSummary(long Phone, string Name, string Surname);
