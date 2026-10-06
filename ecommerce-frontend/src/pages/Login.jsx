import { useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import "./Login.css";

const Login = () => {
  const navigate = useNavigate();
  const location = useLocation();

  const { login, register, isAuthenticated, loading } = useAuth();

  // =========================================================
  // ORIGINAL PAGE
  // =========================================================

  const from = location.state?.from?.pathname || "/";

  // =========================================================
  // REDIRECT AFTER AUTHENTICATION
  // =========================================================

  useEffect(() => {
    if (!loading && isAuthenticated) {
      navigate(from, { replace: true });
    }
  }, [loading, isAuthenticated, navigate, from]);

  // =========================================================
  // LOGIN
  // =========================================================

  const handleLogin = async () => {
    try {
      await login();
    } catch (error) {
      console.error("Login failed:", error);
    }
  };

  // =========================================================
  // REGISTER
  // =========================================================

  const handleRegister = async () => {
    try {
      await register();
    } catch (error) {
      console.error("Registration failed:", error);
    }
  };

  // =========================================================
  // LOADING
  // =========================================================

  if (loading) {
    return (
      <div className="login-page">
        <div className="login-card loading-card">
          <div className="login-loading-icon">🔐</div>

          <h2>Checking your account...</h2>

          <p>Please wait while we securely check your login.</p>

          <div className="loading-spinner"></div>
        </div>
      </div>
    );
  }

  // =========================================================
  // LOGIN PAGE
  // =========================================================

  return (
    <div className="login-page">
      {/* =====================================================
          LEFT SIDE
      ===================================================== */}

      <div className="login-welcome">
        <div className="welcome-content">
          <div className="welcome-logo">🛍️</div>

          <h1>
            Welcome to
            <span>E-Commerce</span>
          </h1>

          <p>
            Shop your favorite products with a simple, secure and enjoyable
            experience.
          </p>

          <div className="welcome-features">
            <div className="welcome-feature">
              <span>🛒</span>
              <div>
                <strong>Easy Shopping</strong>
                <small>Find products you love</small>
              </div>
            </div>

            <div className="welcome-feature">
              <span>🔒</span>
              <div>
                <strong>Secure Account</strong>
                <small>Your account stays protected</small>
              </div>
            </div>

            <div className="welcome-feature">
              <span>📦</span>
              <div>
                <strong>Track Orders</strong>
                <small>Stay updated on your orders</small>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* =====================================================
          RIGHT SIDE
      ===================================================== */}

      <div className="login-section">
        <div className="login-card">
          {/* Header */}

          <div className="login-header">
            <div className="login-icon">👋</div>

            <h2>Welcome Back!</h2>

            <p>Sign in to continue shopping with us.</p>
          </div>

          {/* Information */}

          <div className="login-message">
            <div className="message-icon">🔐</div>

            <div>
              <strong>Secure Sign In</strong>

              <p>Your account is securely protected.</p>
            </div>
          </div>

          {/* Login */}

          <button type="button" onClick={handleLogin} className="login-button">
            <span>Sign In</span>
            <span className="login-arrow">→</span>
          </button>

          {/* Register */}

          <div className="register-section">
            <span>New to E-Commerce?</span>

            <button
              type="button"
              onClick={handleRegister}
              className="register-button"
            >
              Create an account
            </button>
          </div>

          {/* Security Footer */}

          <div className="login-footer">
            <span>🔒 Secure</span>

            <span>•</span>

            <span>Private</span>

            <span>•</span>

            <span>Protected</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;
