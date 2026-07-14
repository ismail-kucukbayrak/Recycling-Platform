using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using RecyclingPlatform.Api.Models;
using RecyclingPlatform.Api.Services;

namespace RecyclingPlatform.Api.Controllers;

[ApiController]
[Route("api/collector")]
[Authorize(Roles = "collector")]
public class CollectorController : ControllerBase
{
    private readonly CollectorService _collectorService;

    public CollectorController(CollectorService collectorService)
    {
        _collectorService = collectorService;
    }

    [HttpGet("warehouse")]
    public async Task<ActionResult<List<WarehouseRecord>>> GetWarehouseRecords()
    {
        var records = await _collectorService.GetWarehouseRecordsAsync();
        return Ok(records);
    }

    [HttpPost("appointments")]
    public async Task<IActionResult> CreateAppointment(CreateAppointmentRequest request)
    {
        try
        {
            await _collectorService.CreateAppointmentAsync(
                User.GetPhone(), request.WasteId, request.Amount, request.Time);
            return Ok(new { message = "Appointment created." });
        }
        catch (Exception ex)
        {
            return BadRequest(new { message = ex.Message });
        }
    }
}
