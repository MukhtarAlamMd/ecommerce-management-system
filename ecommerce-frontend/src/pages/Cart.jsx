import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import cartService from "../services/cartService";

const Cart = () => {
  const navigate = useNavigate();

  const [cartItems, setCartItems] = useState([]);

  // =========================================================
  // LOAD CART
  // =========================================================

  useEffect(() => {
    setCartItems(cartService.getCart());
  }, []);

  // =========================================================
  // REMOVE PRODUCT
  // =========================================================

  const handleRemove = (productId) => {
    const updatedCart = cartService.removeFromCart(productId);

    setCartItems(updatedCart);
  };

  // =========================================================
  // INCREASE QUANTITY
  // =========================================================

  const increaseQuantity = (productId, currentQuantity) => {
    const updatedCart = cartService.updateQuantity(
      productId,
      currentQuantity + 1,
    );

    setCartItems(updatedCart);
  };

  // =========================================================
  // DECREASE QUANTITY
  // =========================================================

  const decreaseQuantity = (productId, currentQuantity) => {
    if (currentQuantity <= 1) {
      return;
    }

    const updatedCart = cartService.updateQuantity(
      productId,
      currentQuantity - 1,
    );

    setCartItems(updatedCart);
  };

  // =========================================================
  // TOTAL
  // =========================================================

  const subtotal = cartItems.reduce(
    (sum, item) => sum + Number(item.price || 0) * Number(item.quantity || 0),
    0,
  );

  const totalItems = cartItems.reduce(
    (sum, item) => sum + Number(item.quantity || 0),
    0,
  );

  const deliveryCharge = subtotal >= 999 ? 0 : 99;

  const total = subtotal + deliveryCharge;

  // =========================================================
  // CHECKOUT
  // =========================================================

  const handleCheckout = () => {
    navigate("/checkout");
  };

  // =========================================================
  // EMPTY CART
  // =========================================================

  if (cartItems.length === 0) {
    return (
      <div className="container py-5">
        <div
          className="text-center shadow-sm rounded p-5"
          style={{
            maxWidth: "600px",
            margin: "50px auto",
            backgroundColor: "#fff",
          }}
        >
          <div style={{ fontSize: "80px" }}>🛒</div>

          <h2 className="mt-3">Your Cart is Empty</h2>

          <p className="text-muted mb-4">
            Looks like you haven't added anything to your cart yet.
          </p>

          <button
            className="btn btn-primary btn-lg px-4"
            onClick={() => navigate("/products")}
          >
            🛍️ Continue Shopping
          </button>
        </div>
      </div>
    );
  }

  // =========================================================
  // CART PAGE
  // =========================================================

  return (
    <div className="container py-5">
      {/* =====================================================
          PAGE HEADER
      ===================================================== */}

      <div className="d-flex justify-content-between align-items-center mb-4 flex-wrap gap-3">
        <div>
          <h1 className="fw-bold mb-1">🛒 Shopping Cart</h1>

          <p className="text-muted mb-0">
            {totalItems} item{totalItems !== 1 ? "s" : ""} in your cart
          </p>
        </div>

        <button
          className="btn btn-outline-primary"
          onClick={() => navigate("/products")}
        >
          ← Continue Shopping
        </button>
      </div>

      <div className="row g-4">
        {/* ===================================================
            CART ITEMS
        =================================================== */}

        <div className="col-lg-8">
          {cartItems.map((item) => (
            <div className="card shadow-sm border-0 mb-3" key={item.id}>
              <div className="card-body p-4">
                <div className="row align-items-center g-3">
                  {/* =========================================
                      PRODUCT IMAGE
                  ========================================= */}

                  <div className="col-md-2 text-center">
                    {item.imageUrl ? (
                      <img
                        src={`http://localhost:8080${item.imageUrl}`}
                        alt={item.name}
                        className="img-fluid rounded"
                        style={{
                          width: "90px",
                          height: "90px",
                          objectFit: "cover",
                        }}
                      />
                    ) : (
                      <div
                        className="d-flex align-items-center justify-content-center bg-light rounded"
                        style={{
                          width: "90px",
                          height: "90px",
                          fontSize: "35px",
                          margin: "auto",
                        }}
                      >
                        📦
                      </div>
                    )}
                  </div>

                  {/* =========================================
                      PRODUCT INFO
                  ========================================= */}

                  <div className="col-md-4">
                    <h5 className="fw-bold mb-2">{item.name}</h5>

                    <p className="text-muted mb-0">
                      ₹ {Number(item.price).toFixed(2)}
                    </p>
                  </div>

                  {/* =========================================
                      QUANTITY
                  ========================================= */}

                  <div className="col-md-3">
                    <label className="small text-muted mb-2">Quantity</label>

                    <div className="input-group">
                      <button
                        className="btn btn-outline-secondary"
                        onClick={() =>
                          decreaseQuantity(item.id, Number(item.quantity))
                        }
                      >
                        −
                      </button>

                      <input
                        type="text"
                        className="form-control text-center"
                        value={item.quantity}
                        readOnly
                      />

                      <button
                        className="btn btn-outline-secondary"
                        onClick={() =>
                          increaseQuantity(item.id, Number(item.quantity))
                        }
                      >
                        +
                      </button>
                    </div>
                  </div>

                  {/* =========================================
                      TOTAL
                  ========================================= */}

                  <div className="col-md-2">
                    <label className="small text-muted">Total</label>

                    <div className="fw-bold text-primary fs-5">
                      ₹{" "}
                      {(Number(item.price) * Number(item.quantity)).toFixed(2)}
                    </div>
                  </div>

                  {/* =========================================
                      REMOVE
                  ========================================= */}

                  <div className="col-md-1 text-center">
                    <button
                      className="btn btn-outline-danger"
                      title="Remove from cart"
                      onClick={() => handleRemove(item.id)}
                    >
                      🗑️
                    </button>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>

        {/* ===================================================
            ORDER SUMMARY
        =================================================== */}

        <div className="col-lg-4">
          <div
            className="card shadow-sm border-0"
            style={{
              position: "sticky",
              top: "20px",
            }}
          >
            <div className="card-body p-4">
              <h3 className="fw-bold mb-4">Order Summary</h3>

              {/* ITEMS */}

              <div className="d-flex justify-content-between mb-3">
                <span className="text-muted">Items ({totalItems})</span>

                <strong>₹ {subtotal.toFixed(2)}</strong>
              </div>

              {/* DELIVERY */}

              <div className="d-flex justify-content-between mb-3">
                <span className="text-muted">Delivery</span>

                {deliveryCharge === 0 ? (
                  <span className="text-success fw-bold">FREE</span>
                ) : (
                  <strong>₹ {deliveryCharge.toFixed(2)}</strong>
                )}
              </div>

              <hr />

              {/* TOTAL */}

              <div className="d-flex justify-content-between mb-4">
                <h4 className="fw-bold">Total</h4>

                <h4 className="fw-bold text-primary">₹ {total.toFixed(2)}</h4>
              </div>

              {/* CHECKOUT */}

              <button
                className="btn btn-success btn-lg w-100"
                onClick={handleCheckout}
              >
                Proceed to Checkout →
              </button>

              {/* SECURITY */}

              <div className="text-center mt-4 text-muted small">
                🔒 Secure Checkout
                <br />
                Your information is protected and secure.
              </div>

              {/* FREE DELIVERY MESSAGE */}

              {subtotal < 999 && (
                <div className="alert alert-light border mt-4 mb-0 text-center">
                  🚚 Add ₹ {(999 - subtotal).toFixed(2)} more for{" "}
                  <strong>FREE Delivery!</strong>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Cart;
