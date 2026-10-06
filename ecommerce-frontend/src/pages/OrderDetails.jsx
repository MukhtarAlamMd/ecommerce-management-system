import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";

import orderService from "../services/orderService";

const OrderDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // =========================================================
  // LOAD ORDER
  // =========================================================

  useEffect(() => {
    const loadOrder = async () => {
      try {
        setLoading(true);
        setError("");

        const data = await orderService.getOrderById(id);

        console.log("Order details:", data);

        setOrder(data);
      } catch (err) {
        console.error("Failed to load order:", err);

        setError(err.response?.data?.message || "Failed to load order details");
      } finally {
        setLoading(false);
      }
    };

    loadOrder();
  }, [id]);

  // =========================================================
  // LOADING
  // =========================================================

  if (loading) {
    return (
      <div className="container mt-5 text-center">
        <div className="spinner-border" role="status" />

        <p className="mt-3">Loading order details...</p>
      </div>
    );
  }

  // =========================================================
  // ERROR
  // =========================================================

  if (error) {
    return (
      <div className="container mt-5">
        <div className="alert alert-danger">{error}</div>

        <button className="btn btn-primary" onClick={() => navigate("/orders")}>
          Back to My Orders
        </button>
      </div>
    );
  }

  if (!order) {
    return (
      <div className="container mt-5">
        <div className="alert alert-warning">Order not found.</div>

        <Link to="/orders" className="btn btn-primary">
          Back to My Orders
        </Link>
      </div>
    );
  }

  // =========================================================
  // CALCULATIONS
  // =========================================================

  const totalAmount = Number(order.totalAmount || order.total || 0);

  // =========================================================
  // UI
  // =========================================================

  return (
    <div className="container mt-5 mb-5">
      {/* =====================================================
          HEADER
      ===================================================== */}

      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="fw-bold">Order #{order.id}</h2>

          <p className="text-muted mb-0">Order details</p>
        </div>

        <Link to="/orders" className="btn btn-outline-secondary">
          Back to My Orders
        </Link>
      </div>

      {/* =====================================================
          ORDER SUMMARY
      ===================================================== */}

      <div className="row mb-4">
        {/* ORDER STATUS */}

        <div className="col-md-4 mb-3">
          <div className="card shadow-sm h-100">
            <div className="card-body">
              <h6 className="text-muted">Order Status</h6>

              <span
                className={`badge ${
                  order.status === "CONFIRMED"
                    ? "bg-success"
                    : order.status === "CANCELLED"
                      ? "bg-danger"
                      : "bg-warning text-dark"
                }`}
              >
                {order.status || "PENDING"}
              </span>
            </div>
          </div>
        </div>

        {/* PAYMENT METHOD */}

        <div className="col-md-4 mb-3">
          <div className="card shadow-sm h-100">
            <div className="card-body">
              <h6 className="text-muted">Payment Method</h6>

              <strong>{order.paymentMethod || "N/A"}</strong>
            </div>
          </div>
        </div>

        {/* TOTAL */}

        <div className="col-md-4 mb-3">
          <div className="card shadow-sm h-100">
            <div className="card-body">
              <h6 className="text-muted">Total Amount</h6>

              <h4 className="text-primary mb-0">₹{totalAmount.toFixed(2)}</h4>
            </div>
          </div>
        </div>
      </div>

      {/* =====================================================
          ORDER ITEMS
      ===================================================== */}

      <div className="card shadow-sm mb-4">
        <div className="card-header">
          <h5 className="mb-0">Order Items</h5>
        </div>

        <div className="card-body">
          {!order.items || order.items.length === 0 ? (
            <div className="alert alert-info mb-0">No order items found.</div>
          ) : (
            order.items.map((item, index) => {
              const price = Number(item.price || item.unitPrice || 0);

              const quantity = Number(item.quantity || 0);

              const itemTotal = price * quantity;

              return (
                <div
                  key={item.id || index}
                  className="d-flex justify-content-between align-items-center border-bottom py-3"
                >
                  <div>
                    <h6 className="mb-1">
                      {item.productName || `Product #${item.productId}`}
                    </h6>

                    <small className="text-muted">
                      Product ID: {item.productId}
                    </small>

                    <br />

                    <small className="text-muted">Quantity: {quantity}</small>
                  </div>

                  <div className="text-end">
                    <div>₹{price.toFixed(2)}</div>

                    <strong className="text-primary">
                      ₹{itemTotal.toFixed(2)}
                    </strong>
                  </div>
                </div>
              );
            })
          )}

          <div className="d-flex justify-content-between mt-4">
            <h5>Total</h5>

            <h5 className="text-primary">₹{totalAmount.toFixed(2)}</h5>
          </div>
        </div>
      </div>

      {/* =====================================================
          SHIPPING + CUSTOMER
      ===================================================== */}

      <div className="row">
        {/* SHIPPING ADDRESS */}

        <div className="col-md-6 mb-4">
          <div className="card shadow-sm h-100">
            <div className="card-header">
              <h5 className="mb-0">Shipping Address</h5>
            </div>

            <div className="card-body">
              <p className="mb-0">
                {order.shippingAddress || "No shipping address available"}
              </p>
            </div>
          </div>
        </div>

        {/* ORDER INFORMATION */}

        <div className="col-md-6 mb-4">
          <div className="card shadow-sm h-100">
            <div className="card-header">
              <h5 className="mb-0">Order Information</h5>
            </div>

            <div className="card-body">
              <p>
                <strong>Order ID:</strong> #{order.id}
              </p>

              <p>
                <strong>Status:</strong> {order.status || "N/A"}
              </p>

              <p>
                <strong>Payment Method:</strong> {order.paymentMethod || "N/A"}
              </p>

              <p className="mb-0">
                <strong>Created:</strong>{" "}
                {order.createdAt
                  ? new Date(order.createdAt).toLocaleString()
                  : "N/A"}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default OrderDetails;
