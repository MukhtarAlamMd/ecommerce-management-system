import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import inventoryService from "../services/inventoryService";
import productService from "../services/productService";

const AdminInventory = () => {
  const navigate = useNavigate();

  const [inventory, setInventory] = useState([]);
  const [products, setProducts] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // =========================================================
  // LOAD INVENTORY + PRODUCTS
  // =========================================================

  const loadData = async () => {
    try {
      setLoading(true);
      setError("");

      const [inventoryData, productData] = await Promise.all([
        inventoryService.getAllInventory(),
        productService.getAllProducts(),
      ]);

      setInventory(inventoryData || []);
      setProducts(productData || []);
    } catch (err) {
      console.error("Failed to load inventory:", err);

      setError(err.response?.data?.message || "Failed to load inventory");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  // =========================================================
  // FIND PRODUCT
  // =========================================================

  const getProductName = (productId) => {
    const product = products.find(
      (item) => Number(item.id) === Number(productId),
    );

    return product ? product.name : `Product #${productId}`;
  };

  // =========================================================
  // DELETE
  // =========================================================

  const handleDelete = async (id) => {
    const confirmed = window.confirm(
      "Are you sure you want to delete this inventory?",
    );

    if (!confirmed) {
      return;
    }

    try {
      setError("");

      await inventoryService.deleteInventory(id);

      await loadData();
    } catch (err) {
      console.error("Failed to delete inventory:", err);

      setError(err.response?.data?.message || "Failed to delete inventory");
    }
  };

  // =========================================================
  // STOCK IN
  // =========================================================

  const handleStockIn = async (productId) => {
    const quantity = window.prompt("Enter quantity to add:");

    if (quantity === null) {
      return;
    }

    const parsedQuantity = Number(quantity);

    if (!Number.isInteger(parsedQuantity) || parsedQuantity <= 0) {
      setError("Stock quantity must be a positive whole number.");

      return;
    }

    try {
      setError("");

      await inventoryService.addStock(productId, parsedQuantity);

      await loadData();
    } catch (err) {
      console.error("Failed to add stock:", err);

      setError(err.response?.data?.message || "Failed to add stock");
    }
  };

  // =========================================================
  // LOADING
  // =========================================================

  if (loading) {
    return (
      <div className="container mt-5 text-center">
        <div className="spinner-border" role="status" />

        <p className="mt-3">Loading inventory...</p>
      </div>
    );
  }

  // =========================================================
  // UI
  // =========================================================

  return (
    <div className="container mt-5">
      {/* =====================================================
          HEADER
      ===================================================== */}

      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="fw-bold">Manage Inventory</h2>

          <p className="text-muted mb-0">Manage product stock</p>
        </div>

        <div className="d-flex gap-2">
          <button
            type="button"
            className="btn btn-outline-secondary"
            onClick={() => navigate("/admin")}
          >
            Dashboard
          </button>

          <button
            type="button"
            className="btn btn-primary"
            onClick={() => navigate("/admin/inventory/create")}
          >
            Add Inventory
          </button>
        </div>
      </div>

      {/* =====================================================
          ERROR
      ===================================================== */}

      {error && <div className="alert alert-danger">{error}</div>}

      {/* =====================================================
          TABLE
      ===================================================== */}

      <div className="card shadow-sm">
        <div className="card-header">
          <strong>Inventory ({inventory.length})</strong>
        </div>

        <div className="table-responsive">
          <table className="table table-hover mb-0 align-middle">
            <thead>
              <tr>
                <th>ID</th>
                <th>Product</th>
                <th>Available</th>
                <th>Reserved</th>
                <th>Reorder Level</th>
                <th>Status</th>
                <th>Stock Status</th>
                <th>Actions</th>
              </tr>
            </thead>

            <tbody>
              {inventory.length === 0 ? (
                <tr>
                  <td colSpan="8" className="text-center py-4">
                    No inventory found.
                  </td>
                </tr>
              ) : (
                inventory.map((item) => (
                  <tr key={item.id}>
                    {/* ID */}

                    <td>#{item.id}</td>

                    {/* PRODUCT */}

                    <td>
                      <div className="fw-semibold">
                        {getProductName(item.productId)}
                      </div>

                      <small className="text-muted">
                        Product ID: #{item.productId}
                      </small>
                    </td>

                    {/* AVAILABLE */}

                    <td>
                      <strong>{item.availableQuantity}</strong>
                    </td>

                    {/* RESERVED */}

                    <td>{item.reservedQuantity ?? 0}</td>

                    {/* REORDER LEVEL */}

                    <td>{item.reorderLevel}</td>

                    {/* STATUS */}

                    <td>
                      {item.enabled ? (
                        <span className="badge bg-success">Enabled</span>
                      ) : (
                        <span className="badge bg-secondary">Disabled</span>
                      )}
                    </td>

                    {/* LOW STOCK */}

                    <td>
                      {item.lowStock ? (
                        <span className="badge bg-danger">Low Stock</span>
                      ) : (
                        <span className="badge bg-success">Normal</span>
                      )}
                    </td>

                    {/* ACTIONS */}

                    <td>
                      <div className="d-flex gap-2">
                        {/* EDIT */}

                        <button
                          type="button"
                          className="btn btn-sm btn-outline-primary"
                          onClick={() =>
                            navigate(`/admin/inventory/edit/${item.id}`)
                          }
                        >
                          Edit
                        </button>

                        {/* STOCK IN */}

                        <button
                          type="button"
                          className="btn btn-sm btn-outline-success"
                          onClick={() => handleStockIn(item.productId)}
                        >
                          Stock In
                        </button>

                        {/* DELETE */}

                        <button
                          type="button"
                          className="btn btn-sm btn-outline-danger"
                          onClick={() => handleDelete(item.id)}
                        >
                          Delete
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default AdminInventory;
