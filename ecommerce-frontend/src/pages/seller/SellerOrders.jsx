import { useEffect, useState } from "react";
import orderService from "../../services/orderService";

const SellerOrders = () => {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [updatingId, setUpdatingId] = useState(null);

  // =====================================================
  // LOAD ORDERS
  // =====================================================

  const loadOrders = async () => {
    try {
      setLoading(true);
      setError("");

      const data = await orderService.getAllOrders();

      setOrders(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error("Failed to load seller orders:", err);

      setError(err.response?.data?.message || "Failed to load orders");
    } finally {
      setLoading(false);
    }
  };

  // =====================================================
  // INITIAL LOAD
  // =====================================================

  useEffect(() => {
    loadOrders();
  }, []);

  // =====================================================
  // UPDATE ORDER STATUS
  // =====================================================

  const handleStatusChange = async (orderId, status) => {
    try {
      setUpdatingId(orderId);
      setError("");

      const updatedOrder = await orderService.updateOrderStatus(
        orderId,
        status,
      );

      setOrders((currentOrders) =>
        currentOrders.map((order) =>
          order.id === orderId ? updatedOrder : order,
        ),
      );
    } catch (err) {
      console.error("Failed to update order status:", err);

      setError(err.response?.data?.message || "Failed to update order status");
    } finally {
      setUpdatingId(null);
    }
  };

  // =====================================================
  // FORMAT DATE
  // =====================================================

  const formatDate = (date) => {
    if (!date) {
      return "N/A";
    }

    return new Date(date).toLocaleString();
  };

  // =====================================================
  // STATUS BADGE
  // =====================================================

  const getStatusClass = (status) => {
    switch (status) {
      case "PENDING":
        return "bg-warning text-dark";

      case "CONFIRMED":
        return "bg-primary";

      case "PROCESSING":
        return "bg-info text-dark";

      case "SHIPPED":
        return "bg-secondary";

      case "DELIVERED":
        return "bg-success";

      case "CANCELLED":
        return "bg-danger";

      default:
        return "bg-dark";
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
    <div className="container mt-5 mb-5">
      {/* =================================================
          HEADER
      ================================================= */}

      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="fw-bold">Seller Orders</h2>

          <p className="text-muted mb-0">View and manage customer orders</p>
        </div>

        <button
          className="btn btn-outline-primary"
          onClick={loadOrders}
          disabled={loading}
        >
          Refresh
        </button>
      </div>

      {/* =================================================
          ERROR
      ================================================= */}

      {error && <div className="alert alert-danger">{error}</div>}

      {/* =================================================
          EMPTY
      ================================================= */}

      {orders.length === 0 ? (
        <div className="card shadow-sm">
          <div className="card-body text-center py-5">
            <h5>No orders found</h5>

            <p className="text-muted mb-0">There are currently no orders.</p>
          </div>
        </div>
      ) : (
        <div className="card shadow-sm">
          {/* =================================================
              CARD HEADER
          ================================================= */}

          <div className="card-header">
            <h5 className="mb-0">Orders ({orders.length})</h5>
          </div>

          {/* =================================================
              TABLE
          ================================================= */}

          <div className="card-body p-0">
            <div className="table-responsive">
              <table className="table table-hover align-middle mb-0">
                <thead className="table-light">
                  <tr>
                    <th>ID</th>
                    <th>Customer</th>
                    <th>Items</th>
                    <th>Total</th>
                    <th>Payment</th>
                    <th>Status</th>
                    <th>Created</th>
                  </tr>
                </thead>

                <tbody>
                  {orders.map((order) => (
                    <tr key={order.id}>
                      {/* ID */}

                      <td>
                        <strong>#{order.id}</strong>
                      </td>

                      {/* CUSTOMER */}

                      <td>{order.customerEmail || "N/A"}</td>

                      {/* ITEMS */}

                      <td>
                        {order.items?.length > 0 ? (
                          <div>
                            {order.items.map((item, index) => (
                              <div
                                key={item.id ?? `${order.id}-${index}`}
                                className="mb-1"
                              >
                                <strong>
                                  {item.productName ||
                                    `Product #${item.productId ?? "N/A"}`}
                                </strong>

                                <span className="text-muted">
                                  {" "}
                                  × {item.quantity ?? 0}
                                </span>
                              </div>
                            ))}
                          </div>
                        ) : (
                          <span className="text-muted">No items</span>
                        )}
                      </td>

                      {/* TOTAL */}

                      <td>
                        <strong>
                          ₹{Number(order.totalAmount || 0).toFixed(2)}
                        </strong>
                      </td>

                      {/* PAYMENT */}

                      <td>{order.paymentMethod || "N/A"}</td>

                      {/* STATUS */}

                      <td>
                        <span
                          className={`badge ${getStatusClass(order.status)}`}
                        >
                          {order.status || "N/A"}
                        </span>
                      </td>

                      {/* CREATED */}

                      <td>
                        <small>{formatDate(order.createdAt)}</small>
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

export default SellerOrders;
