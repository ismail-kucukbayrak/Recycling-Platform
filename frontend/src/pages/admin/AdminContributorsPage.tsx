import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { adminApi } from "../../api/admin";
import { ApiError } from "../../api/client";
import { useAuth } from "../../context/AuthContext";
import type { ResidentSummary } from "../../api/types";

export function AdminContributorsPage() {
  const { auth } = useAuth();
  const [rows, setRows] = useState<ResidentSummary[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    adminApi
      .getContributors(auth!.token)
      .then(setRows)
      .catch((err) => setError(err instanceof ApiError ? err.message : "Could not load list."))
      .finally(() => setLoading(false));
  }, [auth]);

  return (
    <div className="page">
      <div className="toolbar card-wide">
        <h2>Residents Who Added Waste This Month</h2>
        <Link to="/admin" className="btn-link">
          Back
        </Link>
      </div>

      {error && <div className="error-banner">{error}</div>}

      {!error && (
        <div className="table-wrapper card-wide">
          <table>
            <thead>
              <tr>
                <th>Phone</th>
                <th>Name</th>
                <th>Surname</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan={3}>Loading...</td>
                </tr>
              ) : (
                rows.map((row) => (
                  <tr key={row.phone}>
                    <td>{row.phone}</td>
                    <td>{row.name}</td>
                    <td>{row.surname}</td>
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
