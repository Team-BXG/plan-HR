import React, { useState } from 'react';

const GenerateAttendance = () => {
  // Mock data for UI
  const fakeReport = [
    { date: '2025-05-20', status: 'Present' },
    { date: '2025-05-20', status: 'Leave' },
    { date: '2025-05-21', status: 'Leave' },
    { date: '2025-05-23', status: 'Leave' }
  ];

  return (
    <div style={{ padding: '20px' }}>
      <h3 style={{ color: 'var(--green-900)', marginBottom: '20px' }}>Daily Attendance Report</h3>
      
      <div style={{ display: 'flex', flexDirection: 'column', gap: '15px', marginBottom: '20px' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          <label style={{ width: '80px', color: 'var(--text-primary)' }}>Employee:</label>
          <select className="input-field" style={{ width: '250px' }}>
             <option>E011 - Abraham Kebede</option>
          </select>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
           <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
              <label style={{ color: 'var(--text-primary)' }}>From:</label>
              <input type="date" className="input-field" defaultValue="2023-10-03" />
           </div>
           
           <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
              <label style={{ color: 'var(--text-primary)' }}>To:</label>
              <input type="date" className="input-field" defaultValue="2026-04-29" />
           </div>

           <button className="btn-primary" style={{ padding: '8px 20px', borderRadius: '4px' }}>
              Generate Report
           </button>
        </div>
      </div>

      <div className="glass-panel" style={{ padding: '0', overflow: 'hidden' }}>
        <table style={{ width: '100%', textAlign: 'left', borderCollapse: 'collapse', fontSize: '14px' }}>
          <thead>
            <tr style={{ borderBottom: '2px solid var(--green-300)', backgroundColor: 'var(--green-100)', color: 'var(--green-900)' }}>
              <th style={{ padding: '12px' }}>Date</th>
              <th style={{ padding: '12px' }}>Status</th>
            </tr>
          </thead>
          <tbody>
            {fakeReport.map((record, index) => (
              <tr key={index} style={{ borderBottom: '1px solid #eee' }}>
                <td style={{ padding: '12px' }}>{record.date}</td>
                <td style={{ padding: '12px' }}>{record.status}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default GenerateAttendance;
