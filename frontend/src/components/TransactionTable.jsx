import React from 'react';
import { FiEdit2, FiTrash2 } from 'react-icons/fi';
import Loading from './Loading';
import EmptyState from './EmptyState';
import '../styles/components.css';

const formatCurrency = (amount) => {
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR'
  }).format(amount);
};

const formatDate = (dateString) => {
  return new Intl.DateTimeFormat('en-IN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  }).format(new Date(dateString));
};

const TransactionTable = ({ transactions, onEdit, onDelete, loading }) => {
  if (loading) return <Loading />;
  if (!transactions || transactions.length === 0) return <EmptyState message="No transactions found." />;

  return (
    <div className="table-responsive">
      <table className="data-table">
        <thead>
          <tr>
            <th>Date</th>
            <th>Description</th>
            <th>Category</th>
            <th>Type</th>
            <th>Amount</th>
            <th>Payment Method</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {transactions.map(t => (
            <tr key={t.id}>
              <td>{formatDate(t.transactionDate)}</td>
              <td>{t.description || '-'}</td>
              <td>{t.categoryName || t.category?.name || 'Uncategorized'}</td>
              <td>
                <span className={`badge badge-${t.type?.toLowerCase()}`}>
                  {t.type}
                </span>
              </td>
              <td className={`amount-${t.type?.toLowerCase()}`}>
                {formatCurrency(t.amount)}
              </td>
              <td>{t.paymentMethod ? t.paymentMethod.replace('_', ' ') : '-'}</td>
              <td className="actions-cell">
                <button className="btn-icon text-primary" onClick={() => onEdit(t.id)} title="Edit">
                  <FiEdit2 />
                </button>
                <button className="btn-icon text-danger" onClick={() => onDelete(t.id)} title="Delete">
                  <FiTrash2 />
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default TransactionTable;
