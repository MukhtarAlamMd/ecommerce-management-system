import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import { useAuth } from "../context/AuthContext";
import orderService from "../services/orderService";

const MyOrders = () => {
  const { user } = useAuth();
  const navigate = useNavigate();

  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // =========================================================
  // LOAD MY ORDERS
  // =========================================================

  useEffect(() => {
    const loadOrders = async () => {
      if (!user?.email) {
        setLoading(false);
        return;
      }

      try {
        setLoading(true);
        setError("");

        const data = await orderService.getMyOrders(user.email);

        console.log("My orders:", data);

        setOrders(data || []);
      } catch (err) {
        console.error("Failed to load orders:", err);

        setError(err.response?.data?.message || "Failed to load your orders");
      } finally {
        setLoading(false);
      }
    };

    loadOrders();
  }, [user]);

  // =========================================================
  // LOADING
  // =========================================================

  if (loading) {
    return (
      <div className="container mt-5 text-center">
        <div className="spinner-border" role="status" />

        <p className="mt-3">Loading your orders...</p>
      </div>
    );
  }

  // =========================================================
  // UI
  // =========================================================

  return (
    <div className="container mt-5 mb-5">
      <div className="mb-4">
        <h2 className="fw-bold">My Orders</h2>

        <p className="text-muted">View your orders and payment details</p>
      </div>

      {/* ERROR */}

      {error && <div className="alert alert-danger">{error}</div>}

      {/* NO ORDERS */}

      {!error && orders.length === 0 && (
        <div className="card shadow-sm">
          <div className="card-body text-center py-5">
            <h4>No orders found</h4>

            <p className="text-muted">You haven't placed any orders yet.</p>

            <button
              className="btn btn-primary"
              onClick={() => navigate("/products")}
            >
              Start Shopping
            </button>
          </div>
        </div>
      )}

      {/* ORDERS */}

      {orders.length > 0 && (
        <div className="card shadow-sm">
          <div className="card-header">
            <strong>Orders ({orders.length})</strong>
          </div>

          <div className="table-responsive">
            <table className="table table-hover mb-0">
              <thead>
                <tr>
                  <th>Order ID</th>
                  <th>Items</th>
                  <th>Total</th>
                  <th>Payment</th>
                  <th>Status</th>
                  <th>Created</th>
                  <th>Action</th>
                </tr>
              </thead>

              <tbody>
                {orders.map((order) => (
                  <tr key={order.id}>
                    <td>
                      <strong>#{order.id}</strong>
                    </td>

                    <td>{order.items?.length || 0}</td>

                    <td>₹{Number(order.totalAmount || 0).toFixed(2)}</td>

                    <td>{order.paymentMethod || "N/A"}</td>

                    <td>
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
                    </td>

                    <td>
                      {order.createdAt
                        ? new Date(order.createdAt).toLocaleString()
                        : "N/A"}
                    </td>

                    <td>
                      <button
                        className="btn btn-sm btn-outline-primary"
                        onClick={() => navigate(`/orders/${order.id}`)}
                      >
                        View
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
};

export default MyOrders;
