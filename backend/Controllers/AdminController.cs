using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using RecyclingPlatform.Api.Services;

namespace RecyclingPlatform.Api.Controllers;

[ApiController]
[Route("api/admin")]
[Authorize(Roles = "admin")]
public class AdminController : ControllerBase
{
    private readonly AdminService _adminService;

    public AdminController(AdminService adminService)
    {
        _adminService = adminService;
    }

    [HttpGet("warehouse")]
    public async Task<IActionResult> GetWarehouseRecords()
    {
        return Ok(await _adminService.GetWarehouseRecordsAsync());
    }

    [HttpGet("appointments/today")]
    public async Task<IActionResult> GetTodaysAppointments()
    {
        return Ok(await _adminService.GetTodaysAppointmentsAsync());
    }

    [HttpGet("reports/monthly-waste")]
    public async Task<IActionResult> GetMonthlyTotalWasteReport()
    {
        return Ok(await _adminService.GetMonthlyTotalWasteReportAsync());
    }

    [HttpGet("reports/contributors")]
    public async Task<IActionResult> GetResidentsWhoAddedWasteThisMonth()
    {
        return Ok(await _adminService.GetResidentsWhoAddedWasteThisMonthAsync());
    }

    [HttpGet("residents")]
    public async Task<IActionResult> GetResidentByName([FromQuery] string name)
    {
        if (string.IsNullOrWhiteSpace(name))
        {
            return BadRequest(new { message = "Name query parameter is required." });
        }

        return Ok(await _adminService.GetResidentByNameAsync(name));
    }

    [HttpDelete("residents/{phone:long}")]
    public async Task<IActionResult> DeleteResident(long phone)
    {
        try
        {
            await _adminService.DeleteResidentAsync(phone);
            return Ok(new { message = "Resident removed from the system." });
        }
        catch (Exception ex)
        {
            return BadRequest(new { message = ex.Message });
        }
    }

    [HttpPost("reset-monthly-waste")]
    public async Task<IActionResult> ResetMonthlyWaste()
    {
        await _adminService.ResetMonthlyWasteAsync();
        return Ok(new { message = "Monthly waste records have been reset." });
    }
}
