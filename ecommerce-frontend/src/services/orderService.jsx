import api from "./api";

// =====================================================
// CREATE ORDER
// =====================================================

const createOrder = async (orderData) => {
  const response = await api.post("/api/orders", orderData);

  return response.data;
};

// =====================================================
// GET MY ORDERS
// =====================================================

const getMyOrders = async () => {
  const response = await api.get("/api/orders/my-orders");

  return response.data;
};

// =====================================================
// GET ALL ORDERS
// ADMIN + SELLER
// =====================================================

const getAllOrders = async () => {
  const response = await api.get("/api/orders");

  return response.data;
};

// =====================================================
// GET ORDER BY ID
// =====================================================

const getOrderById = async (id) => {
  const response = await api.get(`/api/orders/${id}`);

  return response.data;
};

// =====================================================
// UPDATE ORDER STATUS
// ADMIN + SELLER
// =====================================================

const updateOrderStatus = async (id, status) => {
  const response = await api.patch(`/api/orders/${id}/status`, null, {
    params: {
      status,
    },
  });

  return response.data;
};

// =====================================================
// CANCEL ORDER
// =====================================================

const cancelOrder = async (id) => {
  const response = await api.patch(`/api/orders/${id}/cancel`);

  return response.data;
};

// =====================================================
// EXPORT
// =====================================================

const orderService = {
  createOrder,
  getMyOrders,
  getAllOrders,
  getOrderById,
  updateOrderStatus,
  cancelOrder,
};

export default orderService;
