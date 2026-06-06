import api from './axiosConfig';

export const createRazorpayOrder = (userId, orderId, amount) => {
  return api.post(`/payments/create-order?userId=${userId}&orderId=${orderId}&amount=${amount}`);
};

export const verifyRazorpaySignature = (paymentData) => {
  return api.post('/payments/verify-signature', paymentData);
};
