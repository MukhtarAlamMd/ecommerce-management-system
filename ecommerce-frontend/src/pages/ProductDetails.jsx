import { useEffect, useState } from "react";

import { useNavigate, useParams } from "react-router-dom";

import cartService from "../services/cartService";
import productService from "../services/productService";

const ProductDetails = () => {
  const { id } = useParams();

  const navigate = useNavigate();

  const [product, setProduct] = useState(null);

  const [loading, setLoading] = useState(true);

  const [error, setError] = useState("");

  // =========================================================
  // LOAD PRODUCT
  // =========================================================

  const loadProduct = async () => {
    try {
      setLoading(true);

      setError("");

      const data = await productService.getProductById(id);

      setProduct(data);
    } catch (err) {
      console.error("Failed to load product details:", err);

      setError("Failed to load product details.");
    } finally {
      setLoading(false);
    }
  };

  // =========================================================
  // LOAD PRODUCT
  // =========================================================

  useEffect(() => {
    loadProduct();
  }, [id]);

  // =========================================================
  // ADD TO CART
  // =========================================================

  const handleAddToCart = () => {
    cartService.addToCart(product);

    navigate("/cart");
  };

  // =========================================================
  // LOADING
  // =========================================================

  if (loading) {
    return (
      <div className="container mt-5 text-center">
        <h4>Loading product...</h4>
      </div>
    );
  }

  // =========================================================
  // ERROR
  // =========================================================

  if (error) {
    return (
      <div className="container mt-5">
        <div className="alert alert-danger">⚠️ {error}</div>

        <button
          className="btn btn-secondary"
          onClick={() => navigate("/products")}
        >
          ← Back to Products
        </button>
      </div>
    );
  }

  // =========================================================
  // PRODUCT NOT FOUND
  // =========================================================

  if (!product) {
    return (
      <div className="container mt-5">
        <div className="alert alert-warning">Product not found.</div>

        <button
          className="btn btn-secondary"
          onClick={() => navigate("/products")}
        >
          ← Back to Products
        </button>
      </div>
    );
  }

  // =========================================================
  // PRODUCT DETAILS
  // =========================================================

  return (
    <div className="container mt-5 mb-5">
      {/* BACK BUTTON */}

      <button
        className="btn btn-outline-secondary mb-4"
        onClick={() => navigate("/products")}
      >
        ← Back to Products
      </button>

      <div className="row g-4">
        {/* PRODUCT IMAGE */}

        <div className="col-md-6">
          <div
            className="
              border
              rounded
              shadow-sm
              p-4
              d-flex
              align-items-center
              justify-content-center
            "
            style={{
              minHeight: "400px",
              backgroundColor: "#f8f9fa",
            }}
          >
            {product.imageUrl ? (
              <img
                src={`http://localhost:8080${product.imageUrl}`}
                alt={product.name}
                className="img-fluid"
                style={{
                  maxHeight: "380px",
                  objectFit: "contain",
                }}
              />
            ) : (
              <div className="text-center text-muted">
                <div style={{ fontSize: "80px" }}>📦</div>

                <p>No Image Available</p>
              </div>
            )}
          </div>
        </div>

        {/* PRODUCT INFORMATION */}

        <div className="col-md-6">
          {product.categoryName && (
            <span className="badge bg-primary mb-3">
              {product.categoryName}
            </span>
          )}

          <h1 className="mb-3">{product.name}</h1>

          <p className="text-muted fs-5">
            {product.description || "No description available."}
          </p>

          <hr />

          <h2 className="text-primary mb-4">₹ {product.price}</h2>

          <div className="mb-4">
            {product.enabled ? (
              <span className="text-success fw-bold">● Available</span>
            ) : (
              <span className="text-danger fw-bold">
                ● Currently Unavailable
              </span>
            )}
          </div>

          <p className="text-muted">Product ID: #{product.id}</p>

          {/* ACTION BUTTONS */}

          <div className="d-flex gap-3 flex-wrap mt-4">
            <button
              className="btn btn-primary btn-lg"
              disabled={!product.enabled}
              onClick={handleAddToCart}
            >
              🛒 Add to Cart
            </button>

            <button
              className="btn btn-outline-primary btn-lg"
              onClick={() => navigate("/products")}
            >
              Continue Shopping
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProductDetails;
