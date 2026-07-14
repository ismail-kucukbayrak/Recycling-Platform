import { useState, type FormEvent } from "react";
import { Link } from "react-router-dom";
import { adminApi } from "../../api/admin";
import { ApiError } from "../../api/client";
import { useAuth } from "../../context/AuthContext";
import type { ResidentSummary } from "../../api/types";

export function AdminResidentsPage() {
  const { auth } = useAuth();
  const [name, setName] = useState("");
  const [rows, setRows] = useState<ResidentSummary[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [searched, setSearched] = useState(false);

  const handleSearch = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const results = await adminApi.findResidentByName(auth!.token, name.trim());
      setRows(results);
      setSearched(true);
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Search failed.");
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (phone: number) => {
    if (!confirm("Do you want to delete this neighborhood resident?")) return;

    try {
      await adminApi.deleteResident(auth!.token, phone);
      setRows((prev) => prev.filter((row) => row.phone !== phone));
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Could not delete resident.");
    }
  };

  return (
    <div className="page">
      <div className="toolbar card-wide">
        <h2>View Neighborhood Resident</h2>
        <Link to="/admin" className="btn-link">
          Back
        </Link>
      </div>

      <form className="stack card-wide" style={{ flexDirection: "row", marginBottom: 16 }} onSubmit={handleSearch}>
        <input
          type="text"
          placeholder="Resident name"
          value={name}
          onChange={(e) => setName(e.target.value)}
          required
          style={{ flex: 1, padding: "9px 12px", border: "1px solid var(--border)", borderRadius: 8 }}
        />
        <button className="btn" type="submit" disabled={loading}>
          {loading ? "Searching..." : "Search"}
        </button>
      </form>

      {error && <div className="error-banner">{error}</div>}

      {!error && searched && (
        <div className="table-wrapper card-wide">
          <table>
            <thead>
              <tr>
                <th>Phone</th>
                <th>Name</th>
                <th>Surname</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {rows.length === 0 ? (
                <tr>
                  <td colSpan={4}>No residents found.</td>
                </tr>
              ) : (
                rows.map((row) => (
                  <tr key={row.phone}>
                    <td>{row.phone}</td>
                    <td>{row.name}</td>
                    <td>{row.surname}</td>
                    <td>
                      <button className="btn btn-danger" onClick={() => handleDelete(row.phone)}>
                        Delete
                      </button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
