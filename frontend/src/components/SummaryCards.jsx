import React from 'react';
import { FiTrendingUp, FiTrendingDown, FiDollarSign } from 'react-icons/fi';
import '../styles/components.css';

const formatCurrency = (amount) => {
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR'
  }).format(amount || 0);
};

const SummaryCards = ({ summary }) => {
  if (!summary) return null;

  return (
    <div className="summary-cards-grid">
      <div className="summary-card">
        <div className="summary-icon icon-blue">
          <FiDollarSign />
        </div>
        <div className="summary-info">
          <p className="summary-label">Total Balance</p>
          <h3 className="summary-value">{formatCurrency(summary.balance)}</h3>
        </div>
      </div>
      <div className="summary-card">
        <div className="summary-icon icon-green">
          <FiTrendingUp />
        </div>
        <div className="summary-info">
          <p className="summary-label">Total Income</p>
          <h3 className="summary-value amount-income">{formatCurrency(summary.totalIncome)}</h3>
        </div>
      </div>
      <div className="summary-card">
        <div className="summary-icon icon-red">
          <FiTrendingDown />
        </div>
        <div className="summary-info">
          <p className="summary-label">Total Expense</p>
          <h3 className="summary-value amount-expense">{formatCurrency(summary.totalExpense)}</h3>
        </div>
      </div>
      <div className="summary-card">
        <div className="summary-icon icon-green">
          <FiTrendingUp />
        </div>
        <div className="summary-info">
          <p className="summary-label">Monthly Income</p>
          <h3 className="summary-value amount-income">{formatCurrency(summary.currentMonthIncome)}</h3>
        </div>
      </div>
      <div className="summary-card">
        <div className="summary-icon icon-red">
          <FiTrendingDown />
        </div>
        <div className="summary-info">
          <p className="summary-label">Monthly Expense</p>
          <h3 className="summary-value amount-expense">{formatCurrency(summary.currentMonthExpense)}</h3>
        </div>
      </div>
    </div>
  );
};

export default SummaryCards;
