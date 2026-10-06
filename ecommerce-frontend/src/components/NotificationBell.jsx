import { useCallback, useEffect, useRef, useState } from "react";
import { useAuth } from "../context/AuthContext";
import notificationService from "../services/notificationService";

const NotificationBell = () => {
  const { user } = useAuth();

  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);

  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const dropdownRef = useRef(null);

  // =====================================================
  // USER ID
  // =====================================================

  const userId = user?.id || user?.userId;

  // =====================================================
  // LOAD NOTIFICATIONS
  // =====================================================

  const loadNotifications = useCallback(async () => {
    if (!userId) {
      setNotifications([]);
      setUnreadCount(0);
      return;
    }

    try {
      setLoading(true);
      setError("");

      // Get all notifications
      const data = await notificationService.getNotificationsByUserId(userId);

      setNotifications(Array.isArray(data) ? data : []);

      // Get unread count
      const count =
        await notificationService.getUnreadNotificationCount(userId);

      setUnreadCount(Number(count) || 0);
    } catch (err) {
      console.error("Failed to load notifications:", err);

      setError(err.response?.data?.message || "Failed to load notifications");
    } finally {
      setLoading(false);
    }
  }, [userId]);

  // =====================================================
  // INITIAL LOAD
  // =====================================================

  useEffect(() => {
    loadNotifications();
  }, [loadNotifications]);

  // =====================================================
  // AUTOMATIC REFRESH
  // =====================================================
  // Check for new notifications every 30 seconds.

  useEffect(() => {
    if (!userId) {
      return;
    }

    const intervalId = setInterval(() => {
      loadNotifications();
    }, 30000);

    return () => {
      clearInterval(intervalId);
    };
  }, [userId, loadNotifications]);

  // =====================================================
  // CLOSE DROPDOWN WHEN CLICKING OUTSIDE
  // =====================================================

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  // =====================================================
  // TOGGLE DROPDOWN
  // =====================================================

  const handleToggle = () => {
    setOpen((current) => !current);
  };

  // =====================================================
  // MARK ONE NOTIFICATION AS READ
  // =====================================================

  const handleNotificationClick = async (notification) => {
    if (notification.status !== "UNREAD" || !notification.id) {
      return;
    }

    try {
      setError("");

      const updated = await notificationService.markAsRead(notification.id);

      // Update notification in local state
      setNotifications((current) =>
        current.map((item) => (item.id === notification.id ? updated : item)),
      );

      // Decrease unread count
      setUnreadCount((current) => Math.max(current - 1, 0));
    } catch (err) {
      console.error("Failed to mark notification as read:", err);

      setError(
        err.response?.data?.message || "Failed to mark notification as read",
      );
    }
  };

  // =====================================================
  // MARK ALL NOTIFICATIONS AS READ
  // =====================================================

  const handleMarkAllAsRead = async () => {
    if (!userId || unreadCount === 0) {
      return;
    }

    try {
      setError("");

      await notificationService.markAllAsRead(userId);

      // Update all notifications locally
      setNotifications((current) =>
        current.map((notification) => ({
          ...notification,
          status: "READ",
          readAt: notification.readAt || new Date().toISOString(),
        })),
      );

      // Reset unread count
      setUnreadCount(0);
    } catch (err) {
      console.error("Failed to mark all notifications as read:", err);

      setError(
        err.response?.data?.message ||
          "Failed to mark all notifications as read",
      );
    }
  };

  // =====================================================
  // FORMAT DATE
  // =====================================================

  const formatDate = (date) => {
    if (!date) {
      return "";
    }

    const parsedDate = new Date(date);

    if (Number.isNaN(parsedDate.getTime())) {
      return "";
    }

    return parsedDate.toLocaleString();
  };

  // =====================================================
  // NOTIFICATION ICON
  // =====================================================

  const getNotificationIcon = (type) => {
    switch (type) {
      case "ORDER_CREATED":
        return "🛒";

      case "ORDER_CONFIRMED":
        return "✅";

      case "ORDER_SHIPPED":
        return "🚚";

      case "ORDER_DELIVERED":
        return "📦";

      case "ORDER_CANCELLED":
        return "❌";

      case "PAYMENT_SUCCESS":
        return "💳";

      case "PAYMENT_FAILED":
        return "⚠️";

      default:
        return "🔔";
    }
  };

  // =====================================================
  // DON'T SHOW FOR LOGGED OUT USER
  // =====================================================

  if (!user) {
    return null;
  }

  // =====================================================
  // UI
  // =====================================================

  return (
    <div className="position-relative ms-2" ref={dropdownRef}>
      {/* =================================================
          BELL BUTTON
      ================================================= */}

      <button
        type="button"
        className="btn btn-dark position-relative border-0"
        onClick={handleToggle}
        aria-label="Notifications"
        aria-expanded={open}
      >
        <span
          style={{
            fontSize: "22px",
            lineHeight: 1,
          }}
        >
          🔔
        </span>

        {/* UNREAD BADGE */}

        {unreadCount > 0 && (
          <span
            className="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger"
            style={{
              fontSize: "10px",
            }}
          >
            {unreadCount > 99 ? "99+" : unreadCount}
          </span>
        )}
      </button>

      {/* =================================================
          DROPDOWN
      ================================================= */}

      {open && (
        <div
          className="card shadow position-absolute end-0 mt-2"
          style={{
            width: "380px",
            maxWidth: "90vw",
            zIndex: 1050,
          }}
        >
          {/* =================================================
              HEADER
          ================================================= */}

          <div className="card-header bg-white d-flex justify-content-between align-items-center">
            <div>
              <strong>Notifications</strong>

              {unreadCount > 0 && (
                <span className="badge bg-danger ms-2">
                  {unreadCount} unread
                </span>
              )}
            </div>

            {unreadCount > 0 && (
              <button
                type="button"
                className="btn btn-sm btn-link text-decoration-none"
                onClick={handleMarkAllAsRead}
              >
                Mark all read
              </button>
            )}
          </div>

          {/* =================================================
              ERROR
          ================================================= */}

          {error && <div className="alert alert-danger m-2 mb-0">{error}</div>}

          {/* =================================================
              LOADING
          ================================================= */}

          {loading ? (
            <div className="text-center p-4">
              <div className="spinner-border spinner-border-sm" role="status" />

              <div className="mt-2 text-muted">Loading notifications...</div>
            </div>
          ) : notifications.length === 0 ? (
            /* =================================================
               EMPTY
            ================================================= */

            <div className="text-center p-4">
              <div
                style={{
                  fontSize: "35px",
                }}
              >
                🔕
              </div>

              <p className="text-muted mb-0">No notifications</p>
            </div>
          ) : (
            /* =================================================
               NOTIFICATIONS
            ================================================= */

            <div
              style={{
                maxHeight: "420px",
                overflowY: "auto",
              }}
            >
              {notifications.map((notification) => {
                const isUnread = notification.status === "UNREAD";

                return (
                  <button
                    type="button"
                    key={notification.id}
                    className={`w-100 text-start border-0 border-bottom p-3 ${
                      isUnread ? "bg-light" : "bg-white"
                    }`}
                    onClick={() => handleNotificationClick(notification)}
                    disabled={!isUnread}
                    aria-label={
                      isUnread
                        ? `Mark ${notification.title || "notification"} as read`
                        : notification.title || "Notification"
                    }
                    style={{
                      cursor: isUnread ? "pointer" : "default",
                    }}
                  >
                    <div className="d-flex">
                      {/* ICON */}

                      <div
                        className="me-3"
                        style={{
                          fontSize: "24px",
                        }}
                      >
                        {getNotificationIcon(notification.type)}
                      </div>

                      {/* CONTENT */}

                      <div className="flex-grow-1">
                        <div className="d-flex justify-content-between">
                          <strong>
                            {notification.title || "Notification"}
                          </strong>

                          {/* UNREAD DOT */}

                          {isUnread && (
                            <span
                              className="rounded-circle bg-primary"
                              style={{
                                width: "8px",
                                height: "8px",
                                marginTop: "6px",
                                flexShrink: 0,
                              }}
                            />
                          )}
                        </div>

                        <div
                          className="text-muted small mt-1"
                          style={{
                            whiteSpace: "normal",
                          }}
                        >
                          {notification.message}
                        </div>

                        <div className="text-secondary mt-2">
                          <small>{formatDate(notification.createdAt)}</small>
                        </div>
                      </div>
                    </div>
                  </button>
                );
              })}
            </div>
          )}

          {/* =================================================
              FOOTER
          ================================================= */}

          <div className="card-footer bg-white text-center">
            <button
              type="button"
              className="btn btn-sm btn-outline-secondary"
              onClick={loadNotifications}
              disabled={loading}
            >
              Refresh
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default NotificationBell;
