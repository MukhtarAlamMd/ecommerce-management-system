import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import productService from "../services/productService";
import inventoryService from "../services/inventoryService";

const AdminProducts = () => {
  const navigate = useNavigate();

  // =========================================================
  // STATE
  // =========================================================

  const [products, setProducts] = useState([]);
  const [inventoryMap, setInventoryMap] = useState({});

  const [loading, setLoading] = useState(true);
  const [deletingId, setDeletingId] = useState(null);

  const [error, setError] = useState("");

  // Selected image for each product
  const [selectedImages, setSelectedImages] = useState({});

  // Preview URL for each product
  const [previewUrls, setPreviewUrls] = useState({});

  // Uploading state for each product
  const [uploadingImage, setUploadingImage] = useState({});

  // Image upload message for each product
  const [imageMessages, setImageMessages] = useState({});

  // =========================================================
  // LOAD PRODUCTS + INVENTORY
  // =========================================================

  const loadData = async () => {
    try {
      setLoading(true);
      setError("");

      const [productData, inventoryData] = await Promise.all([
        productService.getAllProducts(),
        inventoryService.getAllInventory(),
      ]);

      console.log("PRODUCTS:", productData);
      console.log("INVENTORY:", inventoryData);

      // -------------------------------------------------------
      // PRODUCT LIST
      // -------------------------------------------------------

      setProducts(Array.isArray(productData) ? productData : []);

      // -------------------------------------------------------
      // INVENTORY MAP
      // productId -> inventory
      // -------------------------------------------------------

      const map = {};

      if (Array.isArray(inventoryData)) {
        inventoryData.forEach((inventory) => {
          map[inventory.productId] = inventory;
        });
      }

      console.log("INVENTORY MAP:", map);

      setInventoryMap(map);
    } catch (err) {
      console.error("Failed to load products/inventory:", err);

      setError(err.response?.data?.message || "Failed to load products");
    } finally {
      setLoading(false);
    }
  };

  // =========================================================
  // INITIAL LOAD
  // =========================================================

  useEffect(() => {
    loadData();
  }, []);

  // =========================================================
  // IMAGE SELECT
  // =========================================================

  const handleImageSelect = (productId, file) => {
    if (!file) {
      return;
    }

    // -------------------------------------------------------
    // VALIDATE FILE TYPE
    // -------------------------------------------------------

    if (!file.type.startsWith("image/")) {
      setImageMessages((prev) => ({
        ...prev,
        [productId]: "Please select a valid image file.",
      }));

      return;
    }

    // -------------------------------------------------------
    // VALIDATE FILE SIZE
    // Maximum = 5 MB
    // -------------------------------------------------------

    const maxSize = 5 * 1024 * 1024;

    if (file.size > maxSize) {
      setImageMessages((prev) => ({
        ...prev,
        [productId]: "Image size must be less than 5 MB.",
      }));

      return;
    }

    // -------------------------------------------------------
    // CREATE PREVIEW URL
    // -------------------------------------------------------

    const previewUrl = URL.createObjectURL(file);

    // Revoke previous preview URL if it exists
    if (previewUrls[productId]) {
      URL.revokeObjectURL(previewUrls[productId]);
    }

    setSelectedImages((prev) => ({
      ...prev,
      [productId]: file,
    }));

    setPreviewUrls((prev) => ({
      ...prev,
      [productId]: previewUrl,
    }));

    setImageMessages((prev) => ({
      ...prev,
      [productId]: "",
    }));
  };

  // =========================================================
  // UPLOAD IMAGE
  // =========================================================

  const handleImageUpload = async (productId) => {
    const file = selectedImages[productId];

    if (!file) {
      setImageMessages((prev) => ({
        ...prev,
        [productId]: "Please select an image first.",
      }));

      return;
    }

    try {
      setUploadingImage((prev) => ({
        ...prev,
        [productId]: true,
      }));

      setImageMessages((prev) => ({
        ...prev,
        [productId]: "",
      }));

      // -------------------------------------------------------
      // UPLOAD
      // -------------------------------------------------------

      await productService.uploadProductImage(productId, file);

      // -------------------------------------------------------
      // SUCCESS
      // -------------------------------------------------------

      setImageMessages((prev) => ({
        ...prev,
        [productId]: "Image uploaded successfully!",
      }));

      // Remove selected image
      setSelectedImages((prev) => {
        const updated = { ...prev };
        delete updated[productId];
        return updated;
      });

      // Remove preview
      if (previewUrls[productId]) {
        URL.revokeObjectURL(previewUrls[productId]);
      }

      setPreviewUrls((prev) => {
        const updated = { ...prev };
        delete updated[productId];
        return updated;
      });

      // Reload products so new imageUrl appears
      await loadData();
    } catch (err) {
      console.error("Image upload failed:", err);

      setImageMessages((prev) => ({
        ...prev,
        [productId]: err.response?.data?.message || "Failed to upload image.",
      }));
    } finally {
      setUploadingImage((prev) => ({
        ...prev,
        [productId]: false,
      }));
    }
  };

  // =========================================================
  // DELETE PRODUCT
  // =========================================================

  const handleDelete = async (id) => {
    const confirmed = window.confirm(
      "Are you sure you want to delete this product?",
    );

    if (!confirmed) {
      return;
    }

    try {
      setDeletingId(id);
      setError("");

      await productService.deleteProduct(id);

      await loadData();
    } catch (err) {
      console.error("Failed to delete product:", err);

      setError(err.response?.data?.message || "Failed to delete product");
    } finally {
      setDeletingId(null);
    }
  };

  // =========================================================
  // IMAGE URL
  // =========================================================

  const getImageUrl = (product) => {
    if (!product.imageUrl) {
      return null;
    }

    // Backend already returned complete URL
    if (
      product.imageUrl.startsWith("http://") ||
      product.imageUrl.startsWith("https://")
    ) {
      return product.imageUrl;
    }

    // IMPORTANT:
    // Relative image URLs must go through API Gateway.
    //
    // Example:
    // /uploads/products/abc.jpg
    //
    // becomes:
    // http://localhost:8080/uploads/products/abc.jpg

    return `http://localhost:8080${product.imageUrl}`;
  };

  // =========================================================
  // LOADING
  // =========================================================

  if (loading) {
    return (
      <div className="container mt-5 text-center">
        <div className="spinner-border" role="status" />

        <p className="mt-3">Loading products...</p>
      </div>
    );
  }

  // =========================================================
  // PAGE
  // =========================================================

  return (
    <div className="container-fluid mt-4 mb-5 px-4">
      {/* =====================================================
          HEADER
      ===================================================== */}

      <div className="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
        <div>
          <h2 className="fw-bold mb-1">Manage Products</h2>

          <p className="text-muted mb-0">
            Create, edit, manage stock and product photos
          </p>
        </div>

        <div className="d-flex gap-2 flex-wrap">
          <button
            className="btn btn-outline-secondary"
            onClick={() => navigate("/admin")}
          >
            Dashboard
          </button>

          <button className="btn btn-outline-primary" onClick={loadData}>
            Refresh
          </button>

          <button
            className="btn btn-primary"
            onClick={() => navigate("/admin/products/create")}
          >
            + Create Product
          </button>
        </div>
      </div>

      {/* =====================================================
          ERROR
      ===================================================== */}

      {error && <div className="alert alert-danger">{error}</div>}

      {/* =====================================================
          EMPTY
      ===================================================== */}

      {!products.length ? (
        <div className="card shadow-sm">
          <div className="card-body text-center py-5">
            <div
              style={{
                fontSize: "45px",
                marginBottom: "12px",
              }}
            >
              📦
            </div>

            <h5>No products found</h5>

            <p className="text-muted">Create your first product.</p>

            <button
              className="btn btn-primary"
              onClick={() => navigate("/admin/products/create")}
            >
              Create Product
            </button>
          </div>
        </div>
      ) : (
        /* =====================================================
           PRODUCT TABLE
        ===================================================== */

        <div className="card shadow-sm border-0">
          {/* CARD HEADER */}

          <div className="card-header bg-white py-3">
            <div className="d-flex justify-content-between align-items-center">
              <h5 className="mb-0 fw-bold">All Products ({products.length})</h5>
            </div>
          </div>

          {/* TABLE */}

          <div className="card-body p-0">
            <div className="table-responsive">
              <table className="table table-hover align-middle mb-0">
                <thead className="table-light">
                  <tr>
                    <th>ID</th>

                    <th style={{ minWidth: "110px" }}>Photo</th>

                    <th style={{ minWidth: "180px" }}>Name</th>

                    <th style={{ minWidth: "250px" }}>Description</th>

                    <th>Category</th>

                    <th>Price</th>

                    <th>Stock</th>

                    <th>Status</th>

                    <th style={{ minWidth: "220px" }}>Actions</th>
                  </tr>
                </thead>

                <tbody>
                  {products.map((product) => {
                    // =================================================
                    // FIND INVENTORY
                    // =================================================

                    const inventory = inventoryMap[product.id];

                    // =================================================
                    // REAL AVAILABLE STOCK
                    // =================================================

                    const stock = inventory?.availableQuantity ?? 0;

                    // =================================================
                    // PRODUCT AVAILABILITY
                    // =================================================

                    const isAvailable =
                      product.enabled === true &&
                      inventory?.enabled === true &&
                      stock > 0;

                    // =================================================
                    // IMAGE
                    // =================================================

                    const imageUrl = getImageUrl(product);

                    const previewUrl = previewUrls[product.id];

                    const hasSelectedImage = Boolean(
                      selectedImages[product.id],
                    );

                    return (
                      <tr key={product.id}>
                        {/* =================================================
                            ID
                        ================================================= */}

                        <td>
                          <strong>#{product.id}</strong>
                        </td>

                        {/* =================================================
                            PHOTO
                        ================================================= */}

                        <td>
                          <div
                            style={{
                              width: "90px",
                              height: "90px",
                              borderRadius: "12px",
                              overflow: "hidden",
                              background: "#f6f8fc",
                              border: "1px solid #e5eaf2",
                              display: "flex",
                              alignItems: "center",
                              justifyContent: "center",
                            }}
                          >
                            {previewUrl ? (
                              <img
                                src={previewUrl}
                                alt={`Preview of ${product.name}`}
                                style={{
                                  width: "100%",
                                  height: "100%",
                                  objectFit: "cover",
                                }}
                              />
                            ) : imageUrl ? (
                              <img
                                src={imageUrl}
                                alt={product.name}
                                style={{
                                  width: "100%",
                                  height: "100%",
                                  objectFit: "cover",
                                }}
                                onError={(e) => {
                                  console.error(
                                    "Product image failed to load:",
                                    imageUrl,
                                  );

                                  e.currentTarget.style.display = "none";
                                }}
                              />
                            ) : (
                              <div
                                style={{
                                  textAlign: "center",
                                  color: "#94a3b8",
                                  fontSize: "12px",
                                }}
                              >
                                <div
                                  style={{
                                    fontSize: "25px",
                                  }}
                                >
                                  📷
                                </div>
                                No photo
                              </div>
                            )}
                          </div>

                          {/* CHOOSE IMAGE */}

                          <label
                            htmlFor={`product-image-${product.id}`}
                            className="btn btn-sm btn-outline-primary mt-2"
                            style={{
                              width: "90px",
                              cursor: "pointer",
                            }}
                          >
                            📷 Choose
                          </label>

                          <input
                            id={`product-image-${product.id}`}
                            type="file"
                            accept="image/jpeg,image/png,image/webp,image/gif"
                            hidden
                            onChange={(e) =>
                              handleImageSelect(product.id, e.target.files?.[0])
                            }
                          />

                          {/* UPLOAD BUTTON */}

                          {hasSelectedImage && (
                            <button
                              type="button"
                              className="btn btn-sm btn-primary mt-2"
                              style={{
                                width: "90px",
                              }}
                              onClick={() => handleImageUpload(product.id)}
                              disabled={uploadingImage[product.id]}
                            >
                              {uploadingImage[product.id]
                                ? "Uploading..."
                                : "⬆ Upload"}
                            </button>
                          )}

                          {/* MESSAGE */}

                          {imageMessages[product.id] && (
                            <div
                              style={{
                                width: "150px",
                                marginTop: "6px",
                                fontSize: "11px",
                                color: imageMessages[product.id].includes(
                                  "successfully",
                                )
                                  ? "#15803d"
                                  : "#dc2626",
                              }}
                            >
                              {imageMessages[product.id]}
                            </div>
                          )}
                        </td>

                        {/* =================================================
                            NAME
                        ================================================= */}

                        <td>
                          <strong>{product.name}</strong>
                        </td>

                        {/* =================================================
                            DESCRIPTION
                        ================================================= */}

                        <td>
                          <span
                            className="text-muted"
                            style={{
                              display: "inline-block",
                              maxWidth: "250px",
                            }}
                          >
                            {product.description || "N/A"}
                          </span>
                        </td>

                        {/* =================================================
                            CATEGORY
                        ================================================= */}

                        <td>
                          {product.categoryName || product.categoryId || "N/A"}
                        </td>

                        {/* =================================================
                            PRICE
                        ================================================= */}

                        <td>
                          <strong>
                            ₹{Number(product.price || 0).toFixed(2)}
                          </strong>
                        </td>

                        {/* =================================================
                            STOCK
                        ================================================= */}

                        <td>
                          <span
                            className={
                              stock > 0 ? "badge bg-success" : "badge bg-danger"
                            }
                          >
                            {stock}
                          </span>
                        </td>

                        {/* =================================================
                            STATUS
                        ================================================= */}

                        <td>
                          {isAvailable ? (
                            <span className="badge bg-success">Available</span>
                          ) : (
                            <span className="badge bg-danger">
                              Out of Stock
                            </span>
                          )}
                        </td>

                        {/* =================================================
                            ACTIONS
                        ================================================= */}

                        <td>
                          <div className="d-flex gap-2 flex-wrap">
                            <button
                              className="btn btn-sm btn-outline-primary"
                              onClick={() =>
                                navigate(`/admin/products/edit/${product.id}`)
                              }
                              disabled={deletingId === product.id}
                            >
                              Edit
                            </button>

                            <button
                              className="btn btn-sm btn-outline-danger"
                              onClick={() => handleDelete(product.id)}
                              disabled={deletingId === product.id}
                            >
                              {deletingId === product.id
                                ? "Deleting..."
                                : "Delete"}
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

export default AdminProducts;
