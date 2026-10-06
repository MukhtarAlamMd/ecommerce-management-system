import api from "./api";

// =====================================================
// REGISTER
// POST /api/auth/register
// =====================================================

const register = async (userData) => {
  const response = await api.post("/api/auth/register", userData);

  return response.data;
};

// =====================================================
// LOGIN
// POST /api/auth/login
// =====================================================

const login = async (credentials) => {
  const response = await api.post("/api/auth/login", credentials);

  return response.data;
};

// =====================================================
// GET CURRENT USER
// =====================================================

const getCurrentUser = () => {
  const user = localStorage.getItem("user");

  if (!user) {
    return null;
  }

  try {
    return JSON.parse(user);
  } catch (error) {
    console.error("Invalid user data in localStorage");

    localStorage.removeItem("user");

    return null;
  }
};

// =====================================================
// GET PROFILE
// IMPORTANT:
// Change this URL if your backend uses another endpoint.
// =====================================================

const getProfile = async () => {
  const response = await api.get("/api/auth/profile");

  return response.data;
};

// =====================================================
// LOGOUT
// =====================================================

const logout = () => {
  localStorage.removeItem("token");
  localStorage.removeItem("refreshToken");
  localStorage.removeItem("tokenType");
  localStorage.removeItem("user");
};

// =====================================================
// EXPORT
// =====================================================

const authService = {
  register,
  login,
  getCurrentUser,
  getProfile,
  logout,
};

export default authService;
