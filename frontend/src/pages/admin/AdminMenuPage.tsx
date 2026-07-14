import { Link } from "react-router-dom";

export function AdminMenuPage() {
  return (
    <div className="page">
      <h2>Admin Panel</h2>
      <div className="menu-grid" style={{ maxWidth: 800 }}>
        <Link to="/admin/warehouse" className="btn">
          Warehouse
        </Link>
        <Link to="/admin/appointments" className="btn">
          Appointments
        </Link>
        <Link to="/admin/reports/monthly-waste" className="btn">
          Total Waste Added This Month
        </Link>
        <Link to="/admin/reports/contributors" className="btn">
          Residents Who Added Waste This Month
        </Link>
        <Link to="/admin/residents" className="btn">
          View Neighborhood Resident
        </Link>
        <Link to="/admin/reset" className="btn btn-danger">
          Reset Monthly Waste Records
        </Link>
      </div>
    </div>
  );
}
