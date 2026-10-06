import api from "./api";

// =====================================================
// GET USER NOTIFICATIONS
// GET /api/notifications/user/{userId}
// =====================================================

const getNotificationsByUserId = async (userId) => {
  const response = await api.get(
    `/api/notifications/user/${encodeURIComponent(userId)}`,
  );

  return response.data;
};

// =====================================================
// GET UNREAD NOTIFICATIONS
// GET /api/notifications/user/{userId}/unread
// =====================================================

const getUnreadNotifications = async (userId) => {
  const response = await api.get(
    `/api/notifications/user/${encodeURIComponent(userId)}/unread`,
  );

  return response.data;
};

// =====================================================
// GET UNREAD COUNT
// GET /api/notifications/user/{userId}/unread/count
// =====================================================

const getUnreadNotificationCount = async (userId) => {
  const response = await api.get(
    `/api/notifications/user/${encodeURIComponent(userId)}/unread/count`,
  );

  return response.data;
};

// =====================================================
// GET NOTIFICATION BY ID
// GET /api/notifications/{id}
// =====================================================

const getNotificationById = async (id) => {
  const response = await api.get(`/api/notifications/${id}`);

  return response.data;
};

// =====================================================
// MARK ONE AS READ
// PATCH /api/notifications/{id}/read
// =====================================================

const markAsRead = async (id) => {
  const response = await api.patch(`/api/notifications/${id}/read`);

  return response.data;
};

// =====================================================
// MARK ALL AS READ
// PATCH /api/notifications/user/{userId}/read-all
// =====================================================

const markAllAsRead = async (userId) => {
  await api.patch(
    `/api/notifications/user/${encodeURIComponent(userId)}/read-all`,
  );
};

// =====================================================
// DELETE NOTIFICATION
// DELETE /api/notifications/{id}
// =====================================================

const deleteNotification = async (id) => {
  await api.delete(`/api/notifications/${id}`);
};

// =====================================================
// EXPORT
// =====================================================

const notificationService = {
  getNotificationsByUserId,
  getUnreadNotifications,
  getUnreadNotificationCount,
  getNotificationById,
  markAsRead,
  markAllAsRead,
  deleteNotification,
};

export default notificationService;
