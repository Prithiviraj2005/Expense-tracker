import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { transactionService } from '../services/transactionService';
import { categoryService } from '../services/categoryService';
import TransactionTable from '../components/TransactionTable';
import Filters from '../components/Filters';
import ConfirmDialog from '../components/ConfirmDialog';
import ErrorMessage from '../components/ErrorMessage';
import '../styles/transactions.css';

const TransactionsPage = () => {
  const [transactions, setTransactions] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  
  const [filters, setFilters] = useState({
    type: '',
    categoryId: '',
    startDate: '',
    endDate: '',
    search: ''
  });
  
  const [deleteDialog, setDeleteDialog] = useState({ isOpen: false, id: null });
  const navigate = useNavigate();

  useEffect(() => {
    fetchCategories();
  }, []);

  useEffect(() => {
    fetchTransactions();
  }, [page, filters]);

  const fetchCategories = async () => {
    try {
      const data = await categoryService.getCategories();
      setCategories(data);
    } catch (err) {
      console.error('Failed to load categories');
    }
  };

  const fetchTransactions = async () => {
    try {
      setLoading(true);
      const params = {
        page,
        size: 10,
        sort: 'transactionDate,desc',
        ...filters
      };
      
      const data = await transactionService.getTransactions(params);
      setTransactions(data.content || data);
      setTotalPages(data.totalPages || 1);
      setError('');
    } catch (err) {
      setError('Failed to load transactions');
    } finally {
      setLoading(false);
    }
  };

  const handleFilterChange = (newFilters) => {
    setFilters(newFilters);
    setPage(0); // Reset to first page on filter change
  };

  const handleDeleteClick = (id) => {
    setDeleteDialog({ isOpen: true, id });
  };

  const handleDeleteConfirm = async () => {
    try {
      await transactionService.deleteTransaction(deleteDialog.id);
      fetchTransactions();
      setDeleteDialog({ isOpen: false, id: null });
    } catch (err) {
      setError('Failed to delete transaction');
      setDeleteDialog({ isOpen: false, id: null });
    }
  };

  return (
    <div className="transactions-page">
      <div className="page-header">
        <h2>Transactions</h2>
        <button className="btn btn-primary" onClick={() => navigate('/transactions/new')}>
          Add Transaction
        </button>
      </div>

      <Filters 
        filters={filters} 
        onFilterChange={handleFilterChange} 
        categories={categories}
      />

      {error && <ErrorMessage message={error} />}

      <div className="section-card">
        <TransactionTable 
          transactions={transactions} 
          onEdit={(id) => navigate(`/transactions/edit/${id}`)}
          onDelete={handleDeleteClick}
          loading={loading}
        />
        
        {!loading && totalPages > 1 && (
          <div className="pagination">
            <button 
              className="btn btn-secondary" 
              disabled={page === 0} 
              onClick={() => setPage(p => p - 1)}
            >
              Previous
            </button>
            <span className="page-info">Page {page + 1} of {totalPages}</span>
            <button 
              className="btn btn-secondary" 
              disabled={page >= totalPages - 1} 
              onClick={() => setPage(p => p + 1)}
            >
              Next
            </button>
          </div>
        )}
      </div>

      <ConfirmDialog 
        isOpen={deleteDialog.isOpen}
        title="Delete Transaction"
        message="Are you sure you want to delete this transaction? This action cannot be undone."
        onConfirm={handleDeleteConfirm}
        onCancel={() => setDeleteDialog({ isOpen: false, id: null })}
      />
    </div>
  );
};

export default TransactionsPage;
