import React from 'react';
import { FiInbox } from 'react-icons/fi';
import '../styles/components.css';

const EmptyState = ({ message, actionLabel, onAction, icon }) => {
  return (
    <div className="empty-state">
      <div className="empty-icon">
        {icon || <FiInbox size={48} />}
      </div>
      <p className="empty-message">{message || 'No data found'}</p>
      {onAction && actionLabel && (
        <button className="btn btn-primary mt-4" onClick={onAction}>
          {actionLabel}
        </button>
      )}
    </div>
  );
};

export default EmptyState;
