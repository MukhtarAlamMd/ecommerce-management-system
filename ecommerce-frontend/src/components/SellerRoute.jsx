import { Navigate, Outlet } from "react-router-dom";

import { useAuth } from "../context/AuthContext";

const SellerRoute = () => {
  const { user, loading } = useAuth();

  if (loading) {
    return (
      <div className="container mt-5 text-center">
        <div className="spinner-border" role="status" />

        <p className="mt-3">Checking authentication...</p>
      </div>
    );
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  // =====================================================
  // CHECK SELLER ROLE
  // =====================================================

  const roles = user.roles || [];

  const normalizedRoles = roles.map((role) =>
    String(role)
      .replace(/^ROLE_/, "")
      .toUpperCase()
      .trim(),
  );

  const isSeller = normalizedRoles.includes("SELLER");

  if (!isSeller) {
    return <Navigate to="/" replace />;
  }

  return <Outlet />;
};

export default SellerRoute;
