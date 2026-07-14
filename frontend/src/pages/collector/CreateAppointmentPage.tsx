import { useEffect, useState, type FormEvent } from "react";
import { Link } from "react-router-dom";
import { collectorApi } from "../../api/collector";
import { ApiError } from "../../api/client";
import { useAuth } from "../../context/AuthContext";
import type { WarehouseRecord } from "../../api/types";

export function CreateAppointmentPage() {
  const { auth } = useAuth();
  const [records, setRecords] = useState<WarehouseRecord[]>([]);
  const [wasteId, setWasteId] = useState<number | null>(null);
  const [amount, setAmount] = useState("");
  const [date, setDate] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);
  const [loading, setLoading] = useState(false);
  const [loadingRecords, setLoadingRecords] = useState(true);

  useEffect(() => {
    collectorApi
      .getWarehouseRecords(auth!.token)
      .then((data) => {
        setRecords(data);
        if (data.length > 0) setWasteId(data[0].wasteId);
      })
      .catch((err) => setError(err instanceof ApiError ? err.message : "Could not load warehouse."))
      .finally(() => setLoadingRecords(false));
  }, [auth]);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    if (wasteId === null) return;

    setError(null);
    setSuccess(false);
    setLoading(true);
    try {
      const isoTime = new Date(`${date}T00:00:00`).toISOString();
      await collectorApi.createAppointment(auth!.token, wasteId, Number(amount), isoTime);
      setSuccess(true);
      setAmount("");
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Could not create appointment.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page">
      <div className="toolbar card-wide">
        <h2>Warehouse Stock</h2>
        <Link to="/collector" className="btn-link">
          Back
        </Link>
      </div>

      <div className="table-wrapper card-wide" style={{ marginBottom: 24 }}>
        <table>
          <thead>
            <tr>
              <th>Waste ID</th>
              <th>Waste Type</th>
              <th>Amount (kg)</th>
            </tr>
          </thead>
          <tbody>
            {loadingRecords ? (
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

      <div className="card">
        <h2>Create Appointment</h2>
        <form className="stack" onSubmit={handleSubmit}>
          {error && <div className="error-banner">{error}</div>}
          {success && <div className="success-banner">Appointment created.</div>}
          <div className="field">
            <label htmlFor="wasteType">Waste Type</label>
            <select
              id="wasteType"
              value={wasteId ?? ""}
              onChange={(e) => setWasteId(Number(e.target.value))}
            >
              {records.map((record) => (
                <option key={record.wasteId} value={record.wasteId}>
                  {record.wasteName}
                </option>
              ))}
            </select>
          </div>
          <div className="field">
            <label htmlFor="amount">Amount (kg)</label>
            <input
              id="amount"
              type="number"
              min="1"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              required
            />
          </div>
          <div className="field">
            <label htmlFor="date">Date</label>
            <input
              id="date"
              type="date"
              value={date}
              onChange={(e) => setDate(e.target.value)}
              required
            />
          </div>
          <button className="btn" type="submit" disabled={loading || wasteId === null}>
            {loading ? "Creating..." : "Create"}
          </button>
        </form>
      </div>
    </div>
  );
}
