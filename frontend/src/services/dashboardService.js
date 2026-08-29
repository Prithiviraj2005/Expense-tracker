import api from './api';

export const dashboardService = {
  getSummary: async () => {
    const response = await api.get('/dashboard/summary');
    return response.data;
  },
  getCategorySummary: async (type) => {
    const params = type ? { type } : {};
    const response = await api.get('/dashboard/category-summary', { params });
    return response.data;
  },
  getMonthlySummary: async (year) => {
    const params = year ? { year } : {};
    const response = await api.get('/dashboard/monthly-summary', { params });
    return response.data;
  }
};
