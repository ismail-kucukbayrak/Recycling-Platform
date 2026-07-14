import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { adminApi } from "../../api/admin";
import { ApiError } from "../../api/client";
import { useAuth } from "../../context/AuthContext";
import type { AppointmentRecord } from "../../api/types";

export function AdminAppointmentsPage() {
  const { auth } = useAuth();
  const [rows, setRows] = useState<AppointmentRecord[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    adminApi
      .getTodaysAppointments(auth!.token)
      .then(setRows)
      .catch((err) => setError(err instanceof ApiError ? err.message : "Could not load appointments."))
      .finally(() => setLoading(false));
  }, [auth]);

  return (
    <div className="page">
      <div className="toolbar card-wide">
        <h2>Today &amp; Future Appointments</h2>
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
                <th>ID</th>
                <th>Phone</th>
                <th>Company</th>
                <th>Waste ID</th>
                <th>Waste</th>
                <th>Amount (kg)</th>
                <th>Time</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan={7}>Loading...</td>
                </tr>
              ) : (
                rows.map((row) => (
                  <tr key={row.id}>
                    <td>{row.id}</td>
                    <td>{row.phone}</td>
                    <td>{row.companyName}</td>
                    <td>{row.wasteId}</td>
                    <td>{row.wasteName}</td>
                    <td>{row.amountKg}</td>
                    <td>{new Date(row.time).toLocaleString()}</td>
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
