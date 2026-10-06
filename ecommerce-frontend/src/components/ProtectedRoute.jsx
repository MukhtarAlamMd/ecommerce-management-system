import { Navigate, useLocation } from "react-router-dom";

import { useAuth } from "../context/AuthContext";

const ProtectedRoute = ({ children }) => {
  const { isAuthenticated, loading } = useAuth();

  const location = useLocation();

  // =============================================
  // LOADING
  // =============================================

  if (loading) {
    return (
      <div className="container mt-5 text-center">
        <div className="spinner-border" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    );
  }

  // =============================================
  // NOT LOGGED IN
  // =============================================

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

  // =============================================
  // AUTHENTICATED
  // =============================================

  return children;
};

export default ProtectedRoute;
