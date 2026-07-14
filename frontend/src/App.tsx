import { BrowserRouter, Route, Routes } from "react-router-dom";
import "./App.css";
import { AuthProvider } from "./context/AuthContext";
import { TopBar } from "./components/TopBar";
import { ProtectedRoute } from "./components/ProtectedRoute";

import { HomePage } from "./pages/HomePage";

import { ResidentLoginPage } from "./pages/resident/ResidentLoginPage";
import { ResidentRegisterPage } from "./pages/resident/ResidentRegisterPage";
import { ResidentMenuPage } from "./pages/resident/ResidentMenuPage";
import { AddWastePage } from "./pages/resident/AddWastePage";
import { ResidentReportPage } from "./pages/resident/ResidentReportPage";

import { CollectorLoginPage } from "./pages/collector/CollectorLoginPage";
import { CollectorRegisterPage } from "./pages/collector/CollectorRegisterPage";
import { CollectorMenuPage } from "./pages/collector/CollectorMenuPage";
import { CreateAppointmentPage } from "./pages/collector/CreateAppointmentPage";

import { AdminLoginPage } from "./pages/admin/AdminLoginPage";
import { AdminMenuPage } from "./pages/admin/AdminMenuPage";
import { AdminWarehousePage } from "./pages/admin/AdminWarehousePage";
import { AdminAppointmentsPage } from "./pages/admin/AdminAppointmentsPage";
import { AdminMonthlyWastePage } from "./pages/admin/AdminMonthlyWastePage";
import { AdminContributorsPage } from "./pages/admin/AdminContributorsPage";
import { AdminResidentsPage } from "./pages/admin/AdminResidentsPage";
import { AdminResetPage } from "./pages/admin/AdminResetPage";

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <TopBar />
        <Routes>
          <Route path="/" element={<HomePage />} />

          <Route path="/resident/login" element={<ResidentLoginPage />} />
          <Route path="/resident/register" element={<ResidentRegisterPage />} />
          <Route
            path="/resident"
            element={
              <ProtectedRoute role="resident">
                <ResidentMenuPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/resident/add-waste"
            element={
              <ProtectedRoute role="resident">
                <AddWastePage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/resident/report"
            element={
              <ProtectedRoute role="resident">
                <ResidentReportPage />
              </ProtectedRoute>
            }
          />

          <Route path="/collector/login" element={<CollectorLoginPage />} />
          <Route path="/collector/register" element={<CollectorRegisterPage />} />
          <Route
            path="/collector"
            element={
              <ProtectedRoute role="collector">
                <CollectorMenuPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/collector/appointments/new"
            element={
              <ProtectedRoute role="collector">
                <CreateAppointmentPage />
              </ProtectedRoute>
            }
          />

          <Route path="/admin/login" element={<AdminLoginPage />} />
          <Route
            path="/admin"
            element={
              <ProtectedRoute role="admin">
                <AdminMenuPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/warehouse"
            element={
              <ProtectedRoute role="admin">
                <AdminWarehousePage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/appointments"
            element={
              <ProtectedRoute role="admin">
                <AdminAppointmentsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/reports/monthly-waste"
            element={
              <ProtectedRoute role="admin">
                <AdminMonthlyWastePage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/reports/contributors"
            element={
              <ProtectedRoute role="admin">
                <AdminContributorsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/residents"
            element={
              <ProtectedRoute role="admin">
                <AdminResidentsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/reset"
            element={
              <ProtectedRoute role="admin">
                <AdminResetPage />
              </ProtectedRoute>
            }
          />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
