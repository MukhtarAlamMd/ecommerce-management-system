import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import notificationService from "../services/notificationService";
import { useAuth } from "../context/AuthContext";

const Notifications = () => {
  const { user } = useAuth();
  const navigate = useNavigate();

  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  // =====================================================
  // USER ID
  // =====================================================

  const userId = user?.id || user?.userId;

  // =====================================================
  // LOAD NOTIFICATIONS
  // =====================================================

  const loadNotifications = async () => {
    if (!userId) {
      setError("User information not available.");
      setLoading(false);
      return;
    }

    try {
      setLoading(true);
      setError("");

      const data = await notificationService.getNotificationsByUserId(userId);

      setNotifications(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error("Failed to load notifications:", err);

      setError(err.response?.data?.message || "Failed to load notifications");
    } finally {
      setLoading(false);
    }
  };

  // =====================================================
  // INITIAL LOAD
  // =====================================================

  useEffect(() => {
    loadNotifications();
  }, [userId]);

  // =====================================================
  // MARK ONE AS READ
  // =====================================================

  const handleMarkAsRead = async (notification) => {
    if (notification.status === "READ") {
      return;
    }

    try {
      const updated = await notificationService.markAsRead(notification.id);

      setNotifications((current) =>
        current.map((item) => (item.id === notification.id ? updated : item)),
      );
    } catch (err) {
      console.error("Failed to mark notification as read:", err);
    }
  };

  // =====================================================
  // MARK ALL AS READ
  // =====================================================

  const handleMarkAllAsRead = async () => {
    if (!userId) {
      return;
    }

    try {
      await notificationService.markAllAsRead(userId);

      setNotifications((current) =>
        current.map((notification) => ({
          ...notification,
          status: "READ",
          readAt: notification.readAt || new Date().toISOString(),
        })),
      );
    } catch (err) {
      console.error("Failed to mark all notifications as read:", err);

      setError(
        err.response?.data?.message || "Failed to mark notifications as read",
      );
    }
  };

  // =====================================================
  // DELETE
  // =====================================================

  const handleDelete = async (id) => {
    try {
      await notificationService.deleteNotification(id);

      setNotifications((current) =>
        current.filter((notification) => notification.id !== id),
      );
    } catch (err) {
      console.error("Failed to delete notification:", err);

      setError(err.response?.data?.message || "Failed to delete notification");
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
  // ICON
  // =====================================================

  const getNotificationIcon = (type) => {
    switch (type) {
      case "ORDER":
        return "🛒";

      case "PAYMENT":
        return "💳";

      case "SHIPPING":
        return "🚚";

      case "DELIVERY":
        return "📦";

      case "SYSTEM":
        return "🔔";

      default:
        return "🔔";
    }
  };

  // =====================================================
  // LOADING
  // =====================================================

  if (loading) {
    return (
      <div className="container mt-5 text-center">
        <div className="spinner-border" role="status" />

        <p className="mt-3">Loading notifications...</p>
      </div>
    );
  }

  // =====================================================
  // PAGE
  // =====================================================

  return (
    <div className="container mt-5 mb-5">
      {/* HEADER */}

      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="fw-bold">Notifications</h2>

          <p className="text-muted mb-0">
            Stay updated with your orders and payments.
          </p>
        </div>

        <div>
          <button
            className="btn btn-outline-primary me-2"
            onClick={loadNotifications}
          >
            Refresh
          </button>

          {notifications.some(
            (notification) => notification.status === "UNREAD",
          ) && (
            <button className="btn btn-primary" onClick={handleMarkAllAsRead}>
              Mark All as Read
            </button>
          )}
        </div>
      </div>

      {/* ERROR */}

      {error && <div className="alert alert-danger">{error}</div>}

      {/* EMPTY */}

      {notifications.length === 0 ? (
        <div className="card shadow-sm">
          <div className="card-body text-center py-5">
            <div
              style={{
                fontSize: "50px",
              }}
            >
              🔔
            </div>

            <h5 className="mt-3">No notifications</h5>

            <p className="text-muted mb-0">
              You don't have any notifications yet.
            </p>
          </div>
        </div>
      ) : (
        <div className="row">
          {notifications.map((notification) => {
            const isUnread = notification.status === "UNREAD";

            return (
              <div className="col-12 mb-3" key={notification.id}>
                <div
                  className={`card shadow-sm ${
                    isUnread ? "border-primary" : ""
                  }`}
                >
                  <div className="card-body">
                    <div className="d-flex">
                      {/* ICON */}

                      <div
                        className="me-3"
                        style={{
                          fontSize: "30px",
                        }}
                      >
                        {getNotificationIcon(notification.type)}
                      </div>

                      {/* CONTENT */}

                      <div className="flex-grow-1">
                        <div className="d-flex justify-content-between">
                          <h5 className="fw-bold mb-1">{notification.title}</h5>

                          {isUnread && (
                            <span className="badge bg-primary">UNREAD</span>
                          )}
                        </div>

                        <p className="mb-2">{notification.message}</p>

                        <small className="text-muted">
                          {formatDate(notification.createdAt)}
                        </small>

                        {notification.orderId && (
                          <div className="mt-2">
                            <button
                              className="btn btn-sm btn-outline-secondary"
                              onClick={() =>
                                navigate(`/orders/${notification.orderId}`)
                              }
                            >
                              View Order #{notification.orderId}
                            </button>
                          </div>
                        )}
                      </div>

                      {/* ACTIONS */}

                      <div className="ms-3">
                        {isUnread && (
                          <button
                            className="btn btn-sm btn-outline-primary mb-2"
                            onClick={() => handleMarkAsRead(notification)}
                          >
                            Mark Read
                          </button>
                        )}

                        <button
                          className="btn btn-sm btn-outline-danger"
                          onClick={() => handleDelete(notification.id)}
                        >
                          Delete
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
};

export default Notifications;
