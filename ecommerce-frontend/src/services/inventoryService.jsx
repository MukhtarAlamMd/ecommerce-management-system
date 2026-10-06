import api from "./api";

const getAllInventory = async () => {
  const response = await api.get("/api/inventory");

  return response.data;
};

const getInventoryByProductId = async (productId) => {
  const response = await api.get(`/api/inventory/product/${productId}`);

  return response.data;
};

const createInventory = async (inventoryData) => {
  const response = await api.post("/api/inventory", inventoryData);

  return response.data;
};

const updateInventory = async (id, inventoryData) => {
  const response = await api.put(`/api/inventory/${id}`, inventoryData);

  return response.data;
};

const addStock = async (productId, quantity) => {
  const response = await api.post(
    `/api/inventory/${productId}/stock-in`,
    null,
    {
      params: {
        quantity,
      },
    },
  );

  return response.data;
};

const reserveStock = async (productId, quantity) => {
  const response = await api.post(`/api/inventory/${productId}/reserve`, null, {
    params: {
      quantity,
    },
  });

  return response.data;
};

const releaseStock = async (productId, quantity) => {
  const response = await api.post(`/api/inventory/${productId}/release`, null, {
    params: {
      quantity,
    },
  });

  return response.data;
};

const inventoryService = {
  getAllInventory,
  getInventoryByProductId,
  createInventory,
  updateInventory,
  addStock,
  reserveStock,
  releaseStock,
};

export default inventoryService;
