import { useState, type FormEvent } from "react";
import { Link } from "react-router-dom";
import { residentApi } from "../../api/resident";
import { ApiError } from "../../api/client";
import { useAuth } from "../../context/AuthContext";

const WASTE_TYPES = ["plastic", "glass", "electronic"];

export function AddWastePage() {
  const { auth } = useAuth();
  const [wasteType, setWasteType] = useState(WASTE_TYPES[0]);
  const [amount, setAmount] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccess(false);
    setLoading(true);
    try {
      await residentApi.addWaste(auth!.token, wasteType, Number(amount));
      setSuccess(true);
      setAmount("");
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Could not add waste.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page">
      <div className="card">
        <h2>Add Waste</h2>
        <form className="stack" onSubmit={handleSubmit}>
          {error && <div className="error-banner">{error}</div>}
          {success && <div className="success-banner">Waste added.</div>}
          <div className="field">
            <label htmlFor="wasteType">Waste Type</label>
            <select id="wasteType" value={wasteType} onChange={(e) => setWasteType(e.target.value)}>
              {WASTE_TYPES.map((type) => (
                <option key={type} value={type}>
                  {type}
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
          <button className="btn" type="submit" disabled={loading}>
            {loading ? "Adding..." : "Add"}
          </button>
          <Link to="/resident" className="btn-link">
            Back
          </Link>
        </form>
      </div>
    </div>
  );
}
