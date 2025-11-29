import axios from 'axios';

const API_BASE_URL = '/api/products';

// Fetch all products
export const getProducts = async (page = 0, size = 10) => {
  const token = localStorage.getItem("token");
  const response = await axios.get(`${API_BASE_URL}?page=${page}&size=${size}`,{
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return response.data;
};

// Fetch a single product by ID
export const getProductById = async (productId) => {
  const token = localStorage.getItem("token");
    const response = await axios.get(`${API_BASE_URL}/${productId}`,{
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
    return response.data;
};

// Create a new product
export const createProduct = async (product) => {
  const token = localStorage.getItem("token");
  const res = await axios.post(API_BASE_URL, product,{
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return res.data;
};

// Update an existing product
export const updateProduct = async (id, product) => {
  const token = localStorage.getItem("token");
  const res = await axios.put(`${API_BASE_URL}/${id}`, product,{
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
  return res.data;
};

// Delete a product by ID
export const deleteProduct = async (id) => {
  const token = localStorage.getItem("token");
  await axios.delete(`${API_BASE_URL}/${id}`,{
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });
};

// Search product
export const searchProducts = async (searchData, page, size) => {
  const token = localStorage.getItem("token");
  const res = await axios.post(
    `${API_BASE_URL}/search?page=${page}&size=${size}`,
    searchData,
    {
      headers: {
        "Authorization": `Bearer ${token}`
      }
    }
  );
  return res.data;
};
