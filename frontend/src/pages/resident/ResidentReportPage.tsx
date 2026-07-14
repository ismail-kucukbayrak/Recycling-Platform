import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { residentApi } from "../../api/resident";
import { ApiError } from "../../api/client";
import { useAuth } from "../../context/AuthContext";
import type { ResidentReportItem } from "../../api/types";

export function ResidentReportPage() {
  const { auth } = useAuth();
  const [rows, setRows] = useState<ResidentReportItem[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    residentApi
      .getReport(auth!.token)
      .then(setRows)
      .catch((err) => setError(err instanceof ApiError ? err.message : "Could not load report."))
      .finally(() => setLoading(false));
  }, [auth]);

  return (
    <div className="page">
      <div className="toolbar" style={{ maxWidth: 480 }}>
        <h2>My Report</h2>
        <Link to="/resident" className="btn-link">
          Back
        </Link>
      </div>

      {error && <div className="error-banner">{error}</div>}

      {!error && (
        <div className="table-wrapper" style={{ maxWidth: 480 }}>
          <table>
            <thead>
              <tr>
                <th>Product</th>
                <th>Amount (kg)</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan={2}>Loading...</td>
                </tr>
              ) : (
                rows.map((row) => (
                  <tr key={row.product}>
                    <td>{row.product}</td>
                    <td>{row.amount}</td>
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
