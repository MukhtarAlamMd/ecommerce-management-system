import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import productService from "../services/productService";
import categoryService from "../services/categoryService";

const AdminProductForm = () => {
  const navigate = useNavigate();
  const { id } = useParams();

  const isEditMode = Boolean(id);

  // =====================================================
  // STATE
  // =====================================================

  const [categories, setCategories] = useState([]);

  const [formData, setFormData] = useState({
    name: "",
    description: "",
    categoryId: "",
    price: "",
    stockQuantity: "",
  });

  const [loading, setLoading] = useState(false);
  const [loadingCategories, setLoadingCategories] = useState(true);
  const [error, setError] = useState("");

  // =====================================================
  // PREDEFINED PRODUCT NAMES
  // These names must match the category names from database
  // =====================================================

  const predefinedProducts = {
    Laptops: [
      "Dell Inspiron 15",
      "HP Pavilion 15",
      "Lenovo IdeaPad 3",
      "ASUS VivoBook 15",
      "Acer Aspire 5",
    ],

    "Mobile Phones": [
      "Samsung Galaxy A15",
      "Redmi Note 14",
      "OnePlus Nord CE 4",
      "Realme 13 Pro",
      "Vivo V30",
    ],

    Headphones: [
      "Sony WH-1000XM5",
      "JBL Tune 770NC",
      "boAt Rockerz 550",
      "OnePlus Nord Buds",
      "Apple AirPods",
    ],

    Televisions: [
      "Samsung 43 Inch Smart TV",
      "LG 50 Inch 4K Smart TV",
      "Sony Bravia 55 Inch",
      "OnePlus 43 Inch Smart TV",
      "TCL 55 Inch 4K TV",
    ],

    "Computer Accessories": [
      "Logitech Wireless Keyboard",
      "HP Wireless Mouse",
      "Logitech C920 Webcam",
      "TP-Link USB WiFi Adapter",
      "Dell USB Keyboard",
    ],

    "Men's Clothing": [
      "Men's Cotton Shirt",
      "Men's Casual T-Shirt",
      "Men's Denim Jeans",
      "Men's Formal Trousers",
      "Men's Winter Jacket",
    ],

    "Women's Clothing": [
      "Cotton Kurti",
      "Designer Saree",
      "Women's Casual Top",
      "Women's Denim Jeans",
      "Women's Anarkali Dress",
    ],

    "Kids' Clothing": [
      "Kids Cotton T-Shirt",
      "Kids Denim Jeans",
      "Girls Frock",
      "Boys Casual Shirt",
      "Kids Winter Jacket",
    ],

    Footwear: [
      "Men's Running Shoes",
      "Women's Sandals",
      "Casual Sneakers",
      "Sports Shoes",
      "Kids School Shoes",
    ],

    "Fashion Accessories": [
      "Leather Wallet",
      "Ladies Handbag",
      "Men's Belt",
      "Baseball Cap",
      "Fashion Sunglasses",
    ],
  };

  // =====================================================
  // GET SELECTED CATEGORY
  // =====================================================

  const selectedCategory = categories.find(
    (category) => String(category.id) === String(formData.categoryId),
  );

  const selectedCategoryName = selectedCategory?.name || "";

  const availableProducts = predefinedProducts[selectedCategoryName] || [];

  // =====================================================
  // LOAD CATEGORIES
  // =====================================================

  const loadCategories = async () => {
    try {
      setLoadingCategories(true);
      setError("");

      const data = await categoryService.getAllCategories();

      setCategories(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error("Failed to load categories:", err);

      setError(
        err.response?.data?.message ||
          "Failed to load categories. Please try again.",
      );
    } finally {
      setLoadingCategories(false);
    }
  };

  // =====================================================
  // LOAD PRODUCT FOR EDIT
  // =====================================================

  const loadProduct = async () => {
    if (!isEditMode) {
      return;
    }

    try {
      setLoading(true);
      setError("");

      const product = await productService.getProductById(id);

      setFormData({
        name: product.name || "",

        description: product.description || "",

        categoryId:
          product.categoryId !== null && product.categoryId !== undefined
            ? String(product.categoryId)
            : "",

        price:
          product.price !== null && product.price !== undefined
            ? String(product.price)
            : "",

        stockQuantity:
          product.stockQuantity !== null && product.stockQuantity !== undefined
            ? String(product.stockQuantity)
            : "",
      });
    } catch (err) {
      console.error("Failed to load product:", err);

      setError(err.response?.data?.message || "Failed to load product.");
    } finally {
      setLoading(false);
    }
  };

  // =====================================================
  // INITIAL LOAD
  // =====================================================

  useEffect(() => {
    loadCategories();
    loadProduct();
  }, [id]);

  // =====================================================
  // HANDLE NORMAL INPUT
  // =====================================================

  const handleChange = (e) => {
    const { name, value } = e.target;

    setFormData((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  // =====================================================
  // HANDLE CATEGORY CHANGE
  // =====================================================

  const handleCategoryChange = (e) => {
    const categoryId = e.target.value;

    setFormData((previous) => ({
      ...previous,
      categoryId,
      name: "",
    }));

    setError("");
  };

  // =====================================================
  // SUBMIT
  // =====================================================

  const handleSubmit = async (e) => {
    e.preventDefault();

    setError("");

    // ---------------------------------------------------
    // VALIDATE CATEGORY
    // ---------------------------------------------------

    if (!formData.categoryId) {
      setError("Please select a category.");
      return;
    }

    // ---------------------------------------------------
    // VALIDATE PRODUCT NAME
    // ---------------------------------------------------

    if (!formData.name) {
      setError("Please select a product name.");
      return;
    }

    // ---------------------------------------------------
    // VALIDATE PRICE
    // ---------------------------------------------------

    const price = Number(formData.price);

    if (Number.isNaN(price) || price <= 0) {
      setError("Price must be greater than zero.");
      return;
    }

    // ---------------------------------------------------
    // VALIDATE STOCK
    // ---------------------------------------------------

    const stockQuantity = Number(formData.stockQuantity);

    if (
      Number.isNaN(stockQuantity) ||
      stockQuantity < 0 ||
      !Number.isInteger(stockQuantity)
    ) {
      setError(
        "Stock quantity must be a whole number greater than or equal to zero.",
      );
      return;
    }

    try {
      setLoading(true);

      const productData = {
        name: formData.name.trim(),

        description: formData.description.trim(),

        // Backend expects Long
        categoryId: Number(formData.categoryId),

        price,

        stockQuantity,
      };

      console.log("PRODUCT DATA:", productData);

      if (isEditMode) {
        await productService.updateProduct(id, productData);
      } else {
        await productService.createProduct(productData);
      }

      navigate("/admin/products");
    } catch (err) {
      console.error("Failed to save product:", err);

      setError(
        err.response?.data?.message ||
          err.response?.data ||
          "Failed to save product.",
      );
    } finally {
      setLoading(false);
    }
  };

  // =====================================================
  // LOADING EDIT PRODUCT
  // =====================================================

  if (loading && isEditMode) {
    return (
      <div className="container mt-5 text-center">
        <div className="spinner-border" role="status" />

        <p className="mt-3">Loading product...</p>
      </div>
    );
  }

  // =====================================================
  // PAGE
  // =====================================================

  return (
    <div className="container mt-5 mb-5">
      {/* =================================================
          HEADER
      ================================================= */}

      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="fw-bold">
            {isEditMode ? "Edit Product" : "Create Product"}
          </h2>

          <p className="text-muted mb-0">
            {isEditMode
              ? "Update product information"
              : "Add a new product to your store"}
          </p>
        </div>

        <button
          type="button"
          className="btn btn-outline-secondary"
          onClick={() => navigate("/admin/products")}
        >
          Back
        </button>
      </div>

      {/* =================================================
          ERROR
      ================================================= */}

      {error && <div className="alert alert-danger">{error}</div>}

      {/* =================================================
          FORM
      ================================================= */}

      <div className="card shadow-sm">
        <div className="card-header">
          <h5 className="mb-0">Product Information</h5>
        </div>

        <div className="card-body">
          <form onSubmit={handleSubmit}>
            {/* ==========================================
                CATEGORY
            ========================================== */}

            <div className="mb-3">
              <label htmlFor="categoryId" className="form-label fw-semibold">
                Category
              </label>

              <select
                id="categoryId"
                name="categoryId"
                className="form-select"
                value={formData.categoryId}
                onChange={handleCategoryChange}
                disabled={loadingCategories}
                required
              >
                <option value="">
                  {loadingCategories
                    ? "Loading categories..."
                    : "Select Category"}
                </option>

                {categories.map((category) => (
                  <option key={category.id} value={category.id}>
                    {category.name}
                  </option>
                ))}
              </select>

              {!loadingCategories && categories.length === 0 && (
                <div className="form-text text-danger">
                  No categories found. Create a category first.
                </div>
              )}
            </div>

            {/* ==========================================
                PRODUCT NAME
            ========================================== */}

            <div className="mb-3">
              <label htmlFor="name" className="form-label fw-semibold">
                Product Name
              </label>

              <select
                id="name"
                name="name"
                className="form-select"
                value={formData.name}
                onChange={handleChange}
                disabled={
                  !formData.categoryId || availableProducts.length === 0
                }
                required
              >
                <option value="">
                  {!formData.categoryId
                    ? "Select Category First"
                    : availableProducts.length === 0
                      ? "No predefined products for this category"
                      : "Select Product"}
                </option>

                {availableProducts.map((productName) => (
                  <option key={productName} value={productName}>
                    {productName}
                  </option>
                ))}
              </select>

              <div className="form-text">
                {selectedCategoryName
                  ? availableProducts.length > 0
                    ? `Select a product from ${selectedCategoryName}.`
                    : `No predefined products are available for ${selectedCategoryName}.`
                  : "Select a category first."}
              </div>
            </div>

            {/* ==========================================
                DESCRIPTION
            ========================================== */}

            <div className="mb-3">
              <label htmlFor="description" className="form-label fw-semibold">
                Description
              </label>

              <textarea
                id="description"
                name="description"
                className="form-control"
                rows="4"
                placeholder="Enter product description..."
                value={formData.description}
                onChange={handleChange}
              />
            </div>

            {/* ==========================================
                PRICE + STOCK
            ========================================== */}

            <div className="row">
              {/* PRICE */}

              <div className="col-md-6 mb-3">
                <label htmlFor="price" className="form-label fw-semibold">
                  Price
                </label>

                <div className="input-group">
                  <span className="input-group-text">₹</span>

                  <input
                    id="price"
                    type="number"
                    name="price"
                    className="form-control"
                    min="0.01"
                    step="0.01"
                    placeholder="0.00"
                    value={formData.price}
                    onChange={handleChange}
                    required
                  />
                </div>
              </div>

              {/* STOCK */}

              <div className="col-md-6 mb-3">
                <label
                  htmlFor="stockQuantity"
                  className="form-label fw-semibold"
                >
                  Stock Quantity
                </label>

                <input
                  id="stockQuantity"
                  type="number"
                  name="stockQuantity"
                  className="form-control"
                  min="0"
                  step="1"
                  placeholder="0"
                  value={formData.stockQuantity}
                  onChange={handleChange}
                  required
                />
              </div>
            </div>

            {/* ==========================================
                BUTTONS
            ========================================== */}

            <div className="d-flex gap-2 mt-3">
              <button
                type="submit"
                className="btn btn-primary"
                disabled={
                  loading ||
                  loadingCategories ||
                  categories.length === 0 ||
                  !formData.categoryId ||
                  !formData.name
                }
              >
                {loading
                  ? "Saving..."
                  : isEditMode
                    ? "Update Product"
                    : "Create Product"}
              </button>

              <button
                type="button"
                className="btn btn-outline-secondary"
                disabled={loading}
                onClick={() => navigate("/admin/products")}
              >
                Cancel
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default AdminProductForm;
