import { Link } from "react-router-dom";

export function CollectorMenuPage() {
  return (
    <div className="page">
      <h2>Collector Company</h2>
      <div className="menu-grid" style={{ maxWidth: 300 }}>
        <Link to="/collector/appointments/new" className="btn">
          Create Appointment
        </Link>
      </div>
    </div>
  );
}
