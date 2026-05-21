import React from 'react';
import { useAuth } from '../../app/providers/AuthContext';
import { FileSpreadsheet } from 'lucide-react';

const ManagerReports = () => {
  const { user } = useAuth();

  const handleExport = (type) => {
    alert(`Exporting ${type} report for department: ${user?.department}`);
    // Implement actual CSV/PDF export logic here
  };

  return (
    <div style={{ padding: '20px', height: '100%', display: 'flex', flexDirection: 'column' }}>
      <h2 style={{ color: 'var(--green-900)' }}>Reports & Exports ({user?.department})</h2>
      
      <div className="glass-panel" style={{ padding: '30px', marginTop: '20px', display: 'flex', gap: '20px', flexWrap: 'wrap' }}>
        
        <div style={{ border: '1px solid var(--green-300)', padding: '20px', borderRadius: '10px', width: '250px', textAlign: 'center', backgroundColor: 'var(--green-100)' }}>
            <FileSpreadsheet size={40} color="var(--green-700)" style={{ marginBottom: '15px' }} />
            <h3 style={{ margin: '0 0 10px 0', color: 'var(--green-900)' }}>Employee Directory</h3>
            <p style={{ fontSize: '12px', color: '#666', marginBottom: '20px' }}>Export a list of all employees in your department.</p>
            <button className="btn-primary" onClick={() => handleExport('Employee')} style={{ width: '100%' }}>Export PDF</button>
        </div>

        <div style={{ border: '1px solid var(--green-300)', padding: '20px', borderRadius: '10px', width: '250px', textAlign: 'center', backgroundColor: 'var(--green-100)' }}>
            <FileSpreadsheet size={40} color="var(--green-700)" style={{ marginBottom: '15px' }} />
            <h3 style={{ margin: '0 0 10px 0', color: 'var(--green-900)' }}>Attendance Report</h3>
            <p style={{ fontSize: '12px', color: '#666', marginBottom: '20px' }}>Export attendance records for your department.</p>
            <button className="btn-primary" onClick={() => handleExport('Attendance')} style={{ width: '100%' }}>Export CSV</button>
        </div>

        <div style={{ border: '1px solid var(--green-300)', padding: '20px', borderRadius: '10px', width: '250px', textAlign: 'center', backgroundColor: 'var(--green-100)' }}>
            <FileSpreadsheet size={40} color="var(--green-700)" style={{ marginBottom: '15px' }} />
            <h3 style={{ margin: '0 0 10px 0', color: 'var(--green-900)' }}>Leave Report</h3>
            <p style={{ fontSize: '12px', color: '#666', marginBottom: '20px' }}>Export leave history and status for your department.</p>
            <button className="btn-primary" onClick={() => handleExport('Leave')} style={{ width: '100%' }}>Export CSV</button>
        </div>
        
      </div>
    </div>
  );
};

export default ManagerReports;
