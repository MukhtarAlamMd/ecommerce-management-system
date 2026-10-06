import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import categoryService from "../services/categoryService";

const AdminCategoryForm = () => {
  const navigate = useNavigate();
  const { id } = useParams();

  const isEditMode = Boolean(id);

  const [formData, setFormData] = useState({
    name: "",
    description: "",
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  // =====================================================
  // LOAD CATEGORY FOR EDIT
  // =====================================================

  useEffect(() => {
    if (!isEditMode) {
      return;
    }

    const loadCategory = async () => {
      try {
        setLoading(true);
        setError("");

        const data = await categoryService.getCategoryById(id);

        setFormData({
          name: data.name || "",
          description: data.description || "",
        });
      } catch (err) {
        console.error("Failed to load category:", err);

        setError(err.response?.data?.message || "Failed to load category");
      } finally {
        setLoading(false);
      }
    };

    loadCategory();
  }, [id, isEditMode]);

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

    if (!formData.name.trim()) {
      setError("Category name is required.");
      return;
    }

    try {
      setLoading(true);

      const categoryData = {
        name: formData.name.trim(),
        description: formData.description.trim(),
      };

      if (isEditMode) {
        await categoryService.updateCategory(id, categoryData);
      } else {
        await categoryService.createCategory(categoryData);
      }

      navigate("/admin/categories");
    } catch (err) {
      console.error("Failed to save category:", err);

      setError(err.response?.data?.message || "Failed to save category");
    } finally {
      setLoading(false);
    }
  };

  // =====================================================
  // LOADING EDIT
  // =====================================================

  if (loading && isEditMode && !formData.name) {
    return (
      <div className="container mt-5 text-center">
        <div className="spinner-border" role="status" />

        <p className="mt-3">Loading category...</p>
      </div>
    );
  }

  // =====================================================
  // UI
  // =====================================================

  return (
    <div className="container mt-5">
      {/* HEADER */}

      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="fw-bold">
            {isEditMode ? "Edit Category" : "Create Category"}
          </h2>

          <p className="text-muted mb-0">
            {isEditMode
              ? "Update category information"
              : "Add a new product category"}
          </p>
        </div>

        <button
          type="button"
          className="btn btn-outline-secondary"
          onClick={() => navigate("/admin/categories")}
        >
          Back
        </button>
      </div>

      {/* ERROR */}

      {error && <div className="alert alert-danger">{error}</div>}

      {/* FORM */}

      <div className="card shadow-sm">
        <div className="card-header">
          <h5 className="mb-0">Category Information</h5>
        </div>

        <div className="card-body">
          <form onSubmit={handleSubmit}>
            {/* NAME */}

            <div className="mb-3">
              <label className="form-label">Category Name</label>

              <input
                type="text"
                name="name"
                className="form-control"
                placeholder="Enter category name"
                value={formData.name}
                onChange={handleChange}
                maxLength={100}
                required
              />
            </div>

            {/* DESCRIPTION */}

            <div className="mb-4">
              <label className="form-label">Description</label>

              <textarea
                name="description"
                className="form-control"
                rows="5"
                placeholder="Enter category description"
                value={formData.description}
                onChange={handleChange}
                maxLength={500}
              />

              <div className="form-text">Maximum 500 characters.</div>
            </div>

            {/* BUTTONS */}

            <div className="d-flex gap-2">
              <button
                type="submit"
                className="btn btn-primary"
                disabled={loading}
              >
                {loading
                  ? "Saving..."
                  : isEditMode
                    ? "Update Category"
                    : "Create Category"}
              </button>

              <button
                type="button"
                className="btn btn-outline-secondary"
                onClick={() => navigate("/admin/categories")}
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

export default AdminCategoryForm;
