import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Search } from 'lucide-react';
import { useAuth } from '../../app/providers/AuthContext';

const ManagerEmployeeDirectory = () => {
  const { user } = useAuth();
  const [employees, setEmployees] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  
  useEffect(() => {
    fetchEmployees();
  }, [user]);

  const fetchEmployees = async () => {
    try {
      if (!user?.department) return;
      const resp = await axios.get(`http://localhost:8000/api/employees/?department=${user.department}`);
      setEmployees(resp.data.results || resp.data || []);
    } catch (e) {
      console.error("Failed to fetch employees", e);
    }
  };

  const handleExportPDF = () => {
    // Implement simple print/export functionality
    window.print();
  };

  const displayEmployees = employees.filter(e => 
     (e.name && e.name.toLowerCase().includes(searchQuery.toLowerCase())) || 
     (e.employee_id && e.employee_id.toLowerCase().includes(searchQuery.toLowerCase()))
  );

  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100%', padding: '20px' }}>
      <h2 style={{ color: 'var(--green-900)' }}>Employee Directory ({user?.department})</h2>
      
      <div className="glass-panel" style={{ padding: '15px', marginBottom: '20px', display: 'flex', justifyContent: 'space-between' }}>
         <div style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
             <Search size={18} color="var(--green-700)" />
             <input className="input-field" placeholder="Search ID or Name..." value={searchQuery} onChange={(e) => setSearchQuery(e.target.value)} style={{ borderRadius: '20px', padding: '8px 15px', width: '250px' }} />
         </div>
         <button className="btn-primary" onClick={handleExportPDF} style={{ borderRadius: '20px' }}>Export to PDF</button>
      </div>

      <div className="glass-panel" style={{ flex: 1, padding: '20px', overflowY: 'auto' }}>
        <table style={{ width: '100%', textAlign: 'left', borderCollapse: 'collapse', fontSize: '14px' }}>
          <thead>
            <tr style={{ borderBottom: '2px solid var(--green-400)', color: 'var(--green-900)' }}>
              <th style={{ padding: '10px' }}>ID</th>
              <th>Name</th>
              <th>Department</th>
              <th>Position</th>
              <th>Phone</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {displayEmployees.map(e => (
              <tr key={e.employee_id} style={{ borderBottom: '1px solid #eee' }}>
                <td style={{ padding: '12px 10px' }}>{e.employee_id}</td>
                <td>{e.name}</td>
                <td>{e.department}</td>
                <td>{e.position}</td>
                <td>{e.phone_number}</td>
                <td>
                  <span style={{ 
                    padding: '3px 8px', borderRadius: '12px', fontSize: '12px',
                    backgroundColor: e.is_active ? '#e8f5e9' : '#ffebee',
                    color: e.is_active ? '#2e7d32' : '#c62828'
                  }}>
                    {e.is_active ? 'Active' : 'Inactive'}
                  </span>
                </td>
              </tr>
            ))}
            {displayEmployees.length === 0 && (
              <tr>
                <td colSpan="6" style={{ textAlign: 'center', padding: '20px', color: '#666' }}>No employees found in your department.</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default ManagerEmployeeDirectory;
