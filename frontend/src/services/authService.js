import api from './axiosConfig';

export const login = (email, password) => {
  return api.post('/auth/login', { email, password });
};

export const register = (name, email, password, role) => {
  return api.post('/auth/register', { name, email, password, role });
};

export const forgotPassword = (email) => {
  return api.post(`/auth/forgot-password?email=${email}`);
};

export const resetPassword = (email, otp, newPassword) => {
  return api.post(`/auth/reset-password?email=${email}&otp=${otp}&newPassword=${newPassword}`);
};

export const createVendor = (name, email, password) => {
  return api.post('/auth/register', { name, email, password, role: 'VENDOR', isNewUser: false });
};
