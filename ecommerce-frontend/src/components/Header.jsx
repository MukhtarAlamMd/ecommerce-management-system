import { Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import NotificationBell from "./NotificationBell";

const Header = () => {
  const { user, logout } = useAuth();

  // =============================================
  // USER ROLE
  // =============================================

  const userRole = user?.role
    ? String(user.role)
        .replace(/^ROLE_/, "")
        .toUpperCase()
        .trim()
    : "";

  const isCustomer = userRole === "CUSTOMER";
  const isSeller = userRole === "SELLER";
  const isAdmin = userRole === "ADMIN";

  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-dark">
      <div className="container">
        {/* =========================================
            BRAND
        ========================================= */}

        <Link className="navbar-brand fw-bold" to="/">
          E-Commerce
        </Link>

        <div className="navbar-nav align-items-center">
          {/* =========================================
              PUBLIC
          ========================================= */}

          <Link className="nav-link" to="/">
            Home
          </Link>

          <Link className="nav-link" to="/products">
            Products
          </Link>

          {/* =========================================
              LOGGED-IN USER
          ========================================= */}

          {user && (
            <>
              {/* PROFILE */}

              <Link className="nav-link" to="/profile">
                Profile
              </Link>

              {/* =====================================
                  CUSTOMER
              ===================================== */}

              {isCustomer && (
                <>
                  <Link className="nav-link" to="/cart">
                    🛒 Cart
                  </Link>

                  <Link className="nav-link" to="/orders">
                    My Orders
                  </Link>

                  <Link className="nav-link" to="/payments">
                    My Payments
                  </Link>

                  {/* AI CUSTOMER SUPPORT */}

                  <Link className="nav-link" to="/ai-support">
                    🤖 AI Support
                  </Link>

                  <NotificationBell />
                </>
              )}

              {/* =====================================
                  SELLER
              ===================================== */}

              {isSeller && (
                <>
                  <Link className="nav-link" to="/seller/inventory">
                    Inventory
                  </Link>

                  <Link className="nav-link" to="/seller/orders">
                    Orders
                  </Link>
                </>
              )}

              {/* =====================================
                  ADMIN
              ===================================== */}

              {isAdmin && (
                <>
                  <Link className="nav-link" to="/admin">
                    Admin
                  </Link>

                  <Link className="nav-link" to="/admin/products">
                    Products
                  </Link>

                  <Link className="nav-link" to="/admin/orders">
                    Orders
                  </Link>

                  <Link className="nav-link" to="/admin/categories">
                    Categories
                  </Link>

                  <Link className="nav-link" to="/admin/inventory">
                    Inventory
                  </Link>
                </>
              )}
            </>
          )}

          {/* =========================================
              LOGIN / LOGOUT
          ========================================= */}

          {user ? (
            <>
              <span className="nav-link text-light">
                👤 {user.firstName || user.email}
              </span>

              <button
                type="button"
                className="btn btn-outline-light btn-sm ms-2"
                onClick={logout}
              >
                Logout
              </button>
            </>
          ) : (
            <Link className="nav-link" to="/login">
              🔐 Login
            </Link>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Header;
