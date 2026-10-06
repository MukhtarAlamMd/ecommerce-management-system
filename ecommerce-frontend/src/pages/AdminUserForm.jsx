import { useState } from "react";
import { useNavigate } from "react-router-dom";

import userService from "../services/userService";

const AdminUserForm = () => {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    phone: "",
    password: "",
    role: "ROLE_CUSTOMER",
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  // =====================================================
  // HANDLE CHANGE
  // =====================================================

  const handleChange = (e) => {
    const { name, value } = e.target;

    setFormData((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  // =====================================================
  // SUBMIT
  // =====================================================

  const handleSubmit = async (e) => {
    e.preventDefault();

    setError("");
    setSuccess("");

    // Basic validation
    if (
      !formData.firstName.trim() ||
      !formData.lastName.trim() ||
      !formData.email.trim() ||
      !formData.phone.trim() ||
      !formData.password.trim()
    ) {
      setError("All fields are required.");
      return;
    }

    try {
      setLoading(true);

      const response = await userService.createUser(formData);

      console.log("User created:", response);

      setSuccess(
        `${formData.role.replace("ROLE_", "")} user created successfully.`,
      );

      // Clear form
      setFormData({
        firstName: "",
        lastName: "",
        email: "",
        phone: "",
        password: "",
        role: "ROLE_CUSTOMER",
      });
    } catch (err) {
      console.error("Failed to create user:", err);

      setError(
        err.response?.data?.message ||
          err.response?.data?.error ||
          "Failed to create user",
      );
    } finally {
      setLoading(false);
    }
  };

  // =====================================================
  // UI
  // =====================================================

  return (
    <div className="container mt-5 mb-5">
      {/* HEADER */}

      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="fw-bold">Create User</h2>

          <p className="text-muted mb-0">
            Create Customer, Seller, or Admin accounts
          </p>
        </div>

        <button
          type="button"
          className="btn btn-outline-secondary"
          onClick={() => navigate("/admin")}
        >
          Dashboard
        </button>
      </div>

      {/* ERROR */}

      {error && <div className="alert alert-danger">{error}</div>}

      {/* SUCCESS */}

      {success && <div className="alert alert-success">{success}</div>}

      {/* FORM */}

      <div className="card shadow-sm">
        <div className="card-header">
          <strong>User Information</strong>
        </div>

        <div className="card-body">
          <form onSubmit={handleSubmit}>
            <div className="row">
              {/* FIRST NAME */}

              <div className="col-md-6 mb-3">
                <label className="form-label">First Name</label>

                <input
                  type="text"
                  name="firstName"
                  className="form-control"
                  value={formData.firstName}
                  onChange={handleChange}
                  placeholder="Enter first name"
                  disabled={loading}
                />
              </div>

              {/* LAST NAME */}

              <div className="col-md-6 mb-3">
                <label className="form-label">Last Name</label>

                <input
                  type="text"
                  name="lastName"
                  className="form-control"
                  value={formData.lastName}
                  onChange={handleChange}
                  placeholder="Enter last name"
                  disabled={loading}
                />
              </div>
            </div>

            {/* EMAIL */}

            <div className="mb-3">
              <label className="form-label">Email</label>

              <input
                type="email"
                name="email"
                className="form-control"
                value={formData.email}
                onChange={handleChange}
                placeholder="Enter email"
                disabled={loading}
              />
            </div>

            {/* PHONE */}

            <div className="mb-3">
              <label className="form-label">Phone</label>

              <input
                type="text"
                name="phone"
                className="form-control"
                value={formData.phone}
                onChange={handleChange}
                placeholder="Enter phone number"
                disabled={loading}
              />
            </div>

            {/* PASSWORD */}

            <div className="mb-3">
              <label className="form-label">Password</label>

              <input
                type="password"
                name="password"
                className="form-control"
                value={formData.password}
                onChange={handleChange}
                placeholder="Enter password"
                disabled={loading}
              />
            </div>

            {/* ROLE */}

            <div className="mb-4">
              <label className="form-label">Role</label>

              <select
                name="role"
                className="form-select"
                value={formData.role}
                onChange={handleChange}
                disabled={loading}
              >
                <option value="ROLE_CUSTOMER">Customer</option>

                <option value="ROLE_SELLER">Seller</option>

                <option value="ROLE_ADMIN">Admin</option>
              </select>

              <small className="text-muted">
                Select the role for this user.
              </small>
            </div>

            {/* BUTTONS */}

            <div className="d-flex gap-2">
              <button
                type="submit"
                className="btn btn-primary"
                disabled={loading}
              >
                {loading ? "Creating User..." : "Create User"}
              </button>

              <button
                type="button"
                className="btn btn-outline-secondary"
                onClick={() => navigate("/admin")}
                disabled={loading}
              >
                Cancel
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default AdminUserForm;
