using Npgsql;
using RecyclingPlatform.Api.Data;
using RecyclingPlatform.Api.Models;

namespace RecyclingPlatform.Api.Services;

public class AdminService
{
    private readonly NpgsqlConnectionFactory _connectionFactory;

    public AdminService(NpgsqlConnectionFactory connectionFactory)
    {
        _connectionFactory = connectionFactory;
    }

    public async Task<List<WarehouseRecord>> GetWarehouseRecordsAsync()
    {
        await using var conn = await _connectionFactory.CreateOpenConnectionAsync();
        await using var cmd = new NpgsqlCommand("SELECT * FROM get_warehouse_records()", conn);

        var results = new List<WarehouseRecord>();
        await using var reader = await cmd.ExecuteReaderAsync();
        while (await reader.ReadAsync())
        {
            results.Add(new WarehouseRecord(
                reader.GetInt32(reader.GetOrdinal("waste_id")),
                reader.GetString(reader.GetOrdinal("waste_name")),
                reader.GetInt32(reader.GetOrdinal("amount(kg)"))));
        }

        return results;
    }

    public async Task<List<AppointmentRecord>> GetTodaysAppointmentsAsync()
    {
        await using var conn = await _connectionFactory.CreateOpenConnectionAsync();
        await using var cmd = new NpgsqlCommand("SELECT * FROM todays_appointments", conn);

        var results = new List<AppointmentRecord>();
        await using var reader = await cmd.ExecuteReaderAsync();
        while (await reader.ReadAsync())
        {
            results.Add(new AppointmentRecord(
                reader.GetInt32(reader.GetOrdinal("id")),
                reader.GetInt64(reader.GetOrdinal("phone")),
                reader.GetString(reader.GetOrdinal("company_name")),
                reader.GetInt32(reader.GetOrdinal("waste_id")),
                reader.GetString(reader.GetOrdinal("waste_name")),
                reader.GetInt32(reader.GetOrdinal("amount(kg)")),
                reader.GetDateTime(reader.GetOrdinal("time"))));
        }

        return results;
    }

    public async Task<MonthlyWasteReport> GetMonthlyTotalWasteReportAsync()
    {
        await using var conn = await _connectionFactory.CreateOpenConnectionAsync();
        await using var cmd = new NpgsqlCommand("SELECT * FROM monthly_total_waste_report()", conn);

        await using var reader = await cmd.ExecuteReaderAsync();
        if (await reader.ReadAsync())
        {
            return new MonthlyWasteReport(
                reader.IsDBNull(reader.GetOrdinal("total_plastic(kg)")) ? 0 : reader.GetInt64(reader.GetOrdinal("total_plastic(kg)")),
                reader.IsDBNull(reader.GetOrdinal("total_glass(kg)")) ? 0 : reader.GetInt64(reader.GetOrdinal("total_glass(kg)")),
                reader.IsDBNull(reader.GetOrdinal("total_electronic(kg)")) ? 0 : reader.GetInt64(reader.GetOrdinal("total_electronic(kg)")));
        }

        return new MonthlyWasteReport(0, 0, 0);
    }

    public async Task<List<ResidentSummary>> GetResidentsWhoAddedWasteThisMonthAsync()
    {
        await using var conn = await _connectionFactory.CreateOpenConnectionAsync();
        await using var cmd = new NpgsqlCommand(
            "SELECT * FROM neighborhood_residents_who_added_waste_this_month()", conn);

        var results = new List<ResidentSummary>();
        await using var reader = await cmd.ExecuteReaderAsync();
        while (await reader.ReadAsync())
        {
            results.Add(new ResidentSummary(
                reader.GetInt64(reader.GetOrdinal("phone")),
                reader.GetString(reader.GetOrdinal("name")),
                reader.GetString(reader.GetOrdinal("surname"))));
        }

        return results;
    }

    public async Task<List<ResidentSummary>> GetResidentByNameAsync(string name)
    {
        await using var conn = await _connectionFactory.CreateOpenConnectionAsync();
        await using var cmd = new NpgsqlCommand(
            "SELECT * FROM get_neighborhood_resident_by_name(@name)", conn);
        cmd.Parameters.AddWithValue("name", name);

        var results = new List<ResidentSummary>();
        await using var reader = await cmd.ExecuteReaderAsync();
        while (await reader.ReadAsync())
        {
            results.Add(new ResidentSummary(
                reader.GetInt64(reader.GetOrdinal("phone")),
                reader.GetString(reader.GetOrdinal("name")),
                reader.GetString(reader.GetOrdinal("surname"))));
        }

        return results;
    }

    public async Task DeleteResidentAsync(long phone)
    {
        await using var conn = await _connectionFactory.CreateOpenConnectionAsync();
        await using var cmd = new NpgsqlCommand("SELECT delete_neighborhood_resident(@phone)", conn);
        cmd.Parameters.AddWithValue("phone", phone);
        await cmd.ExecuteNonQueryAsync();
    }

    public async Task ResetMonthlyWasteAsync()
    {
        await using var conn = await _connectionFactory.CreateOpenConnectionAsync();
        await using var cmd = new NpgsqlCommand("SELECT reset_monthly_waste()", conn);
        await cmd.ExecuteNonQueryAsync();
    }
}
