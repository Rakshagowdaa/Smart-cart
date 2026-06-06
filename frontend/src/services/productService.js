import api from './axiosConfig';

export const getAllProducts = () => api.get('/products');

export const getProductById = (id) => api.get(`/products/${id}`);

export const searchProducts = (keyword) => api.get(`/products/search?keyword=${keyword}`);

export const filterByCategory = (category) => api.get(`/products/category/${category}`);
export const getProductsByVendor = (vendorId) => api.get(`/products/vendor/${vendorId}`);

export const addProduct = (product) => api.post('/products', product);

export const updateProduct = (id, product) => api.put(`/products/${id}`, product);

export const deleteProduct = (id) => api.delete(`/products/${id}`);

export const getReviews = (productId) => api.get(`/products/${productId}/reviews`);

export const addReview = (productId, review) => api.post(`/products/${productId}/reviews`, review);
