import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import inventoryService from "../services/inventoryService";
import productService from "../services/productService";

const AdminInventoryForm = () => {
  const navigate = useNavigate();
  const { id } = useParams();

  const isEditMode = Boolean(id);

  const [products, setProducts] = useState([]);

  const [formData, setFormData] = useState({
    productId: "",
    availableQuantity: "",
    reorderLevel: "10",
  });

  const [loading, setLoading] = useState(false);
  const [loadingProducts, setLoadingProducts] = useState(true);
  const [error, setError] = useState("");

  // =========================================================
  // LOAD PRODUCTS
  // =========================================================

  const loadProducts = async () => {
    try {
      setLoadingProducts(true);
      setError("");

      const data = await productService.getAllProducts();

      setProducts(data);
    } catch (err) {
      console.error("Failed to load products:", err);

      setError(err.response?.data?.message || "Failed to load products");
    } finally {
      setLoadingProducts(false);
    }
  };

  // =========================================================
  // LOAD INVENTORY FOR EDIT
  // =========================================================

  const loadInventory = async () => {
    if (!isEditMode) {
      return;
    }

    try {
      setLoading(true);
      setError("");

      const inventory = await inventoryService.getInventoryById(id);

      setFormData({
        productId: inventory.productId ? String(inventory.productId) : "",

        availableQuantity: inventory.availableQuantity ?? "",

        reorderLevel: inventory.reorderLevel ?? 10,
      });
    } catch (err) {
      console.error("Failed to load inventory:", err);

      setError(err.response?.data?.message || "Failed to load inventory");
    } finally {
      setLoading(false);
    }
  };

  // =========================================================
  // INITIAL LOAD
  // =========================================================

  useEffect(() => {
    loadProducts();
    loadInventory();
  }, [id]);

  // =========================================================
  // HANDLE INPUT
  // =========================================================

  const handleChange = (e) => {
    const { name, value } = e.target;

    setFormData((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  // =========================================================
  // SUBMIT
  // =========================================================

  const handleSubmit = async (e) => {
    e.preventDefault();

    setError("");

    if (!formData.productId) {
      setError("Please select a product.");
      return;
    }

    if (
      formData.availableQuantity === "" ||
      Number(formData.availableQuantity) < 0
    ) {
      setError("Available quantity cannot be negative.");
      return;
    }

    if (formData.reorderLevel === "" || Number(formData.reorderLevel) < 0) {
      setError("Reorder level cannot be negative.");
      return;
    }

    try {
      setLoading(true);

      const inventoryData = {
        productId: Number(formData.productId),

        availableQuantity: Number(formData.availableQuantity),

        reorderLevel: Number(formData.reorderLevel),
      };

      console.log("Inventory payload:", inventoryData);

      if (isEditMode) {
        await inventoryService.updateInventory(id, inventoryData);
      } else {
        await inventoryService.createInventory(inventoryData);
      }

      navigate("/admin/inventory");
    } catch (err) {
      console.error("Failed to save inventory:", err);

      setError(err.response?.data?.message || "Failed to save inventory");
    } finally {
      setLoading(false);
    }
  };

  // =========================================================
  // LOADING
  // =========================================================

  if (loading && isEditMode) {
    return (
      <div className="container mt-5 text-center">
        <div className="spinner-border" role="status" />

        <p className="mt-3">Loading inventory...</p>
      </div>
    );
  }

  // =========================================================
  // PAGE
  // =========================================================

  return (
    <div className="container mt-5">
      {/* HEADER */}

      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="fw-bold">
            {isEditMode ? "Edit Inventory" : "Create Inventory"}
          </h2>

          <p className="text-muted mb-0">
            {isEditMode
              ? "Update inventory information"
              : "Add inventory for a product"}
          </p>
        </div>

        <button
          type="button"
          className="btn btn-outline-secondary"
          onClick={() => navigate("/admin/inventory")}
        >
          Back
        </button>
      </div>

      {/* ERROR */}

      {error && <div className="alert alert-danger">{error}</div>}

      {/* FORM */}

      <div className="card shadow-sm">
        <div className="card-body">
          <form onSubmit={handleSubmit}>
            {/* PRODUCT */}

            <div className="mb-3">
              <label className="form-label">Product</label>

              <select
                name="productId"
                className="form-select"
                value={formData.productId}
                onChange={handleChange}
                disabled={loadingProducts || isEditMode}
                required
              >
                <option value="">
                  {loadingProducts ? "Loading products..." : "Select Product"}
                </option>

                {products.map((product) => (
                  <option key={product.id} value={product.id}>
                    {product.name} — ₹{product.price}
                  </option>
                ))}
              </select>

              {isEditMode && (
                <small className="text-muted">
                  Product cannot be changed for existing inventory.
                </small>
              )}
            </div>

            {/* AVAILABLE QUANTITY */}

            <div className="mb-3">
              <label className="form-label">Available Quantity</label>

              <input
                type="number"
                name="availableQuantity"
                className="form-control"
                min="0"
                value={formData.availableQuantity}
                onChange={handleChange}
                required
              />

              <small className="text-muted">
                Current stock available for sale.
              </small>
            </div>

            {/* REORDER LEVEL */}

            <div className="mb-3">
              <label className="form-label">Reorder Level</label>

              <input
                type="number"
                name="reorderLevel"
                className="form-control"
                min="0"
                value={formData.reorderLevel}
                onChange={handleChange}
                required
              />

              <small className="text-muted">Warning level for low stock.</small>
            </div>

            {/* RESERVED */}

            {isEditMode && (
              <div className="mb-3">
                <label className="form-label">Reserved Quantity</label>

                <input
                  type="text"
                  className="form-control"
                  value="Managed automatically by orders"
                  disabled
                />
              </div>
            )}

            {/* BUTTONS */}

            <div className="d-flex gap-2">
              <button
                type="submit"
                className="btn btn-primary"
                disabled={loading || loadingProducts}
              >
                {loading
                  ? "Saving..."
                  : isEditMode
                    ? "Update Inventory"
                    : "Create Inventory"}
              </button>

              <button
                type="button"
                className="btn btn-outline-secondary"
                onClick={() => navigate("/admin/inventory")}
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

export default AdminInventoryForm;
