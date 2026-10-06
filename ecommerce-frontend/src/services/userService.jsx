import api from "./api";

// =====================================================
// GET PROFILE
// GET /api/users/profile
// =====================================================

const getProfile = async () => {
  const response = await api.get("/api/users/profile");

  return response.data;
};

// =====================================================
// UPDATE PROFILE
// PUT /api/users/profile
// =====================================================

const updateProfile = async (userData) => {
  const response = await api.put("/api/users/profile", userData);

  return response.data;
};

// =====================================================
// EXPORT
// =====================================================

const userService = {
  getProfile,
  updateProfile,
};

export default userService;
