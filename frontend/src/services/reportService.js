import api from './api';

export const reportService = {
  getReport: async (params) => {
    const response = await api.get('/reports', { params });
    return response.data;
  }
};
