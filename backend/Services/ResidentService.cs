using Npgsql;
using RecyclingPlatform.Api.Data;
using RecyclingPlatform.Api.Models;

namespace RecyclingPlatform.Api.Services;

public class ResidentService
{
    private readonly NpgsqlConnectionFactory _connectionFactory;

    public ResidentService(NpgsqlConnectionFactory connectionFactory)
    {
        _connectionFactory = connectionFactory;
    }

    public async Task AddWasteAsync(long phone, string wasteType, int amount)
    {
        await using var conn = await _connectionFactory.CreateOpenConnectionAsync();
        await using var cmd = new NpgsqlCommand(
            "SELECT add_waste_for_neighborhood_resident(@phone, @wasteType, @amount)", conn);
        cmd.Parameters.AddWithValue("phone", phone);
        cmd.Parameters.AddWithValue("wasteType", wasteType);
        cmd.Parameters.AddWithValue("amount", amount);
        await cmd.ExecuteNonQueryAsync();
    }

    public async Task<List<ResidentReportItem>> GetReportAsync(long phone)
    {
        await using var conn = await _connectionFactory.CreateOpenConnectionAsync();
        await using var cmd = new NpgsqlCommand("SELECT * FROM neighborhood_resident_report(@phone)", conn);
        cmd.Parameters.AddWithValue("phone", phone);

        var results = new List<ResidentReportItem>();
        await using var reader = await cmd.ExecuteReaderAsync();
        while (await reader.ReadAsync())
        {
            results.Add(new ResidentReportItem(
                reader.GetString(reader.GetOrdinal("product")),
                reader.GetInt32(reader.GetOrdinal("amount"))));
        }

        return results;
    }
}
