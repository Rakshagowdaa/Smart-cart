import api from './axiosConfig';

export const getCart = (userId) => api.get(`/cart/${userId}`);

export const addToCart = (userId, productId, quantity = 1) => {
  return api.post(`/cart/${userId}/add?productId=${productId}&quantity=${quantity}`);
};

export const removeFromCart = (userId, productId) => {
  return api.delete(`/cart/${userId}/remove/${productId}`);
};

export const clearCart = (userId) => api.delete(`/cart/${userId}/clear`);
