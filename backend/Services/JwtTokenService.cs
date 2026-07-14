using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Text;
using Microsoft.IdentityModel.Tokens;

namespace RecyclingPlatform.Api.Services;

public class JwtTokenService
{
    private readonly IConfiguration _configuration;

    public JwtTokenService(IConfiguration configuration)
    {
        _configuration = configuration;
    }

    public string GenerateToken(string subject, string role, IDictionary<string, string>? extraClaims = null)
    {
        var jwtSection = _configuration.GetSection("Jwt");
        var key = jwtSection["Key"]!;
        var issuer = jwtSection["Issuer"]!;
        var audience = jwtSection["Audience"]!;
        var expiryMinutes = int.Parse(jwtSection["ExpiryMinutes"] ?? "120");

        var claims = new List<Claim>
        {
            new(JwtRegisteredClaimNames.Sub, subject),
            new(ClaimTypes.Role, role),
            new(JwtRegisteredClaimNames.Jti, Guid.NewGuid().ToString())
        };

        if (extraClaims is not null)
        {
            foreach (var (claimType, claimValue) in extraClaims)
            {
                claims.Add(new Claim(claimType, claimValue));
            }
        }

        var credentials = new SigningCredentials(
            new SymmetricSecurityKey(Encoding.UTF8.GetBytes(key)),
            SecurityAlgorithms.HmacSha256);

        var token = new JwtSecurityToken(
            issuer: issuer,
            audience: audience,
            claims: claims,
            expires: DateTime.UtcNow.AddMinutes(expiryMinutes),
            signingCredentials: credentials);

        return new JwtSecurityTokenHandler().WriteToken(token);
    }
}
