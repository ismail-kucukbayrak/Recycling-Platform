import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { adminApi } from "../../api/admin";
import { ApiError } from "../../api/client";
import { useAuth } from "../../context/AuthContext";
import type { MonthlyWasteReport } from "../../api/types";

export function AdminMonthlyWastePage() {
  const { auth } = useAuth();
  const [report, setReport] = useState<MonthlyWasteReport | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    adminApi
      .getMonthlyWasteReport(auth!.token)
      .then(setReport)
      .catch((err) => setError(err instanceof ApiError ? err.message : "Could not load report."))
      .finally(() => setLoading(false));
  }, [auth]);

  return (
    <div className="page">
      <div className="toolbar card-wide">
        <h2>Total Waste Added This Month</h2>
        <Link to="/admin" className="btn-link">
          Back
        </Link>
      </div>

      {error && <div className="error-banner">{error}</div>}

      {!loading && !error && report && (
        <div className="stat-grid card-wide">
          <div className="stat-tile">
            <div className="value">{report.totalPlasticKg}</div>
            <div className="label">Plastic (kg)</div>
          </div>
          <div className="stat-tile">
            <div className="value">{report.totalGlassKg}</div>
            <div className="label">Glass (kg)</div>
          </div>
          <div className="stat-tile">
            <div className="value">{report.totalElectronicKg}</div>
            <div className="label">Electronic (kg)</div>
          </div>
        </div>
      )}
    </div>
  );
}
