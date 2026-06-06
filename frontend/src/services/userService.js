import api from './axiosConfig';

export const getUserProfile = (id) => {
  return api.get(`/users/${id}`);
};

export const updateProfile = (id, name, phoneNumber) => {
  return api.put(`/users/${id}`, { name, phoneNumber });
};

export const getAllUsers = () => {
  return api.get('/users');
};
