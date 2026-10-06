import axios from "axios";
import keycloak from "../keycloak";

// =====================================================
// API GATEWAY
// =====================================================

const API_BASE_URL = "http://localhost:8080/api/messages";

// =====================================================
// AI SERVICE
// =====================================================

const aiService = {
  /**
   * Classify a customer message using AI-Service
   *
   * @param {string} message - Customer's message
   * @returns {Promise<object>} AI classification result
   */
  classifyMessage: async (message) => {
    try {
      // -----------------------------------------------
      // Validate message
      // -----------------------------------------------

      if (!message || !message.trim()) {
        throw new Error("Message cannot be empty");
      }

      // -----------------------------------------------
      // Make sure user is authenticated
      // -----------------------------------------------

      if (!keycloak.authenticated || !keycloak.token) {
        throw new Error("You are not authenticated. Please login again.");
      }

      // -----------------------------------------------
      // Refresh token if it is close to expiration
      // -----------------------------------------------

      try {
        await keycloak.updateToken(30);
      } catch (refreshError) {
        console.error("Failed to refresh Keycloak token:", refreshError);

        throw new Error("Your login session has expired. Please login again.");
      }

      // -----------------------------------------------
      // Get latest access token
      // -----------------------------------------------

      const token = keycloak.token;

      console.log("AI request authenticated:", keycloak.authenticated);
      console.log("AI access token available:", !!token);

      // -----------------------------------------------
      // Call API Gateway
      // -----------------------------------------------

      const response = await axios.post(
        `${API_BASE_URL}/classify`,
        {
          message: message.trim(),
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        },
      );

      // -----------------------------------------------
      // Return AI result
      // -----------------------------------------------

      return response.data;
    } catch (error) {
      console.error("AI message classification failed:", error);

      // -----------------------------------------------
      // Backend error
      // -----------------------------------------------

      if (error.response) {
        console.error("AI API status:", error.response.status);
        console.error("AI API response:", error.response.data);

        throw new Error(
          error.response.data?.message ||
            `AI-Service returned an error (${error.response.status})`,
        );
      }

      // -----------------------------------------------
      // Network error
      // -----------------------------------------------

      if (error.request) {
        throw new Error(
          "Unable to connect to AI-Service. Please check API Gateway.",
        );
      }

      // -----------------------------------------------
      // Other error
      // -----------------------------------------------

      throw new Error(error.message || "Something went wrong");
    }
  },
};

export default aiService;
