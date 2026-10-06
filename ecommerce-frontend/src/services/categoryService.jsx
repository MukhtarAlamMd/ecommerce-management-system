import api from "./api";

const getAllCategories = async () => {
  const response = await api.get("/api/categories");

  return response.data;
};

const getCategoryById = async (id) => {
  const response = await api.get(`/api/categories/${id}`);

  return response.data;
};

const createCategory = async (categoryData) => {
  const response = await api.post("/api/categories", categoryData);

  return response.data;
};

const updateCategory = async (id, categoryData) => {
  const response = await api.put(`/api/categories/${id}`, categoryData);

  return response.data;
};

const deleteCategory = async (id) => {
  await api.delete(`/api/categories/${id}`);
};

const categoryService = {
  getAllCategories,
  getCategoryById,
  createCategory,
  updateCategory,
  deleteCategory,
};

export default categoryService;
