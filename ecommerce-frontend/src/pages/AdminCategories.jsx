import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import categoryService from "../services/categoryService";

const AdminCategories = () => {
  const navigate = useNavigate();

  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const loadCategories = async () => {
    try {
      setLoading(true);
      setError("");

      const data = await categoryService.getAllCategories();

      setCategories(data);
    } catch (err) {
      console.error("Failed to load categories:", err);

      setError(err.response?.data?.message || "Failed to load categories");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadCategories();
  }, []);

  const handleDelete = async (id) => {
    const confirmed = window.confirm(
      "Are you sure you want to delete this category?",
    );

    if (!confirmed) {
      return;
    }

    try {
      await categoryService.deleteCategory(id);

      await loadCategories();
    } catch (err) {
      console.error("Failed to delete category:", err);

      setError(err.response?.data?.message || "Failed to delete category");
    }
  };

  if (loading) {
    return (
      <div className="container mt-5 text-center">
        <div className="spinner-border" role="status" />

        <p className="mt-3">Loading categories...</p>
      </div>
    );
  }

  return (
    <div className="container mt-5">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="fw-bold">Manage Categories</h2>

          <p className="text-muted mb-0">
            Create and manage product categories
          </p>
        </div>

        <div className="d-flex gap-2">
          <button
            className="btn btn-primary"
            onClick={() => navigate("/admin/categories/create")}
          >
            Create Category
          </button>

          <button
            className="btn btn-outline-secondary"
            onClick={() => navigate("/admin/products")}
          >
            Products
          </button>
        </div>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      {!categories.length ? (
        <div className="alert alert-info">No categories found.</div>
      ) : (
        <div className="card shadow-sm">
          <div className="card-header">
            <h5 className="mb-0">Categories ({categories.length})</h5>
          </div>

          <div className="card-body p-0">
            <div className="table-responsive">
              <table className="table table-hover mb-0">
                <thead className="table-light">
                  <tr>
                    <th>ID</th>
                    <th>Name</th>
                    <th>Description</th>
                    <th>Actions</th>
                  </tr>
                </thead>

                <tbody>
                  {categories.map((category) => (
                    <tr key={category.id}>
                      <td>
                        <strong>#{category.id}</strong>
                      </td>

                      <td>{category.name}</td>

                      <td>{category.description || "N/A"}</td>

                      <td>
                        <div className="d-flex gap-2">
                          <button
                            className="btn btn-sm btn-outline-primary"
                            onClick={() =>
                              navigate(`/admin/categories/edit/${category.id}`)
                            }
                          >
                            Edit
                          </button>

                          <button
                            className="btn btn-sm btn-outline-danger"
                            onClick={() => handleDelete(category.id)}
                          >
                            Delete
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default AdminCategories;
