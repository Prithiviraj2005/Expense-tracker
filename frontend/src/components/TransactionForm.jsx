import React, { useState, useEffect } from 'react';
import { categoryService } from '../services/categoryService';
import '../styles/components.css';

const TransactionForm = ({ initialData, onSubmit, loading }) => {
  const [formData, setFormData] = useState({
    amount: '',
    type: 'EXPENSE',
    description: '',
    transactionDate: new Date().toISOString().split('T')[0],
    paymentMethod: 'CASH',
    categoryId: ''
  });

  const [categories, setCategories] = useState([]);

  useEffect(() => {
    if (initialData) {
      setFormData({
        ...initialData,
        transactionDate: initialData.transactionDate.split('T')[0],
        categoryId: initialData.category?.id || initialData.categoryId || ''
      });
    }
  }, [initialData]);

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const data = await categoryService.getCategories(formData.type);
        setCategories(data);
      } catch (error) {
        console.error('Failed to fetch categories', error);
      }
    };
    fetchCategories();
  }, [formData.type]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'amount' ? Number(value) : value,
      ...(name === 'type' && { categoryId: '' }) // Reset category on type change
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit(formData);
  };

  return (
    <form className="transaction-form" onSubmit={handleSubmit}>
      <div className="form-group">
        <label>Type</label>
        <select name="type" value={formData.type} onChange={handleChange} required>
          <option value="EXPENSE">Expense</option>
          <option value="INCOME">Income</option>
        </select>
      </div>

      <div className="form-group">
        <label>Amount</label>
        <input 
          type="number" 
          name="amount" 
          value={formData.amount} 
          onChange={handleChange} 
          min="0.01" 
          step="0.01" 
          required 
        />
      </div>

      <div className="form-group">
        <label>Category</label>
        <select name="categoryId" value={formData.categoryId} onChange={handleChange} required>
          <option value="" disabled>Select a category</option>
          {categories.map(cat => (
            <option key={cat.id} value={cat.id}>{cat.name}</option>
          ))}
        </select>
      </div>

      <div className="form-group">
        <label>Date</label>
        <input 
          type="date" 
          name="transactionDate" 
          value={formData.transactionDate} 
          onChange={handleChange} 
          required 
        />
      </div>

      <div className="form-group">
        <label>Payment Method</label>
        <select name="paymentMethod" value={formData.paymentMethod} onChange={handleChange} required>
          <option value="CASH">Cash</option>
          <option value="CREDIT_CARD">Credit Card</option>
          <option value="DEBIT_CARD">Debit Card</option>
          <option value="BANK_TRANSFER">Bank Transfer</option>
          <option value="UPI">UPI</option>
          <option value="OTHER">Other</option>
        </select>
      </div>

      <div className="form-group full-width">
        <label>Description</label>
        <textarea 
          name="description" 
          value={formData.description} 
          onChange={handleChange} 
          rows="3"
        ></textarea>
      </div>

      <div className="form-actions full-width">
        <button type="submit" className="btn btn-primary" disabled={loading}>
          {loading ? 'Saving...' : 'Save Transaction'}
        </button>
      </div>
    </form>
  );
};

export default TransactionForm;
