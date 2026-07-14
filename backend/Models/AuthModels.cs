namespace RecyclingPlatform.Api.Models;

public record ResidentLoginRequest(long Phone, string Password);
public record ResidentRegisterRequest(long Phone, string Password, string Name, string Surname);

public record CollectorLoginRequest(long Phone, string Password);
public record CollectorRegisterRequest(long Phone, string Password, string Name);

public record AdminLoginRequest(string Username, string Password);

public record AuthResponse(string Token, string Role, long? Phone, string? Username);
