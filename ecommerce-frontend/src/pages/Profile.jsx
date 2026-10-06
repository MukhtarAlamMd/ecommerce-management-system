import { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import "./Profile.css";

const Profile = () => {
  // =========================================================
  // AUTHENTICATION / KEYCLOAK USER
  // =========================================================

  const { user, changePassword } = useAuth();

  // =========================================================
  // PROFILE STATE
  // =========================================================

  const [profile, setProfile] = useState(user);
  const [loading, setLoading] = useState(true);

  // =========================================================
  // LOAD PROFILE FROM AUTH CONTEXT
  // =========================================================
  // Keycloak is now responsible for authentication and basic
  // user information, so we don't call the old getProfile()
  // endpoint here.
  // =========================================================

  useEffect(() => {
    setProfile(user);
    setLoading(false);
  }, [user]);

  // =========================================================
  // LOADING
  // =========================================================

  if (loading) {
    return (
      <div className="profile-loading">
        <div className="profile-spinner"></div>

        <p>Loading your profile...</p>
      </div>
    );
  }

  // =========================================================
  // NOT LOGGED IN
  // =========================================================

  if (!profile) {
    return (
      <div className="profile-page">
        <div className="profile-empty">
          <div className="empty-icon">🔐</div>

          <h2>Please Login</h2>

          <p>Please login to view your profile information.</p>
        </div>
      </div>
    );
  }

  // =========================================================
  // PROFILE INITIALS
  // =========================================================

  const firstName = profile.firstName || "";
  const lastName = profile.lastName || "";

  const initials = `${firstName.charAt(0)}${lastName.charAt(0)}`.toUpperCase();

  // =========================================================
  // APPLICATION ROLE
  // =========================================================
  // AuthContext already filters Keycloak roles and returns
  // only:
  //
  // ADMIN
  // SELLER
  // CUSTOMER
  //
  // Therefore default-roles-ecommerce will never be used here.
  // =========================================================

  const role = profile.role || "CUSTOMER";

  // =========================================================
  // ACCOUNT STATUS
  // =========================================================
  // Keycloak user data may not contain "enabled".
  // Therefore, consider the account enabled unless explicitly
  // set to false.
  // =========================================================

  const isEnabled = profile.enabled !== false;

  return (
    <div className="profile-page">
      {/* =====================================================
          BREADCRUMB
      ===================================================== */}

      <div className="profile-breadcrumb">
        <span>⌂</span>

        <span>Home</span>

        <span className="breadcrumb-arrow">›</span>

        <strong>My Profile</strong>

        <div className="profile-top-message">
          "Manage your account, stay in control"
        </div>

        <div className="top-profile-icon">👤</div>
      </div>

      {/* =====================================================
          PROFILE HERO
      ===================================================== */}

      <div className="profile-hero">
        <div className="hero-background-circle circle-one"></div>

        <div className="hero-background-circle circle-two"></div>

        <div className="hero-content">
          {/* =================================================
              AVATAR
          ================================================= */}

          <div className="avatar-wrapper">
            <div className="profile-avatar">{initials || "U"}</div>

            <span
              className={`online-indicator ${isEnabled ? "online" : "offline"}`}
            ></span>
          </div>

          {/* =================================================
              USER INFORMATION
          ================================================= */}

          <div className="hero-user-info">
            <h1>
              {firstName} {lastName}
            </h1>

            <p className="hero-email">
              {profile.email || "No email available"}
            </p>

            {/* =================================================
                APPLICATION ROLE
            ================================================= */}

            <div className="hero-role">
              <span>♙</span>

              {role}
            </div>

            <p className="hero-welcome">
              Welcome back! Here is your account information.
            </p>
          </div>

          {/* =================================================
              EDIT BUTTON
          ================================================= */}

          <button type="button" className="edit-profile-btn">
            <span>✎</span>
            Edit Profile
          </button>

          <button
            type="button"
            className="change-password-btn"
            onClick={changePassword}
          >
            🔒 Change Password
          </button>
        </div>

        {/* =====================================================
            HERO QUOTE
        ===================================================== */}

        <div className="hero-quote">
          "A better shopping experience
          <br />
          starts with a great profile"
        </div>
      </div>

      {/* =====================================================
          MAIN CONTENT
      ===================================================== */}

      <div className="profile-content">
        {/* ===================================================
            PERSONAL INFORMATION
        =================================================== */}

        <section className="profile-section personal-section">
          <div className="section-header">
            <div className="section-icon blue-icon">👤</div>

            <div>
              <h2>Personal Information</h2>

              <p>Your personal details and contact information</p>
            </div>
          </div>

          <div className="info-grid">
            {/* =================================================
                FIRST NAME
            ================================================= */}

            <div className="info-card">
              <div className="info-icon first-icon">👤</div>

              <div className="info-details">
                <span className="info-label">First Name</span>

                <strong>{profile.firstName || "N/A"}</strong>
              </div>
            </div>

            {/* =================================================
                LAST NAME
            ================================================= */}

            <div className="info-card">
              <div className="info-icon last-icon">👤</div>

              <div className="info-details">
                <span className="info-label">Last Name</span>

                <strong>{profile.lastName || "N/A"}</strong>
              </div>
            </div>

            {/* =================================================
                EMAIL
            ================================================= */}

            <div className="info-card">
              <div className="info-icon email-icon">✉</div>

              <div className="info-details">
                <span className="info-label">Email</span>

                <strong>{profile.email || "N/A"}</strong>
              </div>
            </div>

            {/* =================================================
                PHONE
            ================================================= */}

            <div className="info-card">
              <div className="info-icon phone-icon">☎</div>

              <div className="info-details">
                <span className="info-label">Phone</span>

                <strong>{profile.phone || "N/A"}</strong>
              </div>
            </div>
          </div>
        </section>

        {/* ===================================================
            ACCOUNT INFORMATION
        =================================================== */}

        <section className="profile-section account-section">
          <div className="section-header">
            <div className="section-icon purple-icon">⚙</div>

            <div>
              <h2>Account Information</h2>

              <p>Your account role and status</p>
            </div>
          </div>

          <div className="account-info">
            {/* =================================================
                ROLE
            ================================================= */}

            <div className="account-card">
              <div className="account-icon role-icon">🛡</div>

              <div className="account-details">
                <span className="info-label">Role</span>

                <span className="role-badge">{role}</span>
              </div>
            </div>

            {/* =================================================
                STATUS
            ================================================= */}

            <div className="account-card">
              <div
                className={`account-icon ${
                  isEnabled ? "success-icon" : "danger-icon"
                }`}
              >
                {isEnabled ? "✓" : "!"}
              </div>

              <div className="account-details">
                <span className="info-label">Account Status</span>

                <span
                  className={`status-badge ${
                    isEnabled ? "enabled" : "disabled"
                  }`}
                >
                  <span className="status-dot"></span>

                  {isEnabled ? "Enabled" : "Disabled"}
                </span>
              </div>
            </div>
          </div>
        </section>

        {/* ===================================================
            QUICK INFO
        =================================================== */}

        <section className="quick-section">
          <div className="section-header">
            <div className="section-icon info-icon-blue">ℹ</div>

            <div>
              <h2>Quick Info</h2>

              <p>Some important things to know</p>
            </div>
          </div>

          <div className="quick-grid">
            {/* =================================================
                SHOPPING
            ================================================= */}

            <div className="quick-card shopping-card">
              <div className="quick-icon">🛒</div>

              <div>
                <h3>Shop with Confidence</h3>

                <p>
                  Your account is secured and ready for a great shopping
                  experience.
                </p>
              </div>
            </div>

            {/* =================================================
                SECURITY
            ================================================= */}

            <div className="quick-card security-card">
              <div className="quick-icon">🔒</div>

              <div>
                <h3>Keep Your Information Safe</h3>

                <p>Your personal account information is protected.</p>
              </div>
            </div>

            {/* =================================================
                SUPPORT
            ================================================= */}

            <div className="quick-card support-card">
              <div className="quick-icon">♡</div>

              <div>
                <h3>Need Help?</h3>

                <p>Contact our support team anytime you need assistance.</p>
              </div>
            </div>
          </div>
        </section>
      </div>
    </div>
  );
};

export default Profile;
