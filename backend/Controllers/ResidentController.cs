using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using RecyclingPlatform.Api.Models;
using RecyclingPlatform.Api.Services;

namespace RecyclingPlatform.Api.Controllers;

[ApiController]
[Route("api/resident")]
[Authorize(Roles = "resident")]
public class ResidentController : ControllerBase
{
    private readonly ResidentService _residentService;

    public ResidentController(ResidentService residentService)
    {
        _residentService = residentService;
    }

    [HttpPost("waste")]
    public async Task<IActionResult> AddWaste(AddWasteRequest request)
    {
        try
        {
            await _residentService.AddWasteAsync(User.GetPhone(), request.WasteType, request.Amount);
            return Ok(new { message = "Waste added." });
        }
        catch (Exception ex)
        {
            return BadRequest(new { message = ex.Message });
        }
    }

    [HttpGet("report")]
    public async Task<ActionResult<List<ResidentReportItem>>> GetReport()
    {
        var report = await _residentService.GetReportAsync(User.GetPhone());
        return Ok(report);
    }
}
