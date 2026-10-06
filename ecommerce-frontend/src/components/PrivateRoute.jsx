import { Navigate, Outlet, useLocation } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const PrivateRoute = () => {
  const { isAuthenticated, loading } = useAuth();

  const location = useLocation();

  // =========================================================
  // LOADING
  // =========================================================

  if (loading) {
    return (
      <div className="container mt-5 text-center">
        <div className="spinner-border" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    );
  }

  // =========================================================
  // NOT AUTHENTICATED
  // =========================================================

  if (!isAuthenticated) {
    return (
      <Navigate
        to="/login"
        replace
        state={{
          from: location,
        }}
      />
    );
  }

  // =========================================================
  // AUTHENTICATED
  // =========================================================

  return <Outlet />;
};

export default PrivateRoute;
