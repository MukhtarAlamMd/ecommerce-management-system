import axios from "axios";
import keycloak from "../keycloak";

const api = axios.create({
  baseURL: "http://localhost:8080",
});

api.interceptors.request.use(
  async (config) => {
    if (keycloak.authenticated) {
      await keycloak.updateToken(30);

      if (!keycloak.token) {
        return Promise.reject(new Error("No Keycloak access token available."));
      }

      config.headers ??= {};
      config.headers.Authorization = `Bearer ${keycloak.token}`;
    }

    // Let the browser set the multipart boundary for FormData.
    if (typeof FormData !== "undefined" && config.data instanceof FormData) {
      delete config.headers?.["Content-Type"];
      delete config.headers?.["content-type"];
    }

    return config;
  },
  (error) => Promise.reject(error),
);

export default api;
