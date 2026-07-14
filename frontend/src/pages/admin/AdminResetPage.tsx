import { useState } from "react";
import { Link } from "react-router-dom";
import { adminApi } from "../../api/admin";
import { ApiError } from "../../api/client";
import { useAuth } from "../../context/AuthContext";

export function AdminResetPage() {
  const { auth } = useAuth();
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleReset = async () => {
    if (!confirm("This will reset waste amounts for all residents to zero. Continue?")) return;

    setError(null);
    setSuccess(null);
    setLoading(true);
    try {
      const response = await adminApi.resetMonthlyWaste(auth!.token);
      setSuccess(response.message);
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Could not reset records.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page">
      <div className="card">
        <h2>Reset Monthly Waste Records</h2>
        <p style={{ marginBottom: 20 }}>
          This sets every neighborhood resident's plastic, glass, and electronic totals back to zero.
        </p>
        {error && <div className="error-banner" style={{ marginBottom: 12 }}>{error}</div>}
        {success && <div className="success-banner" style={{ marginBottom: 12 }}>{success}</div>}
        <div className="stack">
          <button className="btn btn-danger" onClick={handleReset} disabled={loading}>
            {loading ? "Resetting..." : "Reset Now"}
          </button>
          <Link to="/admin" className="btn-link">
            Back
          </Link>
        </div>
      </div>
    </div>
  );
}
