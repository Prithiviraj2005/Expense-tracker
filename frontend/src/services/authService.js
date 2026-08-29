import api from './api';

export const authService = {
  login: async (data) => {
    const response = await api.post('/auth/login', data);
    return response.data;
  },
  register: async (data) => {
    const response = await api.post('/auth/register', data);
    return response.data;
  },
  getCurrentUser: async () => {
    const response = await api.get('/users/me');
    return response.data;
  },
  updateProfile: async (data) => {
    const response = await api.put('/users/me', data);
    return response.data;
  },
  changePassword: async (data) => {
    const response = await api.put('/users/change-password', data);
    return response.data;
  }
};
