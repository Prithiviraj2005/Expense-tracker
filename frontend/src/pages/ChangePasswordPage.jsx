import React, { useState } from 'react';
import { authService } from '../services/authService';
import ErrorMessage from '../components/ErrorMessage';
import { useNavigate } from 'react-router-dom';
import '../styles/auth.css';

const ChangePasswordPage = () => {
  const [formData, setFormData] = useState({
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    
    if (formData.newPassword !== formData.confirmPassword) {
      return setError('New passwords do not match');
    }
    
    setLoading(true);
    try {
      await authService.changePassword({
        currentPassword: formData.currentPassword,
        newPassword: formData.newPassword
      });
      setSuccess('Password changed successfully');
      setFormData({ currentPassword: '', newPassword: '', confirmPassword: '' });
      setTimeout(() => navigate('/profile'), 2000);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to change password');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="change-password-page">
      <div className="page-header">
        <h2>Change Password</h2>
      </div>

      <div className="section-card max-w-md">
        {error && <ErrorMessage message={error} />}
        {success && <div className="success-message">{success}</div>}
        
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Current Password</label>
            <input 
              type="password" 
              value={formData.currentPassword} 
              onChange={e => setFormData({...formData, currentPassword: e.target.value})} 
              required 
            />
          </div>
          <div className="form-group">
            <label>New Password</label>
            <input 
              type="password" 
              value={formData.newPassword} 
              onChange={e => setFormData({...formData, newPassword: e.target.value})} 
              required 
            />
          </div>
          <div className="form-group">
            <label>Confirm New Password</label>
            <input 
              type="password" 
              value={formData.confirmPassword} 
              onChange={e => setFormData({...formData, confirmPassword: e.target.value})} 
              required 
            />
          </div>
          <div className="form-actions">
             <button type="button" className="btn btn-secondary" onClick={() => navigate('/profile')}>Cancel</button>
             <button type="submit" className="btn btn-primary" disabled={loading}>
              {loading ? 'Changing...' : 'Change Password'}
             </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ChangePasswordPage;
