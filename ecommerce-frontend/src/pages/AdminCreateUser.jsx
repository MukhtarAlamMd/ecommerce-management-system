import { useState } from "react";
import { useNavigate } from "react-router-dom";
import authService from "../services/authService";

const AdminCreateUser = () => {
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
  // HANDLE INPUT
  // =====================================================

  const handleChange = (e) => {
    const { name, value } = e.target;

    setFormData((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  // =====================================================
  // CREATE USER
  // =====================================================

  const handleSubmit = async (e) => {
    e.preventDefault();

    setError("");
    setSuccess("");

    try {
      setLoading(true);

      console.log("Creating user:", formData);

      const response = await authService.createUserByAdmin(formData);

      console.log("User created:", response);

      setSuccess(`User created successfully with role ${response.role}`);

      setFormData({
        firstName: "",
        lastName: "",
        email: "",
        phone: "",
        password: "",
        role: "ROLE_CUSTOMER",
      });
    } catch (err) {
      console.error("Create user failed:", err);

      setError(err.response?.data?.message || "Failed to create user");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container mt-5">
      <div className="row justify-content-center">
        <div className="col-md-7">
          <div className="card shadow">
            <div className="card-header bg-dark text-white">
              <h4 className="mb-0">Create User</h4>
            </div>

            <div className="card-body">
              {/* ERROR */}

              {error && <div className="alert alert-danger">{error}</div>}

              {/* SUCCESS */}

              {success && <div className="alert alert-success">{success}</div>}

              <form onSubmit={handleSubmit}>
                {/* FIRST NAME */}

                <div className="mb-3">
                  <label className="form-label">First Name</label>

                  <input
                    type="text"
                    name="firstName"
                    className="form-control"
                    value={formData.firstName}
                    onChange={handleChange}
                    required
                  />
                </div>

                {/* LAST NAME */}

                <div className="mb-3">
                  <label className="form-label">Last Name</label>

                  <input
                    type="text"
                    name="lastName"
                    className="form-control"
                    value={formData.lastName}
                    onChange={handleChange}
                    required
                  />
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
                    required
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
                    required
                  />
                </div>

                {/* =================================================
                    ROLE
                ================================================= */}

                <div className="mb-4">
                  <label className="form-label fw-bold">Role</label>

                  <select
                    name="role"
                    className="form-select"
                    value={formData.role}
                    onChange={handleChange}
                    required
                  >
                    <option value="ROLE_CUSTOMER">Customer</option>

                    <option value="ROLE_SELLER">Seller</option>

                    <option value="ROLE_ADMIN">Admin</option>
                  </select>
                </div>

                {/* BUTTON */}

                <div className="d-flex gap-2">
                  <button
                    type="submit"
                    className="btn btn-success"
                    disabled={loading}
                  >
                    {loading ? "Creating..." : "Create User"}
                  </button>

                  <button
                    type="button"
                    className="btn btn-secondary"
                    onClick={() => navigate("/admin")}
                  >
                    Cancel
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AdminCreateUser;
