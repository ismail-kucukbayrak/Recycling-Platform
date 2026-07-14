using Microsoft.AspNetCore.Mvc;
using RecyclingPlatform.Api.Models;
using RecyclingPlatform.Api.Services;

namespace RecyclingPlatform.Api.Controllers;

[ApiController]
[Route("api/auth")]
public class AuthController : ControllerBase
{
    private readonly AuthService _authService;
    private readonly JwtTokenService _jwtTokenService;

    public AuthController(AuthService authService, JwtTokenService jwtTokenService)
    {
        _authService = authService;
        _jwtTokenService = jwtTokenService;
    }

    [HttpPost("resident/login")]
    public async Task<ActionResult<AuthResponse>> ResidentLogin(ResidentLoginRequest request)
    {
        var success = await _authService.ResidentLoginAsync(request.Phone, request.Password);
        if (!success)
        {
            return Unauthorized(new { message = "Incorrect phone or password." });
        }

        var token = _jwtTokenService.GenerateToken(request.Phone.ToString(), "resident");
        return Ok(new AuthResponse(token, "resident", request.Phone, null));
    }

    [HttpPost("resident/register")]
    public async Task<IActionResult> ResidentRegister(ResidentRegisterRequest request)
    {
        try
        {
            await _authService.ResidentRegisterAsync(request.Phone, request.Password, request.Name, request.Surname);
            return Ok(new { message = "Registration successful." });
        }
        catch (Exception ex)
        {
            return BadRequest(new { message = ex.Message });
        }
    }

    [HttpPost("collector/login")]
    public async Task<ActionResult<AuthResponse>> CollectorLogin(CollectorLoginRequest request)
    {
        var success = await _authService.CollectorLoginAsync(request.Phone, request.Password);
        if (!success)
        {
            return Unauthorized(new { message = "Incorrect phone or password." });
        }

        var token = _jwtTokenService.GenerateToken(request.Phone.ToString(), "collector");
        return Ok(new AuthResponse(token, "collector", request.Phone, null));
    }

    [HttpPost("collector/register")]
    public async Task<IActionResult> CollectorRegister(CollectorRegisterRequest request)
    {
        try
        {
            await _authService.CollectorRegisterAsync(request.Phone, request.Password, request.Name);
            return Ok(new { message = "Registration successful." });
        }
        catch (Exception ex)
        {
            return BadRequest(new { message = ex.Message });
        }
    }

    [HttpPost("admin/login")]
    public async Task<ActionResult<AuthResponse>> AdminLogin(AdminLoginRequest request)
    {
        var success = await _authService.AdminLoginAsync(request.Username, request.Password);
        if (!success)
        {
            return Unauthorized(new { message = "Incorrect username or password." });
        }

        var token = _jwtTokenService.GenerateToken(request.Username, "admin");
        return Ok(new AuthResponse(token, "admin", null, request.Username));
    }
}
