import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import paymentService from "../services/paymentService";

const MyPayments = () => {
  const { user } = useAuth();

  const [payments, setPayments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // =========================================================
  // LOAD MY PAYMENTS
  // =========================================================

  useEffect(() => {
    const loadPayments = async () => {
      try {
        setLoading(true);
        setError("");

        // IMPORTANT:
        // Use the logged-in user's ID.
        const userId = user?.id;

        if (!userId) {
          setError("User ID not found");
          return;
        }

        const data = await paymentService.getPaymentsByUserId(userId);

        console.log("My payments:", data);

        setPayments(data);
      } catch (err) {
        console.error("Failed to load payments:", err);

        setError(
          err.response?.data?.message ||
            err.response?.data?.error ||
            "Failed to load payments",
        );
      } finally {
        setLoading(false);
      }
    };

    if (user) {
      loadPayments();
    }
  }, [user]);

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
  // ERROR
  // =========================================================

  if (error) {
    return (
      <div className="container mt-5">
        <div className="alert alert-danger">{error}</div>
      </div>
    );
  }

  // =========================================================
  // UI
  // =========================================================

  return (
    <div className="container mt-5 mb-5">
      <div className="mb-4">
        <h2 className="fw-bold">My Payments</h2>

        <p className="text-muted">View your payment history</p>
      </div>

      {/* =====================================================
          NO PAYMENTS
      ===================================================== */}

      {payments.length === 0 ? (
        <div className="alert alert-info">No payments found.</div>
      ) : (
        <div className="card shadow-sm">
          <div className="card-header">
            <strong>Payment History ({payments.length})</strong>
          </div>

          <div className="table-responsive">
            <table className="table table-hover mb-0">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Order ID</th>
                  <th>Amount</th>
                  <th>Method</th>
                  <th>Status</th>
                  <th>Transaction ID</th>
                  <th>Date</th>
                </tr>
              </thead>

              <tbody>
                {payments.map((payment) => (
                  <tr key={payment.id}>
                    <td>#{payment.id}</td>

                    <td>#{payment.orderId}</td>

                    <td className="fw-bold">
                      ₹{Number(payment.amount || 0).toFixed(2)}
                    </td>

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
                        <span className="badge bg-info">REFUNDED</span>
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

export default MyPayments;
