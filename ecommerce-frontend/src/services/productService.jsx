import api from "./api";

const productService = {
  // =========================================================
  // GET ALL PRODUCTS
  // =========================================================

  getAllProducts: async () => {
    const response = await api.get("/api/products");

    return response.data;
  },

  // =========================================================
  // GET PRODUCT BY ID
  // =========================================================

  getProductById: async (id) => {
    const response = await api.get(`/api/products/${id}`);

    return response.data;
  },

  // =========================================================
  // CREATE PRODUCT
  // =========================================================

  createProduct: async (productData) => {
    const response = await api.post("/api/products", productData);

    return response.data;
  },

  // =========================================================
  // UPDATE PRODUCT
  // =========================================================

  updateProduct: async (id, productData) => {
    const response = await api.put(`/api/products/${id}`, productData);

    return response.data;
  },

  // =========================================================
  // DELETE PRODUCT
  // =========================================================

  deleteProduct: async (id) => {
    const response = await api.delete(`/api/products/${id}`);

    return response.data;
  },

  // =========================================================
  // UPLOAD PRODUCT IMAGE
  // =========================================================

  uploadProductImage: async (productId, file) => {
    if (!file) {
      throw new Error("Image file is required");
    }

    const formData = new FormData();

    // IMPORTANT:
    // Backend @RequestParam("file") expects "file"
    formData.append("file", file);

    const response = await api.post(
      `/api/products/${productId}/image`,
      formData,
    );

    return response.data;
  },
};

export default productService;
