import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { adminApi } from "../../api/admin";
import { ApiError } from "../../api/client";
import { useAuth } from "../../context/AuthContext";
import type { WarehouseRecord } from "../../api/types";

export function AdminWarehousePage() {
  const { auth } = useAuth();
  const [records, setRecords] = useState<WarehouseRecord[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    adminApi
      .getWarehouseRecords(auth!.token)
      .then(setRecords)
      .catch((err) => setError(err instanceof ApiError ? err.message : "Could not load warehouse."))
      .finally(() => setLoading(false));
  }, [auth]);

  return (
    <div className="page">
      <div className="toolbar card-wide">
        <h2>Warehouse</h2>
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
                <th>Waste ID</th>
                <th>Waste Type</th>
                <th>Amount (kg)</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan={3}>Loading...</td>
                </tr>
              ) : (
                records.map((record) => (
                  <tr key={record.wasteId}>
                    <td>{record.wasteId}</td>
                    <td>{record.wasteName}</td>
                    <td>{record.amountKg}</td>
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
