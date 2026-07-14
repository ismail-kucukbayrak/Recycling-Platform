using Npgsql;
using RecyclingPlatform.Api.Data;

namespace RecyclingPlatform.Api.Services;

public class AuthService
{
    private readonly NpgsqlConnectionFactory _connectionFactory;

    public AuthService(NpgsqlConnectionFactory connectionFactory)
    {
        _connectionFactory = connectionFactory;
    }

    public async Task<bool> ResidentLoginAsync(long phone, string password)
    {
        await using var conn = await _connectionFactory.CreateOpenConnectionAsync();
        await using var cmd = new NpgsqlCommand("SELECT neighborhood_resident_login(@phone, @password)", conn);
        cmd.Parameters.AddWithValue("phone", phone);
        cmd.Parameters.AddWithValue("password", password);
        return (bool)(await cmd.ExecuteScalarAsync())!;
    }

    public async Task ResidentRegisterAsync(long phone, string password, string name, string surname)
    {
        await using var conn = await _connectionFactory.CreateOpenConnectionAsync();
        await using var cmd = new NpgsqlCommand(
            "SELECT register_neighborhood_resident(@phone, @password, @name, @surname)", conn);
        cmd.Parameters.AddWithValue("phone", phone);
        cmd.Parameters.AddWithValue("password", password);
        cmd.Parameters.AddWithValue("name", name);
        cmd.Parameters.AddWithValue("surname", surname);
        await cmd.ExecuteNonQueryAsync();
    }

    public async Task<bool> CollectorLoginAsync(long phone, string password)
    {
        await using var conn = await _connectionFactory.CreateOpenConnectionAsync();
        await using var cmd = new NpgsqlCommand("SELECT collector_company_login(@phone, @password)", conn);
        cmd.Parameters.AddWithValue("phone", phone);
        cmd.Parameters.AddWithValue("password", password);
        return (bool)(await cmd.ExecuteScalarAsync())!;
    }

    public async Task CollectorRegisterAsync(long phone, string password, string name)
    {
        await using var conn = await _connectionFactory.CreateOpenConnectionAsync();
        await using var cmd = new NpgsqlCommand(
            "SELECT register_collector_company(@phone, @password, @name)", conn);
        cmd.Parameters.AddWithValue("phone", phone);
        cmd.Parameters.AddWithValue("password", password);
        cmd.Parameters.AddWithValue("name", name);
        await cmd.ExecuteNonQueryAsync();
    }

    public async Task<bool> AdminLoginAsync(string username, string password)
    {
        await using var conn = await _connectionFactory.CreateOpenConnectionAsync();
        await using var cmd = new NpgsqlCommand("SELECT admin_login(@username, @password)", conn);
        cmd.Parameters.AddWithValue("username", username);
        cmd.Parameters.AddWithValue("password", password);
        return (bool)(await cmd.ExecuteScalarAsync())!;
    }
}
