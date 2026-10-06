import { BrowserRouter, Routes, Route } from "react-router-dom";

// =========================================================
// COMMON / CUSTOMER
// =========================================================

import HomePage from "./pages/HomePage";

import Header from "./components/Header";

import Checkout from "./pages/Checkout";
import Cart from "./pages/Cart";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Profile from "./pages/Profile";
import ProductList from "./pages/ProductList";
import ProductDetails from "./pages/ProductDetails";
import MyOrders from "./pages/MyOrders";
import OrderDetails from "./pages/OrderDetails";
import MyPayments from "./pages/MyPayments";
import Notifications from "./pages/Notifications";

import AISupport from "./components/AISupport";

// =========================================================
// SELLER
// =========================================================

import SellerRoute from "./components/SellerRoute";

import SellerInventory from "./pages/seller/SellerInventory";
import SellerOrders from "./pages/seller/SellerOrders";

// =========================================================
// ADMIN
// =========================================================

import AdminRoute from "./components/AdminRoute";

import AdminDashboard from "./pages/AdminDashboard";
import AdminOrders from "./pages/AdminOrders";

import AdminProducts from "./pages/AdminProducts";
import AdminProductForm from "./pages/AdminProductForm";

import AdminCategories from "./pages/AdminCategories";
import AdminCategoryForm from "./pages/AdminCategoryForm";

import AdminInventory from "./pages/AdminInventory";
import AdminInventoryForm from "./pages/AdminInventoryForm";

import AdminPayments from "./pages/AdminPayments";

import AdminCreateUser from "./pages/AdminCreateUser";

// =========================================================
// HOME
// =========================================================

const Home = () => {
  return (
    <div className="container mt-5">
      <div className="text-center">
        <h1 className="display-4 fw-bold">Welcome to E-Commerce</h1>

        <p className="lead mt-3">
          Shop products from our modern microservices ecommerce platform.
        </p>
      </div>
    </div>
  );
};

// =========================================================
// APP
// =========================================================

function App() {
  return (
    <BrowserRouter>
      {/* =====================================================
          HEADER
      ===================================================== */}

      <Header />

      <Routes>
        {/* =====================================================
            PUBLIC ROUTES
        ===================================================== */}

        <Route path="/" element={<HomePage />} />

        <Route path="/login" element={<Login />} />

        <Route path="/register" element={<Register />} />

        <Route path="/products" element={<ProductList />} />

        <Route path="/products/:id" element={<ProductDetails />} />

        {/* =====================================================
            COMMON / AUTHENTICATED ROUTES
        ===================================================== */}

        <Route path="/profile" element={<Profile />} />

        <Route path="/cart" element={<Cart />} />

        <Route path="/checkout" element={<Checkout />} />

        <Route path="/orders" element={<MyOrders />} />

        <Route path="/orders/:id" element={<OrderDetails />} />

        <Route path="/payments" element={<MyPayments />} />

        <Route path="/notifications" element={<Notifications />} />

        {/* =====================================================
            AI CUSTOMER SUPPORT
        ===================================================== */}

        <Route path="/ai-support" element={<AISupport />} />

        {/* =====================================================
            SELLER ROUTES
            Only SELLER should access these pages.
        ===================================================== */}

        <Route element={<SellerRoute />}>
          <Route path="/seller/inventory" element={<SellerInventory />} />

          <Route path="/seller/orders" element={<SellerOrders />} />
        </Route>

        {/* =====================================================
            ADMIN ROUTES
            Only ADMIN should access these pages.
        ===================================================== */}

        <Route element={<AdminRoute />}>
          {/* ADMIN DASHBOARD */}

          <Route path="/admin" element={<AdminDashboard />} />

          {/* ADMIN ORDERS */}

          <Route path="/admin/orders" element={<AdminOrders />} />

          {/* ADMIN PRODUCTS */}

          <Route path="/admin/products" element={<AdminProducts />} />

          <Route path="/admin/products/create" element={<AdminProductForm />} />

          <Route
            path="/admin/products/edit/:id"
            element={<AdminProductForm />}
          />

          {/* ADMIN CATEGORIES */}

          <Route path="/admin/categories" element={<AdminCategories />} />

          <Route
            path="/admin/categories/create"
            element={<AdminCategoryForm />}
          />

          <Route
            path="/admin/categories/edit/:id"
            element={<AdminCategoryForm />}
          />

          {/* ADMIN INVENTORY */}

          <Route path="/admin/inventory" element={<AdminInventory />} />

          <Route
            path="/admin/inventory/create"
            element={<AdminInventoryForm />}
          />

          <Route
            path="/admin/inventory/edit/:id"
            element={<AdminInventoryForm />}
          />

          {/* ADMIN PAYMENTS */}

          <Route path="/admin/payments" element={<AdminPayments />} />

          {/* ADMIN USER CREATION */}

          <Route path="/admin/users/create" element={<AdminCreateUser />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
