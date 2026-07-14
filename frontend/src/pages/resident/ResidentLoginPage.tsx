import { useState, type FormEvent } from "react";
import { Link, useNavigate } from "react-router-dom";
import { authApi } from "../../api/auth";
import { ApiError } from "../../api/client";
import { useAuth } from "../../context/AuthContext";

export function ResidentLoginPage() {
  const [phone, setPhone] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const response = await authApi.residentLogin(Number(phone), password);
      login(response);
      navigate("/resident");
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Login failed.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page">
      <div className="card">
        <h2>Neighborhood Resident Login</h2>
        <form className="stack" onSubmit={handleSubmit}>
          {error && <div className="error-banner">{error}</div>}
          <div className="field">
            <label htmlFor="phone">Phone</label>
            <input
              id="phone"
              type="tel"
              value={phone}
              onChange={(e) => setPhone(e.target.value)}
              required
            />
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
            {loading ? "Logging in..." : "Log In"}
          </button>
          <Link to="/resident/register" className="btn-link">
            Don't have an account? Register
          </Link>
        </form>
      </div>
    </div>
  );
}
