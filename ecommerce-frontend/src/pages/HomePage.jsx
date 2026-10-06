import { Link } from "react-router-dom";
import "./HomePage.css";

const HomePage = () => {
  return (
    <div className="home-page">
      {/* =====================================================
          HERO SECTION
      ===================================================== */}

      <main className="home-main">
        {/* LEFT FASHION IMAGE */}

        <div className="hero-side hero-left">
          <div className="fashion-text left-text">
            Fashion
            <br />
            For A Better
            <br />
            You ♥
          </div>

          <img
            src="/image/kids.jpg"
            alt="Fashion Shopping"
            className="hero-model"
          />
        </div>

        {/* =====================================================
            CENTER HERO CONTENT
        ===================================================== */}

        <section className="hero-center">
          <div className="collection-badge">✨ NEW COLLECTION 2026</div>

          <h1>
            Discover Your
            <span>Perfect Style</span>
          </h1>

          <p className="hero-description">
            Discover fashion and products you'll love. Shop securely, enjoy a
            simple and smooth shopping experience, and find something perfect
            for everyone.
          </p>

          <div className="hero-buttons">
            <Link to="/products" className="shop-now-button">
              Shop Now →
            </Link>
          </div>
        </section>

        {/* =====================================================
            RIGHT PRODUCTS IMAGE
        ===================================================== */}

        <div className="hero-side hero-right">
          <div className="fashion-text right-text">
            Shop
            <br />
            The Latest
            <br />
            Trends
          </div>

          <img
            src="/images/hero-products.png"
            alt="Latest Products"
            className="hero-products"
          />
        </div>
      </main>

      {/* =====================================================
          CATEGORY CARDS
      ===================================================== */}

      <section className="categories-section">
        {/* MEN */}

        <Link to="/products?category=Men" className="home-category-card">
          <img
            src="/image/men.jpg"
            alt="Men Fashion"
            className="category-photo"
          />

          <div className="category-info">
            <div className="category-icon blue-icon">👔</div>

            <h2>Men</h2>

            <p>Modern Fashion</p>

            <span>Shop Collection →</span>
          </div>
        </Link>

        {/* WOMEN */}

        <Link
          to="/products?category=Women"
          className="home-category-card women-card"
        >
          <img
            src="/image/women.jpg"
            alt="Women Fashion"
            className="category-photo"
          />

          <div className="category-info">
            <div className="category-icon pink-icon">👗</div>

            <h2>Women</h2>

            <p>Trending Styles</p>

            <span>Shop Collection →</span>
          </div>
        </Link>

        {/* KIDS */}

        <Link
          to="/products?category=Kids"
          className="home-category-card kids-card"
        >
          <img
            src="/image/babykids.jpg"
            alt="Kids Fashion"
            className="category-photo"
          />

          <div className="category-info">
            <div className="category-icon yellow-icon">🧸</div>

            <h2>Kids</h2>

            <p>Fun & Comfortable</p>

            <span>Shop Collection →</span>
          </div>
        </Link>
      </section>

      {/* =====================================================
          BOTTOM FEATURES BAR
      ===================================================== */}

      <footer className="features-bar">
        <div className="feature-item">
          <div className="feature-icon delivery">🚚</div>

          <div>
            <h4>Fast Delivery</h4>

            <p>Quick and reliable shipping</p>
          </div>
        </div>

        <div className="feature-divider" />

        <div className="feature-item">
          <div className="feature-icon payment">🔒</div>

          <div>
            <h4>Secure Payment</h4>

            <p>Safe and protected payments</p>
          </div>
        </div>

        <div className="feature-divider" />

        <div className="feature-item">
          <div className="feature-icon tracking">📦</div>

          <div>
            <h4>Order Tracking</h4>

            <p>Track your order anytime</p>
          </div>
        </div>

        <div className="feature-divider" />

        <div className="feature-item">
          <div className="feature-icon updates">🔔</div>

          <div>
            <h4>Live Updates</h4>

            <p>Get instant notifications</p>
          </div>
        </div>
      </footer>
    </div>
  );
};

export default HomePage;
