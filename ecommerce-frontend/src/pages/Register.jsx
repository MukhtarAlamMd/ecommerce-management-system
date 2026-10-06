import { Link, useLocation } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const Register = () => {
  const location = useLocation();

  const { register, loading } = useAuth();

  const from =
    location.state?.from?.pathname || location.state?.from || "/profile";

  const handleRegister = async () => {
    try {
      await register();
    } catch (error) {
      console.error("Keycloak registration failed:", error);
    }
  };

  return (
    <div className="container mt-5 mb-5">
      <div className="row justify-content-center">
        <div className="col-md-6">
          <div className="card shadow">
            <div className="card-body p-4 text-center">
              <h2 className="mb-2">Create Account</h2>

              <p className="text-muted mb-4">
                Create your account securely using Keycloak
              </p>

              <button
                type="button"
                className="btn btn-success w-100"
                onClick={handleRegister}
                disabled={loading}
              >
                {loading ? "Opening Registration..." : "Create Account"}
              </button>

              <div className="text-center mt-4">
                <span>Already have an account?</span>{" "}
                <Link
                  to="/login"
                  state={{
                    from: location.state?.from || from,
                  }}
                >
                  Login
                </Link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Register;
