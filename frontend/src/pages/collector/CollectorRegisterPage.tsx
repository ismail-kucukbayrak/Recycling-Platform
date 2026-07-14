import { useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { authApi } from "../../api/auth";
import { ApiError } from "../../api/client";

export function CollectorRegisterPage() {
  const [phone, setPhone] = useState("");
  const [password, setPassword] = useState("");
  const [name, setName] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      await authApi.collectorRegister(Number(phone), password, name);
      setSuccess(true);
      setTimeout(() => navigate("/collector/login"), 1200);
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Registration failed.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page">
      <div className="card">
        <h2>Collector Company Registration</h2>
        <form className="stack" onSubmit={handleSubmit}>
          {error && <div className="error-banner">{error}</div>}
          {success && <div className="success-banner">Registration successful. Redirecting...</div>}
          <div className="field">
            <label htmlFor="phone">Phone</label>
            <input id="phone" type="tel" value={phone} onChange={(e) => setPhone(e.target.value)} required />
          </div>
          <div className="field">
            <label htmlFor="name">Company Name</label>
            <input id="name" type="text" value={name} onChange={(e) => setName(e.target.value)} required />
          </div>
          <div className="field">
            <label htmlFor="password">Password</label>
            <input
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>
          <button className="btn" type="submit" disabled={loading}>
            {loading ? "Registering..." : "Register"}
          </button>
          <Link to="/collector/login" className="btn-link">
            Already have an account? Log in
          </Link>
        </form>
      </div>
    </div>
  );
}
