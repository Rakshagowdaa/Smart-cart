import api from './axiosConfig';

export const placeOrder = (userId, address) => {
  let url = `/orders/${userId}?address=${encodeURIComponent(address)}`;
  return api.post(url);
};

export const getOrdersByUser = (userId) => api.get(`/orders/user/${userId}`);

export const getOrderById = (orderId) => api.get(`/orders/${orderId}`);

export const getAllOrders = () => api.get('/orders/all');

export const updateOrderStatus = (orderId, status) => {
  return api.put(`/orders/${orderId}/status?status=${status}`);
};

export const getSalesSummary = () => api.get('/orders/summary');
export const getOrdersByVendor = (vendorId) => api.get(`/orders/vendor/${vendorId}`);
export const getVendorPayouts = (vendorId) => api.get(`/orders/vendor/${vendorId}/payouts`);
