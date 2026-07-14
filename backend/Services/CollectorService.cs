using Npgsql;
using NpgsqlTypes;
using RecyclingPlatform.Api.Data;
using RecyclingPlatform.Api.Models;

namespace RecyclingPlatform.Api.Services;

public class CollectorService
{
    private readonly NpgsqlConnectionFactory _connectionFactory;

    public CollectorService(NpgsqlConnectionFactory connectionFactory)
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

    public async Task CreateAppointmentAsync(long phone, int wasteId, int amount, DateTime time)
    {
        await using var conn = await _connectionFactory.CreateOpenConnectionAsync();
        await using var cmd = new NpgsqlCommand(
            "SELECT create_appointment(@phone, @wasteId, @amount, @time)", conn);
        cmd.Parameters.AddWithValue("phone", phone);
        cmd.Parameters.AddWithValue("wasteId", wasteId);
        cmd.Parameters.AddWithValue("amount", amount);
        cmd.Parameters.Add(new NpgsqlParameter("time", NpgsqlDbType.Timestamp) { Value = DateTime.SpecifyKind(time, DateTimeKind.Unspecified) });

        try
        {
            await cmd.ExecuteNonQueryAsync();
        }
        catch (PostgresException ex)
        {
            throw new InvalidOperationException(ex.MessageText, ex);
        }
    }
}
