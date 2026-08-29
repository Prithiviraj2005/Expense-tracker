import React, { useState, useEffect } from 'react';
import { categoryService } from '../services/categoryService';
import { FiEdit2, FiTrash2, FiPlus } from 'react-icons/fi';
import Loading from '../components/Loading';
import ErrorMessage from '../components/ErrorMessage';
import ConfirmDialog from '../components/ConfirmDialog';
import '../styles/components.css';

const CategoriesPage = () => {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [activeTab, setActiveTab] = useState('EXPENSE');
  
  const [showModal, setShowModal] = useState(false);
  const [formData, setFormData] = useState({ name: '', type: 'EXPENSE', description: '' });
  const [editingId, setEditingId] = useState(null);
  
  const [deleteDialog, setDeleteDialog] = useState({ isOpen: false, id: null });

  useEffect(() => {
    fetchCategories();
  }, [activeTab]);

  const fetchCategories = async () => {
    try {
      setLoading(true);
      const data = await categoryService.getCategories(activeTab);
      setCategories(data);
      setError('');
    } catch (err) {
      setError('Failed to load categories');
    } finally {
      setLoading(false);
    }
  };

  const handleOpenModal = (category = null) => {
    if (category) {
      setFormData({ name: category.name, type: category.type, description: category.description || '' });
      setEditingId(category.id);
    } else {
      setFormData({ name: '', type: activeTab, description: '' });
      setEditingId(null);
    }
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setFormData({ name: '', type: 'EXPENSE', description: '' });
    setEditingId(null);
    setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editingId) {
        await categoryService.updateCategory(editingId, formData);
      } else {
        await categoryService.createCategory(formData);
      }
      handleCloseModal();
      fetchCategories();
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to save category');
    }
  };

  const handleDeleteConfirm = async () => {
    try {
      await categoryService.deleteCategory(deleteDialog.id);
      fetchCategories();
      setDeleteDialog({ isOpen: false, id: null });
    } catch (err) {
      setError(err.response?.data?.message || 'Cannot delete category with existing transactions');
      setDeleteDialog({ isOpen: false, id: null });
    }
  };

  return (
    <div className="categories-page">
      <div className="page-header">
        <h2>Categories</h2>
        <button className="btn btn-primary" onClick={() => handleOpenModal()}>
          <FiPlus /> Add Category
        </button>
      </div>

      {error && <ErrorMessage message={error} />}

      <div className="tabs">
        <button 
          className={`tab ${activeTab === 'EXPENSE' ? 'active' : ''}`}
          onClick={() => setActiveTab('EXPENSE')}
        >
          Expenses
        </button>
        <button 
          className={`tab ${activeTab === 'INCOME' ? 'active' : ''}`}
          onClick={() => setActiveTab('INCOME')}
        >
          Income
        </button>
      </div>

      <div className="section-card">
        {loading ? (
          <Loading />
        ) : (
          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Description</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {categories.length === 0 ? (
                  <tr>
                    <td colSpan="3" className="text-center py-4">No categories found</td>
                  </tr>
                ) : (
                  categories.map(c => (
                    <tr key={c.id}>
                      <td>{c.name}</td>
                      <td>{c.description || '-'}</td>
                      <td className="actions-cell">
                        <button className="btn-icon text-primary" onClick={() => handleOpenModal(c)}>
                          <FiEdit2 />
                        </button>
                        <button className="btn-icon text-danger" onClick={() => setDeleteDialog({ isOpen: true, id: c.id })}>
                          <FiTrash2 />
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {showModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h3 className="modal-title">{editingId ? 'Edit Category' : 'Add Category'}</h3>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Name</label>
                <input 
                  type="text" 
                  value={formData.name} 
                  onChange={e => setFormData({...formData, name: e.target.value})} 
                  required 
                />
              </div>
              {!editingId && (
                <div className="form-group">
                  <label>Type</label>
                  <select 
                    value={formData.type} 
                    onChange={e => setFormData({...formData, type: e.target.value})}
                  >
                    <option value="EXPENSE">Expense</option>
                    <option value="INCOME">Income</option>
                  </select>
                </div>
              )}
              <div className="form-group">
                <label>Description</label>
                <textarea 
                  value={formData.description} 
                  onChange={e => setFormData({...formData, description: e.target.value})}
                  rows="3"
                ></textarea>
              </div>
              <div className="modal-actions">
                <button type="button" className="btn btn-secondary" onClick={handleCloseModal}>Cancel</button>
                <button type="submit" className="btn btn-primary">Save</button>
              </div>
            </form>
          </div>
        </div>
      )}

      <ConfirmDialog 
        isOpen={deleteDialog.isOpen}
        title="Delete Category"
        message="Are you sure you want to delete this category?"
        onConfirm={handleDeleteConfirm}
        onCancel={() => setDeleteDialog({ isOpen: false, id: null })}
      />
    </div>
  );
};

export default CategoriesPage;
