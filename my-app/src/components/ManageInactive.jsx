import React from 'react';

const ManageInactive = () => {
  // Mock data for UI demonstration matching screenshots
  const inactiveEmployees = [
    { id: 'E012', name: 'Bemnet Worku', department: 'Finance', position: 'Intern', phone: '' }
  ];

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
                  <td style={{ padding: '12px' }}>{emp.phone}</td>
                  <td style={{ padding: '12px' }}>
                    <button style={{ padding: '5px 15px', color: 'var(--green-800)', border: '1px solid var(--green-300)', borderRadius: '4px', cursor: 'pointer', backgroundColor: 'var(--green-100)' }}>
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
