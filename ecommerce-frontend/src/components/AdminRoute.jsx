import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const AdminRoute = () => {
  const { isAuthenticated, isAdmin, loading } = useAuth();

  // =========================================================
  // AUTH LOADING
  // =========================================================

  if (loading) {
    return <div>Loading...</div>;
  }

  // =========================================================
  // NOT LOGGED IN
  // =========================================================

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  // =========================================================
  // NOT ADMIN
  // =========================================================

  if (!isAdmin) {
    return <Navigate to="/" replace />;
  }

  // =========================================================
  // ADMIN
  // =========================================================

  return <Outlet />;
};

export default AdminRoute;
