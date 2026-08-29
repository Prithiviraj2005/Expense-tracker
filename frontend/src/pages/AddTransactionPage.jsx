import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { transactionService } from '../services/transactionService';
import TransactionForm from '../components/TransactionForm';
import ErrorMessage from '../components/ErrorMessage';
import '../styles/transactions.css';

const AddTransactionPage = () => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (formData) => {
    try {
      setLoading(true);
      await transactionService.createTransaction(formData);
      navigate('/transactions');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to create transaction');
      setLoading(false);
    }
  };

  return (
    <div className="transaction-form-page">
      <div className="page-header">
        <h2>Add New Transaction</h2>
        <button className="btn btn-secondary" onClick={() => navigate('/transactions')}>
          Back
        </button>
      </div>

      {error && <ErrorMessage message={error} />}

      <div className="section-card">
        <TransactionForm onSubmit={handleSubmit} loading={loading} />
      </div>
    </div>
  );
};

export default AddTransactionPage;
