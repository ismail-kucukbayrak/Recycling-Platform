using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;

namespace RecyclingPlatform.Api.Services;

public static class ClaimsPrincipalExtensions
{
    public static long GetPhone(this ClaimsPrincipal user)
    {
        var value = user.FindFirst(JwtRegisteredClaimNames.Sub)?.Value
            ?? user.FindFirst(ClaimTypes.NameIdentifier)?.Value
            ?? throw new InvalidOperationException("Token does not contain a subject claim.");

        return long.Parse(value);
    }

    public static string GetUsername(this ClaimsPrincipal user)
    {
        return user.FindFirst(JwtRegisteredClaimNames.Sub)?.Value
            ?? user.FindFirst(ClaimTypes.NameIdentifier)?.Value
            ?? throw new InvalidOperationException("Token does not contain a subject claim.");
    }
}
