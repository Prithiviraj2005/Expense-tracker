import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { transactionService } from '../services/transactionService';
import TransactionForm from '../components/TransactionForm';
import ErrorMessage from '../components/ErrorMessage';
import Loading from '../components/Loading';
import '../styles/transactions.css';

const EditTransactionPage = () => {
  const { id } = useParams();
  const [transaction, setTransaction] = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const fetchTransaction = async () => {
      try {
        const data = await transactionService.getTransaction(id);
        setTransaction(data);
      } catch (err) {
        setError('Failed to load transaction details');
      } finally {
        setLoading(false);
      }
    };
    fetchTransaction();
  }, [id]);

  const handleSubmit = async (formData) => {
    try {
      setSaving(true);
      await transactionService.updateTransaction(id, formData);
      navigate('/transactions');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to update transaction');
      setSaving(false);
    }
  };

  if (loading) return <Loading />;

  return (
    <div className="transaction-form-page">
      <div className="page-header">
        <h2>Edit Transaction</h2>
        <button className="btn btn-secondary" onClick={() => navigate('/transactions')}>
          Back
        </button>
      </div>

      {error && <ErrorMessage message={error} />}

      {transaction && (
        <div className="section-card">
          <TransactionForm 
            initialData={transaction} 
            onSubmit={handleSubmit} 
            loading={saving} 
          />
        </div>
      )}
    </div>
  );
};

export default EditTransactionPage;
