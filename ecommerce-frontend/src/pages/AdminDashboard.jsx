import { useCallback, useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";

import orderService from "../services/orderService";
import { useAuth } from "../context/AuthContext";

import "./AdminDashboard.css";

const AdminDashboard = () => {
  const navigate = useNavigate();
  const { user } = useAuth();

  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [error, setError] = useState("");

  /* =========================================
     LOAD ORDERS
  ========================================= */

  const loadOrders = useCallback(async () => {
    try {
      setError("");

      const data = await orderService.getAllOrders();

      setOrders(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error("Failed to load admin dashboard:", err);

      setError(err.response?.data?.message || "Failed to load dashboard data");
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  }, []);

  useEffect(() => {
    if (!user) {
      navigate("/login");
      return;
    }

    loadOrders();
  }, [user, navigate, loadOrders]);

  /* =========================================
     REFRESH
  ========================================= */

  const handleRefresh = async () => {
    setRefreshing(true);
    await loadOrders();
  };

  /* =========================================
     ORDER STATISTICS
  ========================================= */

  const statistics = useMemo(() => {
    const totalOrders = orders.length;

    const pendingOrders = orders.filter(
      (order) => order.status === "PENDING",
    ).length;

    const confirmedOrders = orders.filter(
      (order) => order.status === "CONFIRMED",
    ).length;

    const processingOrders = orders.filter(
      (order) => order.status === "PROCESSING",
    ).length;

    const shippedOrders = orders.filter(
      (order) => order.status === "SHIPPED",
    ).length;

    const deliveredOrders = orders.filter(
      (order) => order.status === "DELIVERED",
    ).length;

    const cancelledOrders = orders.filter(
      (order) => order.status === "CANCELLED",
    ).length;

    const totalSales = orders
      .filter((order) => order.status !== "CANCELLED")
      .reduce((sum, order) => sum + Number(order.totalAmount || 0), 0);

    return {
      totalOrders,
      pendingOrders,
      confirmedOrders,
      processingOrders,
      shippedOrders,
      deliveredOrders,
      cancelledOrders,
      totalSales,
    };
  }, [orders]);

  /* =========================================
     RECENT ORDERS
  ========================================= */

  const recentOrders = useMemo(() => {
    return [...orders]
      .sort((a, b) => {
        const idA = Number(a.id || 0);
        const idB = Number(b.id || 0);

        return idB - idA;
      })
      .slice(0, 5);
  }, [orders]);

  /* =========================================
     STATUS PERCENTAGES
  ========================================= */

  const orderProgress = useMemo(() => {
    const total = Math.max(orders.length, 1);

    return {
      confirmed: (statistics.confirmedOrders / total) * 100,

      processing: (statistics.processingOrders / total) * 100,

      shipped: (statistics.shippedOrders / total) * 100,

      delivered: (statistics.deliveredOrders / total) * 100,
    };
  }, [orders.length, statistics]);

  /* =========================================
     RECENT ACTIVITY
  ========================================= */

  const activities = useMemo(() => {
    return recentOrders.slice(0, 5).map((order) => {
      let text = `Order #${order.id} updated`;
      let type = "blue";

      switch (order.status) {
        case "CONFIRMED":
          text = `Payment confirmed for order #${order.id}`;
          type = "green";
          break;

        case "PROCESSING":
          text = `Order #${order.id} is processing`;
          type = "purple";
          break;

        case "SHIPPED":
          text = `Order #${order.id} has been shipped`;
          type = "orange";
          break;

        case "DELIVERED":
          text = `Order #${order.id} delivered`;
          type = "green";
          break;

        case "CANCELLED":
          text = `Order #${order.id} cancelled`;
          type = "pink";
          break;

        default:
          text = `New order #${order.id} received`;
          type = "blue";
      }

      return {
        text,
        type,
      };
    });
  }, [recentOrders]);

  /* =========================================
     FORMAT DATE
  ========================================= */

  const formatDate = (order) => {
    const dateValue = order.createdAt || order.orderDate || order.date;

    if (!dateValue) {
      return "—";
    }

    const date = new Date(dateValue);

    if (Number.isNaN(date.getTime())) {
      return "—";
    }

    return date.toLocaleDateString("en-IN", {
      day: "2-digit",
      month: "short",
      year: "numeric",
    });
  };

  /* =========================================
     USER NAME
  ========================================= */

  const adminName = user?.firstName || user?.name || user?.username || "Admin";

  /* =========================================
     STATUS CLASS
  ========================================= */

  const getStatusClass = (status) => {
    switch (status) {
      case "DELIVERED":
        return "status-delivered";

      case "CANCELLED":
        return "status-cancelled";

      case "CONFIRMED":
        return "status-confirmed";

      case "PROCESSING":
        return "status-processing";

      case "SHIPPED":
        return "status-shipped";

      case "PENDING":
        return "status-pending";

      default:
        return "status-default";
    }
  };

  /* =========================================
     LOADING
  ========================================= */

  if (!user) {
    return null;
  }

  if (loading) {
    return (
      <div className="admin-loading">
        <div className="loading-spinner"></div>

        <h4>Loading dashboard...</h4>

        <p>Preparing your store overview</p>
      </div>
    );
  }

  /* =========================================
     DASHBOARD
  ========================================= */

  return (
    <div className="admin-dashboard">
      {/* =====================================
          SIDEBAR
      ===================================== */}

      <aside className="admin-sidebar">
        {/* BRAND */}

        <div className="admin-brand">
          <div className="brand-icon">🛒</div>

          <div>
            <h2>E-Commerce</h2>
            <span>Admin Panel</span>
          </div>
        </div>

        {/* NAVIGATION */}

        <nav className="admin-navigation">
          <button
            className="admin-nav-item active"
            onClick={() => navigate("/admin")}
          >
            <span className="nav-icon">⌂</span>
            <span>Dashboard</span>
          </button>

          <button
            className="admin-nav-item"
            onClick={() => navigate("/admin/orders")}
          >
            <span className="nav-icon">🛒</span>
            <span>Orders</span>

            <span className="nav-badge">{statistics.totalOrders}</span>
          </button>

          <button
            className="admin-nav-item"
            onClick={() => navigate("/admin/products")}
          >
            <span className="nav-icon">▣</span>
            <span>Products</span>
          </button>

          <button
            className="admin-nav-item"
            onClick={() => navigate("/admin/categories")}
          >
            <span className="nav-icon">▦</span>
            <span>Categories</span>
          </button>

          <button
            className="admin-nav-item"
            onClick={() => navigate("/admin/inventory")}
          >
            <span className="nav-icon">▤</span>
            <span>Inventory</span>
          </button>

          <button
            className="admin-nav-item"
            onClick={() => navigate("/admin/payments")}
          >
            <span className="nav-icon">▭</span>
            <span>Payments</span>
          </button>

          <button
            className="admin-nav-item"
            onClick={() => navigate("/admin/users")}
          >
            <span className="nav-icon">♙</span>
            <span>Users</span>
          </button>

          <div className="nav-divider"></div>

          <button className="admin-nav-item">
            <span className="nav-icon">▥</span>
            <span>Reports</span>
          </button>

          <button className="admin-nav-item">
            <span className="nav-icon">⚙</span>
            <span>Settings</span>
          </button>
        </nav>

        {/* ADMIN ACCESS */}

        <div className="admin-access-card">
          <div className="access-crown">♛</div>

          <div>
            <strong>Admin Access</strong>
            <small>Full control over your store</small>
          </div>

          <span>›</span>
        </div>
      </aside>

      {/* =====================================
          MAIN AREA
      ===================================== */}

      <main className="admin-main">
        {/* ===================================
            TOP BAR
        =================================== */}

        <header className="admin-topbar">
          <div className="dashboard-search">
            <span>⌕</span>

            <input
              type="text"
              placeholder="Search orders, products, users..."
            />

            <kbd>Ctrl + K</kbd>
          </div>

          <div className="topbar-right">
            <button
              className="topbar-notification"
              onClick={() => navigate("/notifications")}
              title="Notifications"
            >
              ♧<span>3</span>
            </button>

            <div className="admin-user">
              <div className="admin-avatar">
                {adminName.charAt(0).toUpperCase()}
              </div>

              <div className="admin-user-info">
                <strong>{adminName}</strong>

                <small>Administrator</small>
              </div>

              <span className="user-arrow">⌄</span>
            </div>
          </div>
        </header>

        {/* ===================================
            DASHBOARD CONTENT
        =================================== */}

        <section className="dashboard-content">
          {/* WELCOME */}

          <div className="dashboard-welcome">
            <div>
              <h1>Good afternoon, {adminName}! 👋</h1>

              <p>Here's what's happening with your e-commerce store today.</p>
            </div>

            <div className="welcome-actions">
              <div className="dashboard-date">
                <span>▣</span>

                <div>
                  <strong>
                    {new Date().toLocaleDateString("en-IN", {
                      weekday: "short",
                      day: "numeric",
                      month: "short",
                      year: "numeric",
                    })}
                  </strong>

                  <small>
                    {new Date().toLocaleTimeString("en-IN", {
                      hour: "2-digit",
                      minute: "2-digit",
                    })}
                  </small>
                </div>
              </div>

              <button
                className="dashboard-refresh"
                onClick={handleRefresh}
                disabled={refreshing}
              >
                {refreshing ? (
                  <>
                    <span className="button-spinner"></span>
                    Refreshing
                  </>
                ) : (
                  <>↻ Refresh</>
                )}
              </button>
            </div>
          </div>

          {/* ERROR */}

          {error && (
            <div className="dashboard-error">
              <span>⚠</span>

              <div>
                <strong>Unable to load dashboard</strong>

                <p>{error}</p>
              </div>

              <button onClick={handleRefresh}>Try Again</button>
            </div>
          )}

          {/* =================================
              STATISTICS
          ================================= */}

          <div className="statistics-grid">
            {/* TOTAL ORDERS */}

            <div className="stat-card stat-blue">
              <div className="stat-header">
                <div className="stat-icon">🛒</div>

                <div>
                  <span>Total Orders</span>
                  <strong>{statistics.totalOrders}</strong>
                </div>
              </div>

              <div className="stat-footer">
                <span>↑ Active store orders</span>
                <small>All orders</small>
              </div>

              <div className="mini-bars">
                <i></i>
                <i></i>
                <i></i>
                <i></i>
                <i></i>
                <i></i>
              </div>
            </div>

            {/* PENDING */}

            <div className="stat-card stat-orange">
              <div className="stat-header">
                <div className="stat-icon">◷</div>

                <div>
                  <span>Pending Orders</span>
                  <strong>{statistics.pendingOrders}</strong>
                </div>
              </div>

              <div className="stat-footer">
                <span>
                  {statistics.pendingOrders === 0
                    ? "✓ No pending orders"
                    : "Requires attention"}
                </span>

                <small>Current</small>
              </div>

              <div className="mini-bars">
                <i></i>
                <i></i>
                <i></i>
                <i></i>
                <i></i>
                <i></i>
              </div>
            </div>

            {/* CONFIRMED */}

            <div className="stat-card stat-green">
              <div className="stat-header">
                <div className="stat-icon">✓</div>

                <div>
                  <span>Confirmed</span>
                  <strong>{statistics.confirmedOrders}</strong>
                </div>
              </div>

              <div className="stat-footer">
                <span>Orders confirmed</span>
                <small>Current</small>
              </div>

              <div className="mini-bars">
                <i></i>
                <i></i>
                <i></i>
                <i></i>
                <i></i>
                <i></i>
              </div>
            </div>

            {/* PROCESSING */}

            <div className="stat-card stat-purple">
              <div className="stat-header">
                <div className="stat-icon">⚙</div>

                <div>
                  <span>Processing</span>
                  <strong>{statistics.processingOrders}</strong>
                </div>
              </div>

              <div className="stat-footer">
                <span>Being processed</span>
                <small>Current</small>
              </div>

              <div className="mini-bars">
                <i></i>
                <i></i>
                <i></i>
                <i></i>
                <i></i>
                <i></i>
              </div>
            </div>

            {/* SHIPPED */}

            <div className="stat-card stat-pink">
              <div className="stat-header">
                <div className="stat-icon">🚚</div>

                <div>
                  <span>Shipped</span>
                  <strong>{statistics.shippedOrders}</strong>
                </div>
              </div>

              <div className="stat-footer">
                <span>On the way</span>
                <small>Current</small>
              </div>

              <div className="mini-bars">
                <i></i>
                <i></i>
                <i></i>
                <i></i>
                <i></i>
                <i></i>
              </div>
            </div>

            {/* SALES */}

            <div className="stat-card stat-sales">
              <div className="stat-header">
                <div className="stat-icon">₹</div>

                <div>
                  <span>Total Sales</span>

                  <strong>₹{statistics.totalSales.toFixed(2)}</strong>
                </div>
              </div>

              <div className="stat-footer">
                <span>Excluding cancelled orders</span>
                <small>Revenue</small>
              </div>

              <div className="mini-bars">
                <i></i>
                <i></i>
                <i></i>
                <i></i>
                <i></i>
                <i></i>
              </div>
            </div>
          </div>

          {/* =================================
              MIDDLE SECTION
          ================================= */}

          <div className="dashboard-middle">
            {/* ORDER OVERVIEW */}

            <div className="dashboard-card overview-card">
              <div className="card-heading">
                <div className="heading-left">
                  <div className="heading-icon">▥</div>

                  <div>
                    <h3>Orders Overview</h3>

                    <p>Current order distribution</p>
                  </div>
                </div>

                <span className="overview-label">Live Data</span>
              </div>

              {/* LARGE OVERVIEW */}

              <div className="overview-body">
                <div className="overview-total">
                  <span>Total Orders</span>

                  <strong>{statistics.totalOrders}</strong>

                  <small>Across all order statuses</small>
                </div>

                <div className="progress-section">
                  <div className="progress-row">
                    <div>
                      <span className="progress-dot confirmed"></span>
                      Confirmed
                    </div>

                    <strong>{statistics.confirmedOrders}</strong>
                  </div>

                  <div className="progress-track">
                    <div
                      className="progress-fill confirmed-fill"
                      style={{
                        width: `${orderProgress.confirmed}%`,
                      }}
                    ></div>
                  </div>

                  <div className="progress-row">
                    <div>
                      <span className="progress-dot processing"></span>
                      Processing
                    </div>

                    <strong>{statistics.processingOrders}</strong>
                  </div>

                  <div className="progress-track">
                    <div
                      className="progress-fill processing-fill"
                      style={{
                        width: `${orderProgress.processing}%`,
                      }}
                    ></div>
                  </div>

                  <div className="progress-row">
                    <div>
                      <span className="progress-dot shipped"></span>
                      Shipped
                    </div>

                    <strong>{statistics.shippedOrders}</strong>
                  </div>

                  <div className="progress-track">
                    <div
                      className="progress-fill shipped-fill"
                      style={{
                        width: `${orderProgress.shipped}%`,
                      }}
                    ></div>
                  </div>

                  <div className="progress-row">
                    <div>
                      <span className="progress-dot delivered"></span>
                      Delivered
                    </div>

                    <strong>{statistics.deliveredOrders}</strong>
                  </div>

                  <div className="progress-track">
                    <div
                      className="progress-fill delivered-fill"
                      style={{
                        width: `${orderProgress.delivered}%`,
                      }}
                    ></div>
                  </div>
                </div>
              </div>
            </div>

            {/* QUICK ACTIONS */}

            <div className="dashboard-card quick-actions-card">
              <div className="card-heading">
                <div className="heading-left">
                  <div className="heading-icon quick-icon">⚡</div>

                  <div>
                    <h3>Quick Actions</h3>

                    <p>Manage your store efficiently</p>
                  </div>
                </div>
              </div>

              <div className="quick-actions-grid">
                <button
                  className="quick-action action-blue"
                  onClick={() => navigate("/admin/orders")}
                >
                  <span>🛒</span>
                  <strong>Manage Orders</strong>
                  <b>›</b>
                </button>

                <button
                  className="quick-action action-green"
                  onClick={() => navigate("/admin/products")}
                >
                  <span>▣</span>
                  <strong>Manage Products</strong>
                  <b>›</b>
                </button>

                <button
                  className="quick-action action-purple"
                  onClick={() => navigate("/products")}
                >
                  <span>☷</span>
                  <strong>View Products</strong>
                  <b>›</b>
                </button>

                <button
                  className="quick-action action-orange"
                  onClick={() => navigate("/admin/categories")}
                >
                  <span>◆</span>
                  <strong>Categories</strong>
                  <b>›</b>
                </button>

                <button
                  className="quick-action action-pink"
                  onClick={() => navigate("/admin/inventory")}
                >
                  <span>▤</span>
                  <strong>Inventory</strong>
                  <b>›</b>
                </button>

                <button
                  className="quick-action action-light-blue"
                  onClick={() => navigate("/admin/users")}
                >
                  <span>♙</span>
                  <strong>View Users</strong>
                  <b>›</b>
                </button>
              </div>
            </div>
          </div>

          {/* =================================
              BOTTOM SECTION
          ================================= */}

          <div className="dashboard-bottom">
            {/* RECENT ORDERS */}

            <div className="dashboard-card recent-orders-card">
              <div className="card-heading">
                <div className="heading-left">
                  <div className="heading-icon">▤</div>

                  <div>
                    <h3>Recent Orders</h3>

                    <p>Latest orders from your store</p>
                  </div>
                </div>

                <button
                  className="view-all-button"
                  onClick={() => navigate("/admin/orders")}
                >
                  View All
                </button>
              </div>

              {recentOrders.length === 0 ? (
                <div className="empty-orders">
                  <div>🛒</div>

                  <h4>No orders yet</h4>

                  <p>New orders will appear here.</p>
                </div>
              ) : (
                <div className="orders-table-wrapper">
                  <table className="orders-table">
                    <thead>
                      <tr>
                        <th>#</th>
                        <th>Customer</th>
                        <th>Amount</th>
                        <th>Status</th>
                        <th>Date</th>
                        <th>Action</th>
                      </tr>
                    </thead>

                    <tbody>
                      {recentOrders.map((order) => (
                        <tr key={order.id}>
                          <td>
                            <strong>#{order.id}</strong>
                          </td>

                          <td>
                            <div className="customer-cell">
                              <div className="customer-avatar">
                                {(
                                  order.customerName ||
                                  order.customerEmail ||
                                  "C"
                                )
                                  .charAt(0)
                                  .toUpperCase()}
                              </div>

                              <span>
                                {order.customerName ||
                                  order.customerEmail ||
                                  order.userId ||
                                  "Customer"}
                              </span>
                            </div>
                          </td>

                          <td>₹{Number(order.totalAmount || 0).toFixed(2)}</td>

                          <td>
                            <span
                              className={`order-status ${getStatusClass(
                                order.status,
                              )}`}
                            >
                              {order.status}
                            </span>
                          </td>

                          <td>{formatDate(order)}</td>

                          <td>
                            <button
                              className="table-view-button"
                              onClick={() => navigate(`/admin/orders`)}
                            >
                              View
                            </button>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>

            {/* SYSTEM ACTIVITY */}

            <div className="dashboard-card activity-card">
              <div className="card-heading">
                <div className="heading-left">
                  <div className="heading-icon">♧</div>

                  <div>
                    <h3>System Activity</h3>

                    <p>Latest order updates</p>
                  </div>
                </div>

                <button
                  className="view-all-button"
                  onClick={() => navigate("/admin/orders")}
                >
                  View All
                </button>
              </div>

              <div className="activity-list">
                {activities.length === 0 ? (
                  <div className="empty-activity">No recent activity.</div>
                ) : (
                  activities.map((activity, index) => (
                    <div
                      className="activity-item"
                      key={`${activity.text}-${index}`}
                    >
                      <span className={`activity-dot ${activity.type}`}></span>

                      <span className="activity-message">{activity.text}</span>

                      <small>Recent</small>
                    </div>
                  ))
                )}
              </div>

              {/* SYSTEM STATUS */}

              <div className="system-status">
                <div className="system-status-icon">✓</div>

                <div>
                  <strong>All Systems Operational</strong>

                  <small>Your store is running smoothly</small>
                </div>

                <span>›</span>
              </div>
            </div>
          </div>
        </section>
      </main>
    </div>
  );
};

export default AdminDashboard;
