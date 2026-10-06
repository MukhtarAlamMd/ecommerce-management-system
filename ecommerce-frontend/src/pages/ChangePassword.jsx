import { useState } from "react";
import { useNavigate } from "react-router-dom";
import authService from "../services/authService";

const ChangePassword = () => {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    currentPassword: "",
    newPassword: "",
    confirmPassword: "",
  });

  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    setMessage("");
    setError("");

    if (formData.newPassword !== formData.confirmPassword) {
      setError("New password and confirm password do not match.");
      return;
    }

    if (formData.newPassword.length < 6) {
      setError("New password must be at least 6 characters.");
      return;
    }

    setLoading(true);

    try {
      const response = await authService.changePassword(formData);

      setMessage(response);

      setFormData({
        currentPassword: "",
        newPassword: "",
        confirmPassword: "",
      });

      setTimeout(() => {
        navigate("/profile");
      }, 1500);
    } catch (error) {
      console.error("Change password failed:", error);

      setError(
        error.response?.data?.message ||
          error.response?.data ||
          "Failed to change password.",
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container mt-5">
      <div className="row justify-content-center">
        <div className="col-md-6">
          <div className="card shadow">
            <div className="card-body p-4">
              <h2 className="text-center mb-4">Change Password</h2>

              {message && <div className="alert alert-success">{message}</div>}

              {error && <div className="alert alert-danger">{error}</div>}

              <form onSubmit={handleSubmit}>
                <div className="mb-3">
                  <label className="form-label">Current Password</label>

                  <input
                    type="password"
                    name="currentPassword"
                    className="form-control"
                    value={formData.currentPassword}
                    onChange={handleChange}
                    required
                  />
                </div>

                <div className="mb-3">
                  <label className="form-label">New Password</label>

                  <input
                    type="password"
                    name="newPassword"
                    className="form-control"
                    value={formData.newPassword}
                    onChange={handleChange}
                    required
                  />
                </div>

                <div className="mb-3">
                  <label className="form-label">Confirm New Password</label>

                  <input
                    type="password"
                    name="confirmPassword"
                    className="form-control"
                    value={formData.confirmPassword}
                    onChange={handleChange}
                    required
                  />
                </div>

                <button
                  type="submit"
                  className="btn btn-primary w-100"
                  disabled={loading}
                >
                  {loading ? "Changing Password..." : "Change Password"}
                </button>
              </form>

              <button
                type="button"
                className="btn btn-secondary w-100 mt-3"
                onClick={() => navigate("/profile")}
              >
                Back to Profile
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ChangePassword;
