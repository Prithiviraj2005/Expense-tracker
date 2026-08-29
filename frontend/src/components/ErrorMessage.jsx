import React from 'react';
import { FiAlertCircle } from 'react-icons/fi';
import '../styles/components.css';

const ErrorMessage = ({ message, onRetry }) => {
  if (!message) return null;
  
  return (
    <div className="error-message">
      <FiAlertCircle className="error-icon" />
      <span>{message}</span>
      {onRetry && (
        <button className="retry-btn" onClick={onRetry}>Retry</button>
      )}
    </div>
  );
};

export default ErrorMessage;
