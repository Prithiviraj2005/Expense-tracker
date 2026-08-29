import React from 'react';
import { NavLink } from 'react-router-dom';
import { 
  FiHome, 
  FiList, 
  FiGrid, 
  FiPieChart, 
  FiUser 
} from 'react-icons/fi';
import '../styles/layout.css';

const Sidebar = ({ isOpen, closeSidebar }) => {
  const navItems = [
    { path: '/dashboard', name: 'Dashboard', icon: <FiHome /> },
    { path: '/transactions', name: 'Transactions', icon: <FiList /> },
    { path: '/categories', name: 'Categories', icon: <FiGrid /> },
    { path: '/reports', name: 'Reports', icon: <FiPieChart /> },
    { path: '/profile', name: 'Profile', icon: <FiUser /> },
  ];

  return (
    <>
      {isOpen && <div className="sidebar-overlay" onClick={closeSidebar}></div>}
      <aside className={`sidebar ${isOpen ? 'open' : ''}`}>
        <nav className="sidebar-nav">
          {navItems.map((item) => (
            <NavLink 
              key={item.path} 
              to={item.path}
              className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}
              onClick={closeSidebar}
            >
              <span className="nav-icon">{item.icon}</span>
              <span className="nav-text">{item.name}</span>
            </NavLink>
          ))}
        </nav>
      </aside>
    </>
  );
};

export default Sidebar;
