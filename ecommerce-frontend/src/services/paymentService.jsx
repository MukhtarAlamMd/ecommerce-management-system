import api from "./api";

// =========================================================
// PAYMENT SERVICE
// =========================================================

const paymentService = {
  // =======================================================
  // CREATE PAYMENT
  // POST /api/payments
  // =======================================================

  createPayment: async (paymentData) => {
    const response = await api.post("/api/payments", paymentData);

    return response.data;
  },

  // =======================================================
  // GET PAYMENT BY ID
  // GET /api/payments/{id}
  // =======================================================

  getPaymentById: async (id) => {
    const response = await api.get(`/api/payments/${id}`);

    return response.data;
  },

  // =======================================================
  // GET ALL PAYMENTS
  // ADMIN ONLY
  // GET /api/payments
  // =======================================================

  getAllPayments: async () => {
    const response = await api.get("/api/payments");

    return response.data;
  },

  // =======================================================
  // GET PAYMENTS BY ORDER ID
  // GET /api/payments/order/{orderId}
  // =======================================================

  getPaymentsByOrderId: async (orderId) => {
    const response = await api.get(`/api/payments/order/${orderId}`);

    return response.data;
  },

  // =======================================================
  // GET PAYMENTS BY USER ID
  // GET /api/payments/user/{userId}
  // =======================================================

  getPaymentsByUserId: async (userId) => {
    const response = await api.get(`/api/payments/user/${userId}`);

    return response.data;
  },

  // =======================================================
  // PROCESS PAYMENT
  // POST /api/payments/{id}/process
  // =======================================================

  processPayment: async (id) => {
    const response = await api.post(`/api/payments/${id}/process`);

    return response.data;
  },

  // =======================================================
  // REFUND PAYMENT
  // ADMIN ONLY
  // PATCH /api/payments/{id}/refund
  // =======================================================

  refundPayment: async (id) => {
    const response = await api.patch(`/api/payments/${id}/refund`);

    return response.data;
  },

  // =======================================================
  // CANCEL PAYMENT
  // PATCH /api/payments/{id}/cancel
  // =======================================================

  cancelPayment: async (id) => {
    const response = await api.patch(`/api/payments/${id}/cancel`);

    return response.data;
  },
};

export default paymentService;
