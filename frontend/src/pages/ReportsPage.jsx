import React, { useState, useEffect } from 'react';
import { reportService } from '../services/reportService';
import { exportService } from '../services/exportService';
import { FiDownload } from 'react-icons/fi';
import Loading from '../components/Loading';
import ErrorMessage from '../components/ErrorMessage';
import '../styles/reports.css';

const formatCurrency = (amount) => {
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR'
  }).format(amount || 0);
};

const ReportsPage = () => {
  const [reportData, setReportData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  
  const [period, setPeriod] = useState('MONTHLY');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');

  useEffect(() => {
    if (period !== 'CUSTOM') {
      fetchReport();
    }
  }, [period]);

  const fetchReport = async () => {
    try {
      setLoading(true);
      const params = { period };
      if (period === 'CUSTOM') {
        if (!startDate || !endDate) return;
        params.startDate = startDate;
        params.endDate = endDate;
      }
      const data = await reportService.getReport(params);
      setReportData(data);
      setError('');
    } catch (err) {
      setError('Failed to load report data');
    } finally {
      setLoading(false);
    }
  };

  const handleExport = async (type) => {
    try {
      const params = { period };
      if (period === 'CUSTOM') {
        params.startDate = startDate;
        params.endDate = endDate;
      }
      
      if (type === 'CSV') await exportService.exportCsv(params);
      else if (type === 'EXCEL') await exportService.exportExcel(params);
      else if (type === 'PDF') await exportService.exportPdf(params);
    } catch (err) {
      setError(`Failed to export ${type}`);
    }
  };

  return (
    <div className="reports-page">
      <div className="page-header">
        <h2>Reports</h2>
        <div className="export-actions">
          <button className="btn btn-outline" onClick={() => handleExport('CSV')}><FiDownload /> CSV</button>
          <button className="btn btn-outline" onClick={() => handleExport('EXCEL')}><FiDownload /> Excel</button>
          <button className="btn btn-outline" onClick={() => handleExport('PDF')}><FiDownload /> PDF</button>
        </div>
      </div>

      <div className="report-controls section-card">
        <div className="form-group">
          <label>Report Period</label>
          <select value={period} onChange={(e) => setPeriod(e.target.value)}>
            <option value="DAILY">Daily</option>
            <option value="WEEKLY">Weekly</option>
            <option value="MONTHLY">Monthly</option>
            <option value="YEARLY">Yearly</option>
            <option value="CUSTOM">Custom Date Range</option>
          </select>
        </div>
        
        {period === 'CUSTOM' && (
          <div className="custom-dates">
            <div className="form-group">
              <label>Start Date</label>
              <input type="date" value={startDate} onChange={e => setStartDate(e.target.value)} />
            </div>
            <div className="form-group">
              <label>End Date</label>
              <input type="date" value={endDate} onChange={e => setEndDate(e.target.value)} />
            </div>
            <button className="btn btn-primary mt-4" onClick={fetchReport}>Generate</button>
          </div>
        )}
      </div>

      {error && <ErrorMessage message={error} />}

      {loading ? (
        <Loading />
      ) : reportData ? (
        <div className="report-content">
          <div className="summary-cards-grid">
            <div className="summary-card">
              <div className="summary-info">
                <p className="summary-label">Total Income</p>
                <h3 className="summary-value amount-income">{formatCurrency(reportData.totalIncome)}</h3>
              </div>
            </div>
            <div className="summary-card">
              <div className="summary-info">
                <p className="summary-label">Total Expense</p>
                <h3 className="summary-value amount-expense">{formatCurrency(reportData.totalExpense)}</h3>
              </div>
            </div>
            <div className="summary-card">
              <div className="summary-info">
                <p className="summary-label">Net Balance</p>
                <h3 className="summary-value">{formatCurrency(reportData.balance)}</h3>
              </div>
            </div>
          </div>

          <div className="section-card mt-6">
            <h3>Category Breakdown</h3>
            <div className="table-responsive">
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Category</th>
                    <th>Total Amount</th>
                    <th>Percentage</th>
                  </tr>
                </thead>
                <tbody>
                  {reportData.categorySummaries?.map((item, idx) => (
                    <tr key={idx}>
                      <td><strong>{item.category}</strong></td>
                      <td>{formatCurrency(item.amount)}</td>
                      <td>{typeof item.percentage === 'number' ? `${item.percentage.toFixed(1)}%` : `${item.percentage}%`}</td>
                    </tr>
                  ))}
                  {(!reportData.categorySummaries || reportData.categorySummaries.length === 0) && (
                    <tr>
                      <td colSpan="3" className="text-center py-4">No category data available for this period</td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>

          <div className="section-card mt-6">
            <h3>Transactions in Period ({reportData.transactions?.length || 0})</h3>
            <div className="table-responsive">
              <table className="data-table">
                <thead>
                  <tr>
                    <th>Date</th>
                    <th>Description</th>
                    <th>Category</th>
                    <th>Type</th>
                    <th>Payment Method</th>
                    <th>Amount</th>
                  </tr>
                </thead>
                <tbody>
                  {reportData.transactions?.map((item) => (
                    <tr key={item.id}>
                      <td>{item.transactionDate}</td>
                      <td>{item.description || '-'}</td>
                      <td>{item.categoryName}</td>
                      <td>
                        <span className={`badge badge-${item.type?.toLowerCase()}`}>
                          {item.type}
                        </span>
                      </td>
                      <td>{item.paymentMethod || '-'}</td>
                      <td className={`amount-${item.type?.toLowerCase()}`}>
                        {item.type === 'INCOME' ? '+' : '-'}{formatCurrency(item.amount)}
                      </td>
                    </tr>
                  ))}
                  {(!reportData.transactions || reportData.transactions.length === 0) && (
                    <tr>
                      <td colSpan="6" className="text-center py-4">No transactions found for this period</td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      ) : null}
    </div>
  );
};

export default ReportsPage;
