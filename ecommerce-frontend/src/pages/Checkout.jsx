import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

import cartService from "../services/cartService";
import orderService from "../services/orderService";

const Checkout = () => {
  const navigate = useNavigate();
  const { user } = useAuth();

  const cart = cartService.getCart();

  const [shippingAddress, setShippingAddress] = useState("");
  const [paymentMethod, setPaymentMethod] = useState("COD");

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  // =========================================================
  // CALCULATE TOTAL
  // =========================================================

  const total = cart.reduce((sum, item) => {
    const price = Number(item.price || 0);
    const quantity = Number(item.quantity || 1);

    return sum + price * quantity;
  }, 0);

  // =========================================================
  // PLACE ORDER
  // =========================================================

  const handlePlaceOrder = async (e) => {
    e.preventDefault();

    setError("");

    // =======================================================
    // VALIDATION
    // =======================================================

    if (!user) {
      setError("Please login before placing an order.");
      return;
    }

    if (!shippingAddress.trim()) {
      setError("Shipping address is required.");
      return;
    }

    if (!cart.length) {
      setError("Your cart is empty.");
      return;
    }

    try {
      setLoading(true);

      // =====================================================
      // CREATE ORDER REQUEST
      // =====================================================

      const orderRequest = {
        shippingAddress: shippingAddress.trim(),

        paymentMethod: paymentMethod,

        items: cart.map((item) => ({
          productId: Number(item.id),
          quantity: Number(item.quantity),
        })),
      };

      console.log("=================================");
      console.log("CREATE ORDER");
      console.log("=================================");
      console.log("Order Request:", orderRequest);

      // =====================================================
      // CREATE ORDER
      //
      // Backend handles:
      // 1. Product validation
      // 2. Inventory reservation
      // 3. Order creation
      // 4. Payment creation
      // 5. Payment processing
      // 6. Order confirmation
      // =====================================================

      const createdOrder = await orderService.createOrder(orderRequest);

      console.log("Created Order:", createdOrder);

      // =====================================================
      // GET ORDER ID
      // =====================================================

      const orderId = createdOrder?.id ?? createdOrder?.orderId;

      if (!orderId) {
        throw new Error("Order was created but order ID was not returned.");
      }

      console.log("Order ID:", orderId);

      // =====================================================
      // CLEAR CART
      // =====================================================

      cartService.clearCart();

      // =====================================================
      // SUCCESS
      // =====================================================

      alert(`Order placed successfully!\n\nOrder ID: ${orderId}`);

      navigate("/orders");
    } catch (err) {
      console.error("=================================");
      console.error("CHECKOUT FAILED");
      console.error("=================================");

      console.error(err);

      const backendMessage =
        err.response?.data?.message || err.response?.data?.error;

      setError(backendMessage || err.message || "Failed to place order.");
    } finally {
      setLoading(false);
    }
  };

  // =========================================================
  // LOGIN REQUIRED
  // =========================================================

  if (!user) {
    return (
      <div className="container mt-5">
        <div className="alert alert-warning">
          Please login to continue checkout.
        </div>

        <button className="btn btn-primary" onClick={() => navigate("/login")}>
          Login
        </button>
      </div>
    );
  }

  // =========================================================
  // EMPTY CART
  // =========================================================

  if (!cart.length) {
    return (
      <div className="container mt-5">
        <div className="alert alert-info">Your cart is empty.</div>

        <button
          className="btn btn-primary"
          onClick={() => navigate("/products")}
        >
          Browse Products
        </button>
      </div>
    );
  }

  // =========================================================
  // UI
  // =========================================================

  return (
    <div className="container mt-5 mb-5">
      <h2 className="fw-bold mb-4">Checkout</h2>

      {/* =====================================================
          ERROR
      ===================================================== */}

      {error && <div className="alert alert-danger">{error}</div>}

      <div className="row">
        {/* ===================================================
            ORDER ITEMS
        =================================================== */}

        <div className="col-md-7">
          <div className="card shadow-sm mb-4">
            <div className="card-header">
              <h5 className="mb-0">Order Items</h5>
            </div>

            <div className="card-body">
              {cart.map((item) => {
                const price = Number(item.price || 0);

                const quantity = Number(item.quantity || 1);

                const itemTotal = price * quantity;

                return (
                  <div
                    key={item.id}
                    className="d-flex justify-content-between border-bottom py-3"
                  >
                    <div>
                      <h6>{item.name}</h6>

                      <small className="text-muted">Quantity: {quantity}</small>
                    </div>

                    <strong>₹{itemTotal.toFixed(2)}</strong>
                  </div>
                );
              })}

              {/* TOTAL */}

              <div className="d-flex justify-content-between mt-4">
                <h5>Total</h5>

                <h5 className="text-primary">₹{total.toFixed(2)}</h5>
              </div>
            </div>
          </div>
        </div>

        {/* ===================================================
            DELIVERY + PAYMENT
        =================================================== */}

        <div className="col-md-5">
          <div className="card shadow-sm">
            <div className="card-header">
              <h5 className="mb-0">Delivery & Payment</h5>
            </div>

            <div className="card-body">
              <form onSubmit={handlePlaceOrder}>
                {/* SHIPPING ADDRESS */}

                <div className="mb-3">
                  <label className="form-label">Shipping Address</label>

                  <textarea
                    className="form-control"
                    rows="4"
                    value={shippingAddress}
                    onChange={(e) => setShippingAddress(e.target.value)}
                    placeholder="Enter your complete shipping address"
                    required
                    disabled={loading}
                  />
                </div>

                {/* PAYMENT METHOD */}

                <div className="mb-4">
                  <label className="form-label">Payment Method</label>

                  <select
                    className="form-select"
                    value={paymentMethod}
                    onChange={(e) => setPaymentMethod(e.target.value)}
                    disabled={loading}
                  >
                    <option value="COD">Cash on Delivery</option>

                    <option value="CARD">Credit / Debit Card</option>

                    <option value="UPI">UPI</option>

                    <option value="WALLET">Wallet</option>

                    <option value="NET_BANKING">Net Banking</option>
                  </select>
                </div>

                {/* CUSTOMER */}

                <div className="alert alert-light border">
                  <small className="text-muted">Customer</small>

                  <div className="fw-bold">
                    {user.firstName
                      ? `${user.firstName} ${user.lastName || ""}`
                      : user.email}
                  </div>

                  <small>{user.email}</small>
                </div>

                {/* TOTAL */}

                <div className="d-flex justify-content-between mb-3">
                  <strong>Payable Amount</strong>

                  <strong className="text-primary">₹{total.toFixed(2)}</strong>
                </div>

                {/* PLACE ORDER */}

                <button
                  type="submit"
                  className="btn btn-success w-100"
                  disabled={loading}
                >
                  {loading
                    ? "Processing Order..."
                    : `Place Order - ₹${total.toFixed(2)}`}
                </button>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Checkout;
