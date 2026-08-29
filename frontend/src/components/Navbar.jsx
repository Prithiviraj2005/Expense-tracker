import React, { useContext } from 'react';
import { AuthContext } from '../context/AuthContext';
import { FiLogOut, FiMenu } from 'react-icons/fi';
import '../styles/layout.css';

const Navbar = ({ toggleSidebar }) => {
  const { user, logout } = useContext(AuthContext);

  return (
    <header className="navbar">
      <div className="navbar-left">
        <button className="menu-btn" onClick={toggleSidebar}>
          <FiMenu size={24} />
        </button>
        <h1 className="app-title">ExpenseTracker</h1>
      </div>
      <div className="navbar-right">
        <span className="user-greeting">Hi, {user?.name || 'User'}</span>
        <button className="logout-btn" onClick={logout} title="Logout">
          <FiLogOut size={20} />
          <span className="logout-text">Logout</span>
        </button>
      </div>
    </header>
  );
};

export default Navbar;
