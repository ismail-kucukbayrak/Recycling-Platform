import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export function TopBar() {
  const { auth, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/");
  };

  return (
    <div className="topbar">
      <div className="topbar-inner">
        <Link to="/" className="brand">
          Recycling Platform
        </Link>
        {auth && (
          <button className="btn btn-secondary" onClick={handleLogout}>
            Log out ({auth.role})
          </button>
        )}
      </div>
    </div>
  );
}
