import api from './api';

const downloadFile = (response, defaultFilename) => {
  const contentDisposition = response.headers['content-disposition'];
  let filename = defaultFilename;
  if (contentDisposition) {
    const filenameMatch = contentDisposition.match(/filename="?([^"]+)"?/);
    if (filenameMatch && filenameMatch.length === 2) {
      filename = filenameMatch[1];
    }
  }

  const url = window.URL.createObjectURL(new Blob([response.data]));
  const link = document.createElement('a');
  link.href = url;
  link.setAttribute('download', filename);
  document.body.appendChild(link);
  link.click();
  link.remove();
  window.URL.revokeObjectURL(url);
};

export const exportService = {
  exportCsv: async (params) => {
    const response = await api.get('/export/csv', { params, responseType: 'blob' });
    downloadFile(response, 'export.csv');
  },
  exportExcel: async (params) => {
    const response = await api.get('/export/excel', { params, responseType: 'blob' });
    downloadFile(response, 'export.xlsx');
  },
  exportPdf: async (params) => {
    const response = await api.get('/export/pdf', { params, responseType: 'blob' });
    downloadFile(response, 'export.pdf');
  }
};
