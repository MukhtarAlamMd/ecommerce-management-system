import { createContext, useContext, useEffect, useRef, useState } from "react";
import keycloak from "../keycloak";

const AuthContext = createContext(null);

// =====================================================
// APPLICATION ROLES
// =====================================================

const APP_ROLES = ["ADMIN", "SELLER", "CUSTOMER"];

const getApplicationRole = (roles = []) => {
  return APP_ROLES.find((role) => roles.includes(role)) || null;
};

// =====================================================
// AUTH PROVIDER
// =====================================================

export const AuthProvider = ({ children }) => {
  // =====================================================
  // USER
  // =====================================================

  const [user, setUser] = useState(null);

  // =====================================================
  // LOADING
  // =====================================================

  const [loading, setLoading] = useState(true);

  // =====================================================
  // AUTHENTICATED
  // =====================================================

  const [isAuthenticated, setIsAuthenticated] = useState(false);

  // =====================================================
  // PREVENT DOUBLE INITIALIZATION
  // =====================================================

  const initialized = useRef(false);

  // =====================================================
  // BUILD USER FROM KEYCLOAK TOKEN
  // =====================================================

  const buildUser = () => {
    if (!keycloak.tokenParsed) {
      return null;
    }

    const tokenParsed = keycloak.tokenParsed;

    // -----------------------------------------------
    // Get all realm roles
    // -----------------------------------------------

    const roles = tokenParsed.realm_access?.roles || [];

    // -----------------------------------------------
    // Get application role
    // -----------------------------------------------

    const role = getApplicationRole(roles);

    // -----------------------------------------------
    // Create application user object
    // -----------------------------------------------

    return {
      id: tokenParsed.sub || "",

      username: tokenParsed.preferred_username || "",

      email: tokenParsed.email || "",

      firstName: tokenParsed.given_name || "",

      lastName: tokenParsed.family_name || "",

      roles,

      role,
    };
  };

  // =====================================================
  // INITIALIZE KEYCLOAK
  // =====================================================

  useEffect(() => {
    if (initialized.current) {
      return;
    }

    initialized.current = true;

    const initializeKeycloak = async () => {
      try {
        console.log("Initializing Keycloak...");

        const authenticated = await keycloak.init({
          onLoad: "check-sso",
          pkceMethod: "S256",
          checkLoginIframe: false,
        });

        console.log("Keycloak initialized. Authenticated:", authenticated);

        // =================================================
        // AUTHENTICATED
        // =================================================

        if (authenticated && keycloak.tokenParsed) {
          const userData = buildUser();

          setUser(userData);
          setIsAuthenticated(true);

          console.log("=================================");
          console.log("KEYCLOAK AUTHENTICATION SUCCESS");
          console.log("=================================");

          console.log("User:", userData);

          console.log(
            "Keycloak roles:",
            keycloak.tokenParsed.realm_access?.roles || [],
          );

          console.log("Application role:", userData?.role || null);
        }

        // =================================================
        // NOT AUTHENTICATED
        // =================================================
        else {
          setUser(null);
          setIsAuthenticated(false);

          console.log("User is not authenticated.");
        }

        // =================================================
        // TOKEN EXPIRATION
        // =================================================

        keycloak.onTokenExpired = async () => {
          try {
            console.log("Keycloak token expired. Refreshing...");

            const refreshed = await keycloak.updateToken(30);

            console.log("Token refresh result:", refreshed);

            if (keycloak.tokenParsed) {
              const userData = buildUser();

              setUser(userData);
              setIsAuthenticated(true);
            }
          } catch (error) {
            console.error("Failed to refresh Keycloak token:", error);

            setUser(null);
            setIsAuthenticated(false);
          }
        };
      } catch (error) {
        console.error("Keycloak initialization failed:", error);

        setUser(null);
        setIsAuthenticated(false);
      } finally {
        setLoading(false);
      }
    };

    initializeKeycloak();
  }, []);

  // =====================================================
  // LOGIN
  // =====================================================

  const login = async () => {
    try {
      await keycloak.login({
        redirectUri: window.location.origin,
      });
    } catch (error) {
      console.error("Keycloak login failed:", error);
    }
  };

  // =====================================================
  // REGISTER
  // =====================================================

  const register = async () => {
    try {
      await keycloak.register({
        redirectUri: window.location.origin,
      });
    } catch (error) {
      console.error("Keycloak registration failed:", error);
    }
  };

  // =====================================================
  // LOGOUT
  // =====================================================

  const logout = async () => {
    try {
      setUser(null);
      setIsAuthenticated(false);

      await keycloak.logout({
        redirectUri: window.location.origin,
      });
    } catch (error) {
      console.error("Keycloak logout failed:", error);
    }
  };

  // =====================================================
  // CHANGE PASSWORD
  // =====================================================

  const changePassword = async () => {
    try {
      if (!keycloak.authenticated) {
        console.warn("User is not authenticated. Redirecting to login.");

        await keycloak.login();
        return;
      }

      await keycloak.login({
        action: "UPDATE_PASSWORD",
      });
    } catch (error) {
      console.error("Failed to open Keycloak password change:", error);
    }
  };

  // =====================================================
  // ROLE HELPERS
  // =====================================================

  const hasRole = (requiredRole) => {
    return user?.role === requiredRole;
  };

  const hasAnyRole = (requiredRoles = []) => {
    return requiredRoles.includes(user?.role);
  };

  const isAdmin = user?.role === "ADMIN";

  const isSeller = user?.role === "SELLER";

  const isCustomer = user?.role === "CUSTOMER";

  // =====================================================
  // CONTEXT VALUE
  // =====================================================

  const value = {
    user,

    setUser,

    loading,

    isAuthenticated,

    login,

    register,

    logout,

    changePassword,

    keycloak,

    role: user?.role || null,

    hasRole,

    hasAnyRole,

    isAdmin,

    isSeller,

    isCustomer,
  };

  // =====================================================
  // WAIT FOR KEYCLOAK
  // =====================================================

  if (loading) {
    return (
      <div
        style={{
          minHeight: "100vh",
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
        }}
      >
        <h3>Initializing authentication...</h3>
      </div>
    );
  }

  // =====================================================
  // PROVIDER
  // =====================================================

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

// =====================================================
// useAuth
// =====================================================

export const useAuth = () => {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error("useAuth must be used inside AuthProvider");
  }

  return context;
};
