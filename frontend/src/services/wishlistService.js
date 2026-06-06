import api from './axiosConfig';

export const addToWishlist = (userId, productId) => {
    return api.post(`/wishlist/${userId}/add?productId=${productId}`);
};

export const removeFromWishlist = (userId, productId) => {
    return api.delete(`/wishlist/${userId}/remove/${productId}`);
};

export const getWishlist = (userId) => {
    return api.get(`/wishlist/${userId}`);
};
