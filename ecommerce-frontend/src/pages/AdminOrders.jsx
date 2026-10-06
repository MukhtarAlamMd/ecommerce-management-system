import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import orderService from "../services/orderService";

const AdminOrders = () => {
  const navigate = useNavigate();

  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [updatingId, setUpdatingId] = useState(null);

  // =====================================================
  // LOAD ALL ORDERS
  // =====================================================

  const loadOrders = async () => {
    try {
      setLoading(true);
      setError("");

      const data = await orderService.getAllOrders();

      setOrders(data);
    } catch (err) {
      console.error("Failed to load orders:", err);

      setError(err.response?.data?.message || "Failed to load orders");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadOrders();
  }, []);

  // =====================================================
  // UPDATE ORDER STATUS
  // =====================================================

  const handleStatusChange = async (id, status) => {
    try {
      setUpdatingId(id);
      setError("");

      await orderService.updateOrderStatus(id, status);

      await loadOrders();
    } catch (err) {
      console.error("Failed to update order status:", err);

      setError(err.response?.data?.message || "Failed to update order status");
    } finally {
      setUpdatingId(null);
    }
  };

  // =====================================================
  // CANCEL ORDER
  // =====================================================

  const handleCancelOrder = async (id) => {
    const confirmed = window.confirm(
      "Are you sure you want to cancel this order?",
    );

    if (!confirmed) {
      return;
    }

    try {
      setUpdatingId(id);
      setError("");

      await orderService.cancelOrder(id);

      await loadOrders();
    } catch (err) {
      console.error("Failed to cancel order:", err);

      setError(err.response?.data?.message || "Failed to cancel order");
    } finally {
      setUpdatingId(null);
    }
  };

  // =====================================================
  // STATUS BADGE
  // =====================================================

  const getStatusClass = (status) => {
    switch (status) {
      case "PENDING":
        return "bg-warning text-dark";

      case "CONFIRMED":
        return "bg-info text-dark";

      case "PROCESSING":
        return "bg-primary";

      case "SHIPPED":
        return "bg-secondary";

      case "DELIVERED":
        return "bg-success";

      case "CANCELLED":
        return "bg-danger";

      default:
        return "bg-secondary";
    }
  };

  // =====================================================
  // LOADING
  // =====================================================

  if (loading) {
    return (
      <div className="container mt-5 text-center">
        <div className="spinner-border" role="status" />

        <p className="mt-3">Loading orders...</p>
      </div>
    );
  }

  // =====================================================
  // PAGE
  // =====================================================

  return (
    <div className="container mt-5">
      {/* HEADER */}

      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="fw-bold">Manage Orders</h2>

          <p className="text-muted mb-0">View and manage customer orders</p>
        </div>

        <div className="d-flex gap-2">
          <button className="btn btn-outline-primary" onClick={loadOrders}>
            Refresh
          </button>

          <button
            className="btn btn-outline-secondary"
            onClick={() => navigate("/admin")}
          >
            Dashboard
          </button>
        </div>
      </div>

      {/* ERROR */}

      {error && <div className="alert alert-danger">{error}</div>}

      {/* EMPTY */}

      {!orders.length ? (
        <div className="alert alert-info">No orders found.</div>
      ) : (
        <div className="card shadow-sm">
          <div className="card-header">
            <h5 className="mb-0">All Orders ({orders.length})</h5>
          </div>

          <div className="card-body p-0">
            <div className="table-responsive">
              <table className="table table-hover mb-0 align-middle">
                <thead className="table-light">
                  <tr>
                    <th>Order ID</th>
                    <th>Customer</th>
                    <th>Total</th>
                    <th>Status</th>
                    <th>Created</th>
                    <th>Actions</th>
                  </tr>
                </thead>

                <tbody>
                  {orders.map((order) => (
                    <tr key={order.id}>
                      {/* ORDER ID */}

                      <td>
                        <strong>#{order.id}</strong>
                      </td>

                      {/* CUSTOMER */}

                      <td>{order.customerEmail}</td>

                      {/* TOTAL */}

                      <td>
                        <strong>
                          ₹{Number(order.totalAmount || 0).toFixed(2)}
                        </strong>
                      </td>

                      {/* STATUS */}

                      <td>
                        <span
                          className={`badge ${getStatusClass(order.status)}`}
                        >
                          {order.status}
                        </span>
                      </td>

                      {/* CREATED */}

                      <td>
                        {order.createdAt
                          ? new Date(order.createdAt).toLocaleString()
                          : "N/A"}
                      </td>

                      {/* ACTIONS */}

                      <td>
                        <div className="d-flex flex-column gap-2">
                          {/* STATUS */}

                          <select
                            className="form-select form-select-sm"
                            value={order.status}
                            disabled={
                              updatingId === order.id ||
                              order.status === "CANCELLED"
                            }
                            onChange={(e) =>
                              handleStatusChange(order.id, e.target.value)
                            }
                          >
                            <option value="PENDING">PENDING</option>

                            <option value="CONFIRMED">CONFIRMED</option>

                            <option value="PROCESSING">PROCESSING</option>

                            <option value="SHIPPED">SHIPPED</option>

                            <option value="DELIVERED">DELIVERED</option>

                            <option value="CANCELLED">CANCELLED</option>
                          </select>

                          {/* CANCEL */}

                          {order.status !== "CANCELLED" &&
                            order.status !== "DELIVERED" && (
                              <button
                                className="btn btn-sm btn-outline-danger"
                                disabled={updatingId === order.id}
                                onClick={() => handleCancelOrder(order.id)}
                              >
                                {updatingId === order.id
                                  ? "Updating..."
                                  : "Cancel"}
                              </button>
                            )}
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

export default AdminOrders;
