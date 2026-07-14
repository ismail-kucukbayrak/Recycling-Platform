import { Link } from "react-router-dom";

export function ResidentMenuPage() {
  return (
    <div className="page">
      <h2>Neighborhood Resident</h2>
      <div className="menu-grid" style={{ maxWidth: 480 }}>
        <Link to="/resident/add-waste" className="btn">
          Add Waste
        </Link>
        <Link to="/resident/report" className="btn btn-secondary">
          Report
        </Link>
      </div>
    </div>
  );
}
