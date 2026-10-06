import { useEffect, useState } from "react";

import inventoryService from "../../services/inventoryService";
import productService from "../../services/productService";

const SellerInventory = () => {
  const [inventory, setInventory] = useState([]);
  const [products, setProducts] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [stockQuantity, setStockQuantity] = useState({});
  const [processingId, setProcessingId] = useState(null);

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

      setInventory(Array.isArray(inventoryData) ? inventoryData : []);
      setProducts(Array.isArray(productData) ? productData : []);
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
  // PRODUCT NAME
  // =========================================================

  const getProductName = (productId) => {
    const product = products.find((item) => item.id === productId);

    return product?.name || `Product #${productId}`;
  };

  // =========================================================
  // STOCK INPUT
  // =========================================================

  const handleQuantityChange = (productId, value) => {
    setStockQuantity((previous) => ({
      ...previous,
      [productId]: value,
    }));
  };

  // =========================================================
  // ADD STOCK
  // =========================================================

  const handleAddStock = async (productId) => {
    const quantity = Number(stockQuantity[productId]);

    if (!quantity || quantity <= 0) {
      setError("Please enter a valid stock quantity.");
      return;
    }

    try {
      setProcessingId(productId);
      setError("");

      await inventoryService.addStock(productId, quantity);

      setStockQuantity((previous) => ({
        ...previous,
        [productId]: "",
      }));

      await loadData();
    } catch (err) {
      console.error("Failed to add stock:", err);

      setError(err.response?.data?.message || "Failed to add stock");
    } finally {
      setProcessingId(null);
    }
  };

  // =========================================================
  // REMOVE STOCK
  // =========================================================

  const handleRemoveStock = async (productId) => {
    const quantity = Number(stockQuantity[productId]);

    if (!quantity || quantity <= 0) {
      setError("Please enter a valid stock quantity.");
      return;
    }

    try {
      setProcessingId(productId);
      setError("");

      await inventoryService.removeStock(productId, quantity);

      setStockQuantity((previous) => ({
        ...previous,
        [productId]: "",
      }));

      await loadData();
    } catch (err) {
      console.error("Failed to remove stock:", err);

      setError(err.response?.data?.message || "Failed to remove stock");
    } finally {
      setProcessingId(null);
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
  // PAGE
  // =========================================================

  return (
    <div className="container mt-5 mb-5">
      {/* HEADER */}

      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="fw-bold">Seller Inventory</h2>

          <p className="text-muted mb-0">Manage stock for your products</p>
        </div>

        <button className="btn btn-outline-primary" onClick={loadData}>
          Refresh
        </button>
      </div>

      {/* ERROR */}

      {error && <div className="alert alert-danger">{error}</div>}

      {/* INVENTORY */}

      {inventory.length === 0 ? (
        <div className="alert alert-info">No inventory found.</div>
      ) : (
        <div className="card shadow-sm">
          <div className="card-header">
            <h5 className="mb-0">Inventory ({inventory.length})</h5>
          </div>

          <div className="card-body p-0">
            <div className="table-responsive">
              <table className="table table-hover align-middle mb-0">
                <thead className="table-light">
                  <tr>
                    <th>ID</th>
                    <th>Product</th>
                    <th>Available</th>
                    <th>Reserved</th>
                    <th>Reorder Level</th>
                    <th>Status</th>
                    <th>Stock</th>
                    <th>Action</th>
                  </tr>
                </thead>

                <tbody>
                  {inventory.map((item) => {
                    const quantity = item.availableQuantity ?? 0;

                    const isLowStock = item.lowStock === true;

                    const isProcessing = processingId === item.productId;

                    return (
                      <tr key={item.id}>
                        {/* ID */}

                        <td>
                          <strong>#{item.id}</strong>
                        </td>

                        {/* PRODUCT */}

                        <td>
                          <strong>{getProductName(item.productId)}</strong>

                          <br />

                          <small className="text-muted">
                            Product ID: #{item.productId}
                          </small>
                        </td>

                        {/* AVAILABLE */}

                        <td>
                          <strong>{quantity}</strong>
                        </td>

                        {/* RESERVED */}

                        <td>{item.reservedQuantity ?? 0}</td>

                        {/* REORDER LEVEL */}

                        <td>{item.reorderLevel ?? 0}</td>

                        {/* STATUS */}

                        <td>
                          {item.enabled ? (
                            <span className="badge bg-success">Enabled</span>
                          ) : (
                            <span className="badge bg-secondary">Disabled</span>
                          )}

                          <br />

                          {isLowStock ? (
                            <span className="badge bg-warning text-dark mt-1">
                              Low Stock
                            </span>
                          ) : (
                            <span className="badge bg-success mt-1">
                              Normal
                            </span>
                          )}
                        </td>

                        {/* STOCK INPUT */}

                        <td>
                          <input
                            type="number"
                            min="1"
                            className="form-control"
                            style={{ width: "100px" }}
                            value={stockQuantity[item.productId] ?? ""}
                            onChange={(e) =>
                              handleQuantityChange(
                                item.productId,
                                e.target.value,
                              )
                            }
                            disabled={isProcessing}
                          />
                        </td>

                        {/* ACTION */}

                        <td>
                          <div className="d-flex gap-2">
                            <button
                              className="btn btn-sm btn-success"
                              onClick={() => handleAddStock(item.productId)}
                              disabled={isProcessing}
                            >
                              {isProcessing ? "..." : "Add"}
                            </button>

                            <button
                              className="btn btn-sm btn-outline-danger"
                              onClick={() => handleRemoveStock(item.productId)}
                              disabled={isProcessing}
                            >
                              Remove
                            </button>
                          </div>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default SellerInventory;
