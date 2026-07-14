import type { ReactNode } from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import type { AuthResponse } from "../api/types";

export function ProtectedRoute({
  role,
  children,
}: {
  role: AuthResponse["role"];
  children: ReactNode;
}) {
  const { auth } = useAuth();

  if (!auth || auth.role !== role) {
    return <Navigate to="/" replace />;
  }

  return <>{children}</>;
}
