import React from 'react';
import { Link } from 'react-router-dom';
import '../styles/auth.css'; // Reusing some auth styles for centering

const NotFoundPage = () => {
  return (
    <div className="auth-container">
      <div className="auth-card" style={{ textAlign: 'center' }}>
        <h1 style={{ fontSize: '4rem', color: 'var(--primary-color)' }}>404</h1>
        <h2>Page Not Found</h2>
        <p style={{ margin: '1rem 0', color: 'var(--text-muted)' }}>
          The page you are looking for doesn't exist or has been moved.
        </p>
        <Link to="/dashboard" className="btn btn-primary" style={{ display: 'inline-block' }}>
          Back to Dashboard
        </Link>
      </div>
    </div>
  );
};

export default NotFoundPage;
