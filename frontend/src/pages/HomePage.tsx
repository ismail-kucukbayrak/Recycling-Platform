import { Link } from "react-router-dom";

export function HomePage() {
  return (
    <div className="page">
      <h1>Recycling Platform</h1>
      <p className="page-intro">
        Track household recycling, schedule pickups with collector companies, and manage the
        neighborhood's waste warehouse — all in one place.
      </p>

      <div className="role-grid">
        <Link to="/resident/login" className="role-card">
          <h2>Neighborhood Resident</h2>
          <p>Log your recyclable waste and view your contribution report.</p>
        </Link>

        <Link to="/collector/login" className="role-card">
          <h2>Collector Company</h2>
          <p>Book a pickup appointment for available warehouse waste.</p>
        </Link>

        <Link to="/admin/login" className="role-card">
          <h2>Admin</h2>
          <p>Monitor warehouse stock, appointments, and monthly reports.</p>
        </Link>
      </div>
    </div>
  );
}
