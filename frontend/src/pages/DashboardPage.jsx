import React, { useState, useEffect } from 'react';
import { dashboardService } from '../services/dashboardService';
import { transactionService } from '../services/transactionService';
import SummaryCards from '../components/SummaryCards';
import TransactionTable from '../components/TransactionTable';
import Loading from '../components/Loading';
import ErrorMessage from '../components/ErrorMessage';
import { useNavigate } from 'react-router-dom';
import {
  PieChart, Pie, Cell, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip as RechartsTooltip, Legend, LineChart, Line, ResponsiveContainer
} from 'recharts';
import '../styles/dashboard.css';

const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884d8', '#82ca9d', '#ffc658'];

const DashboardPage = () => {
  const [summary, setSummary] = useState(null);
  const [categoryData, setCategoryData] = useState([]);
  const [monthlyData, setMonthlyData] = useState([]);
  const [recentTransactions, setRecentTransactions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  
  const navigate = useNavigate();

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      const [sumData, catData, monthData, transData] = await Promise.all([
        dashboardService.getSummary(),
        dashboardService.getCategorySummary('EXPENSE'),
        dashboardService.getMonthlySummary(),
        transactionService.getTransactions({ page: 0, size: 5, sort: 'transactionDate,desc' })
      ]);
      
      setSummary(sumData);
      setCategoryData(catData);
      setMonthlyData(monthData);
      setRecentTransactions(transData.content || transData);
    } catch (err) {
      setError('Failed to load dashboard data');
    } finally {
      setLoading(false);
    }
  };

  if (loading) return <Loading />;
  if (error) return <ErrorMessage message={error} onRetry={fetchDashboardData} />;

  return (
    <div className="dashboard-page">
      <div className="page-header">
        <h2>Dashboard</h2>
        <button className="btn btn-primary" onClick={() => navigate('/transactions/new')}>
          Add Transaction
        </button>
      </div>

      <SummaryCards summary={summary} />

      <div className="charts-grid">
        <div className="chart-card">
          <h3>Expenses by Category</h3>
          {categoryData.length > 0 ? (
            <div className="chart-container">
              <ResponsiveContainer width="100%" height={300}>
                <PieChart>
                  <Pie
                    data={categoryData}
                    cx="50%"
                    cy="50%"
                    labelLine={false}
                    outerRadius={100}
                    fill="#8884d8"
                    dataKey="amount"
                    nameKey="category"
                    label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                  >
                    {categoryData.map((entry, index) => (
                      <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                    ))}
                  </Pie>
                  <RechartsTooltip formatter={(value) => `₹${value}`} />
                  <Legend />
                </PieChart>
              </ResponsiveContainer>
            </div>
          ) : (
            <p className="no-data-msg">No expense data available</p>
          )}
        </div>

        <div className="chart-card">
          <h3>Income vs Expense</h3>
          {monthlyData.length > 0 ? (
            <div className="chart-container">
              <ResponsiveContainer width="100%" height={300}>
                <LineChart data={monthlyData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="month" />
                  <YAxis />
                  <RechartsTooltip formatter={(value) => `₹${value}`} />
                  <Legend />
                  <Line type="monotone" dataKey="income" stroke="#22c55e" name="Income" />
                  <Line type="monotone" dataKey="expense" stroke="#ef4444" name="Expense" />
                </LineChart>
              </ResponsiveContainer>
            </div>
          ) : (
             <p className="no-data-msg">No monthly data available</p>
          )}
        </div>
      </div>

      <div className="recent-transactions section-card">
        <div className="section-header">
          <h3>Recent Transactions</h3>
          <button className="btn-link" onClick={() => navigate('/transactions')}>View All</button>
        </div>
        <TransactionTable 
          transactions={recentTransactions} 
          onEdit={(id) => navigate(`/transactions/edit/${id}`)}
          onDelete={() => {}} // Handle deletion appropriately or just redirect to transactions page
          loading={false}
        />
      </div>
    </div>
  );
};

export default DashboardPage;
