import React from 'react';
import axios from 'axios';

const ManageInactive = () => {
  const [inactiveEmployees, setInactiveEmployees] = React.useState([]);

  React.useEffect(() => {
    fetchInactive();
  }, []);

  const fetchInactive = async () => {
    try {
      const resp = await axios.get('http://localhost:8000/api/employees/inactive/');
      setInactiveEmployees(resp.data.results || resp.data || []);
    } catch (e) {
      console.error(e);
    }
  };

  const handleReactivate = async (id) => {
    try {
      await axios.post(`http://localhost:8000/api/employees/${id}/reactivate/`);
      alert('Employee reactivated successfully');
      fetchInactive();
    } catch (e) {
      alert('Failed to reactivate: ' + (e.response?.data?.message || e.message));
    }
  };

  return (
    <div style={{ padding: '20px' }}>
      <h2 style={{ color: 'var(--green-900)' }}>Manage Inactive Employees</h2>
      
      <div className="glass-panel" style={{ padding: '0', overflow: 'hidden' }}>
        <table style={{ width: '100%', textAlign: 'left', borderCollapse: 'collapse', fontSize: '14px' }}>
          <thead>
            <tr style={{ borderBottom: '2px solid var(--green-300)', backgroundColor: 'var(--green-100)', color: 'var(--green-900)' }}>
              <th style={{ padding: '12px' }}>ID</th>
              <th style={{ padding: '12px' }}>Name</th>
              <th style={{ padding: '12px' }}>Department</th>
              <th style={{ padding: '12px' }}>Position</th>
              <th style={{ padding: '12px' }}>Phone</th>
              <th style={{ padding: '12px' }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {inactiveEmployees.length > 0 ? (
              inactiveEmployees.map((emp, index) => (
                <tr key={index} style={{ borderBottom: '1px solid #eee' }}>
                  <td style={{ padding: '12px' }}>{emp.id}</td>
                  <td style={{ padding: '12px' }}>{emp.name}</td>
                  <td style={{ padding: '12px' }}>{emp.department}</td>
                  <td style={{ padding: '12px' }}>{emp.position}</td>
                  <td style={{ padding: '12px' }}>{emp.phone_number || emp.phone}</td>
                  <td style={{ padding: '12px' }}>
                    <button onClick={() => handleReactivate(emp.id)} style={{ padding: '5px 15px', color: 'var(--green-800)', border: '1px solid var(--green-300)', borderRadius: '4px', cursor: 'pointer', backgroundColor: 'var(--green-100)' }}>
                      Reactivate
                    </button>
                  </td>
                </tr>
              ))
            ) : (
               <tr>
                   <td colSpan="6" style={{ padding: '20px', textAlign: 'center', color: '#999' }}>No inactive employees found</td>
               </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default ManageInactive;
