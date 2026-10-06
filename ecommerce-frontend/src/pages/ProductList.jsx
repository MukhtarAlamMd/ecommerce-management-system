import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";

import productService from "../services/productService";
import inventoryService from "../services/inventoryService";

import "./ProductList.css";

const ProductList = () => {
  const navigate = useNavigate();

  // =========================================================
  // STATE
  // =========================================================

  const [products, setProducts] = useState([]);
  const [inventory, setInventory] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [search, setSearch] = useState("");
  const [selectedCategory, setSelectedCategory] = useState("All");
  const [sortBy, setSortBy] = useState("latest");

  const [favorites, setFavorites] = useState([]);

  // =========================================================
  // LOAD PRODUCTS + INVENTORY
  // =========================================================

  useEffect(() => {
    const loadProducts = async () => {
      try {
        setLoading(true);
        setError("");

        const [productData, inventoryData] = await Promise.all([
          productService.getAllProducts(),
          inventoryService.getAllInventory(),
        ]);

        console.log("PRODUCTS:", productData);
        console.log("INVENTORY:", inventoryData);

        setProducts(Array.isArray(productData) ? productData : []);
        setInventory(Array.isArray(inventoryData) ? inventoryData : []);
      } catch (err) {
        console.error("Failed to load products:", err);

        setError(err.response?.data?.message || "Failed to load products");
      } finally {
        setLoading(false);
      }
    };

    loadProducts();
  }, []);

  // =========================================================
  // COMBINE PRODUCT + INVENTORY
  // =========================================================

  const productsWithInventory = useMemo(() => {
    return products.map((product) => {
      const stockInfo = inventory.find(
        (item) => Number(item.productId) === Number(product.id),
      );

      return {
        ...product,

        availableQuantity: Number(stockInfo?.availableQuantity ?? 0),

        reorderLevel: Number(stockInfo?.reorderLevel ?? 0),
      };
    });
  }, [products, inventory]);

  // =========================================================
  // CATEGORIES
  // =========================================================

  const categories = useMemo(() => {
    const categorySet = new Set();

    productsWithInventory.forEach((product) => {
      if (product.categoryName) {
        categorySet.add(product.categoryName);
      }
    });

    return ["All", ...Array.from(categorySet).sort()];
  }, [productsWithInventory]);

  // =========================================================
  // FILTER + SEARCH + SORT
  // =========================================================

  const filteredProducts = useMemo(() => {
    let result = [...productsWithInventory];

    // -------------------------------------------------------
    // SEARCH
    // -------------------------------------------------------

    if (search.trim()) {
      const searchText = search.toLowerCase().trim();

      result = result.filter((product) => {
        return (
          product.name?.toLowerCase().includes(searchText) ||
          product.description?.toLowerCase().includes(searchText) ||
          product.categoryName?.toLowerCase().includes(searchText)
        );
      });
    }

    // -------------------------------------------------------
    // CATEGORY
    // -------------------------------------------------------

    if (selectedCategory !== "All") {
      result = result.filter(
        (product) => product.categoryName === selectedCategory,
      );
    }

    // -------------------------------------------------------
    // SORT
    // -------------------------------------------------------

    switch (sortBy) {
      case "price-low":
        result.sort((a, b) => Number(a.price || 0) - Number(b.price || 0));
        break;

      case "price-high":
        result.sort((a, b) => Number(b.price || 0) - Number(a.price || 0));
        break;

      case "name":
        result.sort((a, b) =>
          String(a.name || "").localeCompare(String(b.name || "")),
        );
        break;

      default:
        // Latest products first
        result.sort((a, b) => Number(b.id || 0) - Number(a.id || 0));
    }

    return result;
  }, [productsWithInventory, search, selectedCategory, sortBy]);

  // =========================================================
  // FAVORITES
  // =========================================================

  const toggleFavorite = (id) => {
    setFavorites((current) => {
      if (current.includes(id)) {
        return current.filter((item) => item !== id);
      }

      return [...current, id];
    });
  };

  // =========================================================
  // PRODUCT IMAGE
  // =========================================================

  // All frontend API traffic goes through the API Gateway.
  // Uploaded product images must also be requested through it.
  const API_GATEWAY_URL = "http://localhost:8080";

  const getProductImage = (product) => {
    /*
     * Priority:
     *
     * 1. Backend imageUrl
     * 2. Existing image field
     * 3. Local product image
     * 4. Default image
     */

    // -------------------------------------------------------
    // BACKEND IMAGE URL
    // -------------------------------------------------------

    if (product?.imageUrl) {
      const imageUrl = String(product.imageUrl).trim();

      // Backend already returned complete URL
      if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
        return imageUrl;
      }

      /*
       * Uploaded image example:
       *
       * /uploads/products/abc.jpg
       *
       * becomes:
       *
       * http://localhost:8080/uploads/products/abc.jpg
       */

      return `${API_GATEWAY_URL}${
        imageUrl.startsWith("/") ? "" : "/"
      }${imageUrl}`;
    }

    // -------------------------------------------------------
    // EXISTING IMAGE FIELD
    // -------------------------------------------------------

    if (product?.image) {
      const imageUrl = String(product.image).trim();

      if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
        return imageUrl;
      }

      return `${API_GATEWAY_URL}${
        imageUrl.startsWith("/") ? "" : "/"
      }${imageUrl}`;
    }

    // -------------------------------------------------------
    // LOCAL FALLBACK
    // -------------------------------------------------------

    const imageName = String(product?.name || "product")
      .toLowerCase()
      .trim()
      .replace(/[^a-z0-9]+/g, "-")
      .replace(/^-+|-+$/g, "");

    return `/products/${imageName}.jpg`;
  };

  // =========================================================
  // IMAGE FALLBACK
  // =========================================================

  const handleImageError = (event) => {
    const image = event.currentTarget;

    // Prevent infinite fallback loop
    if (image.dataset.fallback === "true") {
      return;
    }

    console.error("Product image could not be loaded:", image.src);

    image.dataset.fallback = "true";

    image.src = "/products/default-product.jpg";
  };

  // =========================================================
  // STOCK
  // =========================================================

  const getStockState = (quantity) => {
    if (quantity <= 0) {
      return {
        label: "Out of Stock",
        className: "stock-out",
      };
    }

    if (quantity <= 10) {
      return {
        label: "Low Stock",
        className: "stock-low",
      };
    }

    return {
      label: "Available",
      className: "stock-available",
    };
  };

  // =========================================================
  // LOADING
  // =========================================================

  if (loading) {
    return (
      <div className="products-loading">
        <div className="products-spinner"></div>

        <h3>Loading products...</h3>

        <p>Please wait while we prepare our collection.</p>
      </div>
    );
  }

  // =========================================================
  // ERROR
  // =========================================================

  if (error) {
    return (
      <div className="products-page">
        <div className="products-error">
          <div className="error-icon">⚠</div>

          <h2>Unable to load products</h2>

          <p>{error}</p>

          <button onClick={() => window.location.reload()}>Try Again</button>
        </div>
      </div>
    );
  }

  // =========================================================
  // UI
  // =========================================================

  return (
    <div className="products-page">
      {/* =====================================================
          HERO
      ===================================================== */}

      <section className="products-header">
        <div className="header-text">
          <span className="collection-label">DISCOVER OUR COLLECTION</span>

          <h1>Products</h1>

          <p>Find your perfect style from our latest collection</p>
        </div>

        {/* SEARCH */}

        <div className="product-search">
          <span className="search-icon">⌕</span>

          <input
            type="text"
            value={search}
            onChange={(event) => setSearch(event.target.value)}
            placeholder="Search products..."
            aria-label="Search products"
          />

          {search && (
            <button
              className="clear-search"
              onClick={() => setSearch("")}
              aria-label="Clear search"
            >
              ×
            </button>
          )}
        </div>
      </section>

      {/* =====================================================
          FILTER BAR
      ===================================================== */}

      <section className="product-toolbar">
        <div className="category-pills">
          {categories.map((category) => (
            <button
              key={category}
              className={
                selectedCategory === category
                  ? "category-pill active"
                  : "category-pill"
              }
              onClick={() => setSelectedCategory(category)}
            >
              {category}
            </button>
          ))}
        </div>

        <div className="toolbar-right">
          <span className="product-count">
            {filteredProducts.length}{" "}
            {filteredProducts.length === 1 ? "Product" : "Products"}
          </span>

          <select
            value={sortBy}
            onChange={(event) => setSortBy(event.target.value)}
            className="sort-select"
            aria-label="Sort products"
          >
            <option value="latest">Sort: Latest</option>

            <option value="price-low">Price: Low to High</option>

            <option value="price-high">Price: High to Low</option>

            <option value="name">Name: A-Z</option>
          </select>
        </div>
      </section>

      {/* =====================================================
          PRODUCTS
      ===================================================== */}

      {filteredProducts.length === 0 ? (
        <div className="empty-products">
          <div className="empty-icon">🛍</div>

          <h2>No products found</h2>

          <p>Try another search or category.</p>

          <button
            onClick={() => {
              setSearch("");
              setSelectedCategory("All");
            }}
          >
            Clear Filters
          </button>
        </div>
      ) : (
        <section className="product-grid">
          {filteredProducts.map((product) => {
            const stock = getStockState(product.availableQuantity);

            const isFavorite = favorites.includes(product.id);

            const productImage = getProductImage(product);

            return (
              <article className="product-card" key={product.id}>
                {/* =================================
                    IMAGE
                ================================= */}

                <div className="product-image-wrapper">
                  <img
                    src={productImage}
                    alt={`${product.name} product`}
                    className="product-image"
                    loading="lazy"
                    onError={handleImageError}
                  />

                  {/* STOCK BADGE */}

                  {product.availableQuantity <= 0 && (
                    <span className="image-stock-badge">Out of Stock</span>
                  )}

                  {product.availableQuantity > 0 &&
                    product.availableQuantity <= 10 && (
                      <span className="image-low-stock-badge">
                        Only {product.availableQuantity} left
                      </span>
                    )}

                  {/* FAVORITE */}

                  <button
                    type="button"
                    className={
                      isFavorite
                        ? "favorite-button favorite-active"
                        : "favorite-button"
                    }
                    onClick={() => toggleFavorite(product.id)}
                    aria-label={
                      isFavorite
                        ? `Remove ${product.name} from favorites`
                        : `Add ${product.name} to favorites`
                    }
                  >
                    {isFavorite ? "♥" : "♡"}
                  </button>
                </div>

                {/* =================================
                    CONTENT
                ================================= */}

                <div className="product-content">
                  <div className="product-category">
                    {product.categoryName || "Collection"}
                  </div>

                  <h2 className="product-name">{product.name}</h2>

                  <p className="product-description">
                    {product.description ||
                      "Discover this product from our latest collection."}
                  </p>

                  {/* PRICE */}

                  <div className="product-price">
                    ₹{Number(product.price || 0).toFixed(2)}
                  </div>

                  {/* STOCK */}

                  <div className="product-stock-row">
                    <span className={`stock-badge ${stock.className}`}>
                      {stock.label}
                    </span>

                    <span className="stock-number">
                      Stock: <strong>{product.availableQuantity}</strong>
                    </span>
                  </div>

                  {/* ACTION */}

                  <button
                    type="button"
                    className={
                      product.availableQuantity <= 0
                        ? "view-product-button disabled"
                        : "view-product-button"
                    }
                    onClick={() => navigate(`/products/${product.id}`)}
                  >
                    <span>🛒</span>
                    View Details
                    <span className="button-arrow">→</span>
                  </button>
                </div>
              </article>
            );
          })}
        </section>
      )}
    </div>
  );
};

export default ProductList;
