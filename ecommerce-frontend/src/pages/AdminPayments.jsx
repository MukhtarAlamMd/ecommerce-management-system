import { useEffect, useState } from "react";
import paymentService from "../services/paymentService";

const AdminPayments = () => {
  const [payments, setPayments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // =========================================================
  // LOAD PAYMENTS
  // =========================================================

  const loadPayments = async () => {
    try {
      setLoading(true);
      setError("");

      const data = await paymentService.getAllPayments();

      console.log("Payments loaded:", data);

      setPayments(data);
    } catch (err) {
      console.error("Failed to load payments:", err);

      console.error("Status:", err.response?.status);
      console.error("Response:", err.response?.data);

      setError(
        err.response?.data?.message ||
          err.response?.data?.error ||
          "Failed to load payments",
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadPayments();
  }, []);

  // =========================================================
  // PROCESS
  // =========================================================

  const handleProcess = async (id) => {
    try {
      await paymentService.processPayment(id);

      await loadPayments();
    } catch (err) {
      console.error("Failed to process payment:", err);

      setError(err.response?.data?.message || "Failed to process payment");
    }
  };

  // =========================================================
  // REFUND
  // =========================================================

  const handleRefund = async (id) => {
    const confirmed = window.confirm(
      "Are you sure you want to refund this payment?",
    );

    if (!confirmed) {
      return;
    }

    try {
      await paymentService.refundPayment(id);

      await loadPayments();
    } catch (err) {
      console.error("Failed to refund payment:", err);

      setError(err.response?.data?.message || "Failed to refund payment");
    }
  };

  // =========================================================
  // CANCEL
  // =========================================================

  const handleCancel = async (id) => {
    const confirmed = window.confirm(
      "Are you sure you want to cancel this payment?",
    );

    if (!confirmed) {
      return;
    }

    try {
      await paymentService.cancelPayment(id);

      await loadPayments();
    } catch (err) {
      console.error("Failed to cancel payment:", err);

      setError(err.response?.data?.message || "Failed to cancel payment");
    }
  };

  // =========================================================
  // LOADING
  // =========================================================

  if (loading) {
    return (
      <div className="container mt-5 text-center">
        <div className="spinner-border" role="status" />

        <p className="mt-3">Loading payments...</p>
      </div>
    );
  }

  // =========================================================
  // UI
  // =========================================================

  return (
    <div className="container mt-5 mb-5">
      {/* HEADER */}

      <div className="mb-4">
        <h2 className="fw-bold">Manage Payments</h2>

        <p className="text-muted">View and manage customer payments</p>
      </div>

      {/* ERROR */}

      {error && <div className="alert alert-danger">{error}</div>}

      {/* TABLE */}

      <div className="card shadow-sm">
        <div className="card-header">
          <strong>Payments ({payments.length})</strong>
        </div>

        <div className="table-responsive">
          <table className="table table-hover mb-0">
            <thead>
              <tr>
                <th>ID</th>
                <th>Order ID</th>
                <th>User ID</th>
                <th>Email</th>
                <th>Amount</th>
                <th>Method</th>
                <th>Status</th>
                <th>Transaction</th>
                <th>Created</th>
                <th>Actions</th>
              </tr>
            </thead>

            <tbody>
              {payments.length === 0 ? (
                <tr>
                  <td colSpan="10" className="text-center py-4">
                    No payments found.
                  </td>
                </tr>
              ) : (
                payments.map((payment) => (
                  <tr key={payment.id}>
                    <td>#{payment.id}</td>

                    <td>#{payment.orderId}</td>

                    <td>#{payment.userId}</td>

                    <td>{payment.customerEmail}</td>

                    <td>₹{Number(payment.amount).toFixed(2)}</td>

                    <td>{payment.paymentMethod}</td>

                    <td>
                      {payment.paymentStatus === "SUCCESS" && (
                        <span className="badge bg-success">SUCCESS</span>
                      )}

                      {payment.paymentStatus === "PENDING" && (
                        <span className="badge bg-warning text-dark">
                          PENDING
                        </span>
                      )}

                      {payment.paymentStatus === "FAILED" && (
                        <span className="badge bg-danger">FAILED</span>
                      )}

                      {payment.paymentStatus === "REFUNDED" && (
                        <span className="badge bg-info text-dark">
                          REFUNDED
                        </span>
                      )}

                      {payment.paymentStatus === "CANCELLED" && (
                        <span className="badge bg-secondary">CANCELLED</span>
                      )}
                    </td>

                    <td>{payment.transactionId || "-"}</td>

                    <td>
                      {payment.createdAt
                        ? new Date(payment.createdAt).toLocaleString()
                        : "-"}
                    </td>

                    <td>
                      {payment.paymentStatus === "PENDING" && (
                        <>
                          <button
                            className="btn btn-sm btn-outline-success me-2"
                            onClick={() => handleProcess(payment.id)}
                          >
                            Process
                          </button>

                          <button
                            className="btn btn-sm btn-outline-danger"
                            onClick={() => handleCancel(payment.id)}
                          >
                            Cancel
                          </button>
                        </>
                      )}

                      {payment.paymentStatus === "SUCCESS" && (
                        <button
                          className="btn btn-sm btn-outline-danger"
                          onClick={() => handleRefund(payment.id)}
                        >
                          Refund
                        </button>
                      )}

                      {payment.paymentStatus === "REFUNDED" && (
                        <span className="text-muted">Refunded</span>
                      )}

                      {payment.paymentStatus === "CANCELLED" && (
                        <span className="text-muted">Cancelled</span>
                      )}
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

export default AdminPayments;
