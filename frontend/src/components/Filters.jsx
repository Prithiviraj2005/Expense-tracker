import React from 'react';
import '../styles/components.css';

const Filters = ({ filters, onFilterChange, categories }) => {
  const handleChange = (e) => {
    const { name, value } = e.target;
    onFilterChange({ ...filters, [name]: value });
  };

  const handleClear = () => {
    onFilterChange({
      type: '',
      categoryId: '',
      startDate: '',
      endDate: '',
      search: ''
    });
  };

  return (
    <div className="filters-container">
      <input
        type="text"
        name="search"
        placeholder="Search description..."
        value={filters.search || ''}
        onChange={handleChange}
        className="filter-input"
      />
      <select name="type" value={filters.type || ''} onChange={handleChange} className="filter-input">
        <option value="">All Types</option>
        <option value="INCOME">Income</option>
        <option value="EXPENSE">Expense</option>
      </select>
      <select name="categoryId" value={filters.categoryId || ''} onChange={handleChange} className="filter-input">
        <option value="">All Categories</option>
        {categories?.map(c => (
          <option key={c.id} value={c.id}>{c.name}</option>
        ))}
      </select>
      <input
        type="date"
        name="startDate"
        value={filters.startDate || ''}
        onChange={handleChange}
        className="filter-input"
        placeholder="Start Date"
      />
      <input
        type="date"
        name="endDate"
        value={filters.endDate || ''}
        onChange={handleChange}
        className="filter-input"
        placeholder="End Date"
      />
      <button className="btn btn-secondary" onClick={handleClear}>Clear</button>
    </div>
  );
};

export default Filters;
