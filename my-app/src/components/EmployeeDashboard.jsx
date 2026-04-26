import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';

const EmployeeDashboard = () => {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState('My Attendance');

  // Hardcode current layout to match the screenshot EXACTLY.
  // The screenshot shows: Welcome, Alemnesh Kassahun! Sunday, May 18, 2025
  const todayDateStr = "Sunday, May 18, 2025"; 

  return (
    <div style={{ padding: '0px', height: '100%', position: 'relative' }}>
      
      <div style={{ textAlign: 'center', marginTop: '40px', marginBottom: '20px' }}>
         <h2 style={{ color: '#6a1b9a', fontWeight: 'bold' }}>Welcome, {user?.name || 'Alemnesh Kassahun'}!</h2>
         <p style={{ color: '#666', fontSize: '13px' }}>{todayDateStr}</p>
      </div>

      <div style={{ padding: '0 40px' }}>
          <div style={{ display: 'flex', borderBottom: '2px solid #e0e0e0', backgroundColor: '#e0e0e0', padding: '5px 5px 0 5px' }}>
              <div 
                 onClick={() => setActiveTab('My Attendance')}
                 style={{ 
                    padding: '8px 15px', fontSize: '13px', cursor: 'pointer',
                    backgroundColor: activeTab === 'My Attendance' ? 'white' : 'transparent',
                    borderTopLeftRadius: '5px', borderTopRightRadius: '5px',
                    color: activeTab === 'My Attendance' ? '#6a1b9a' : '#555',
                    border: activeTab === 'My Attendance' ? '1px solid #ccc' : 'none', borderBottom: 'none'
                 }}>
                 My Attendance
              </div>
              <div 
                 onClick={() => setActiveTab('My Leave Records')}
                 style={{ 
                    padding: '8px 15px', fontSize: '13px', cursor: 'pointer',
                    backgroundColor: activeTab === 'My Leave Records' ? 'white' : 'transparent',
                    borderTopLeftRadius: '5px', borderTopRightRadius: '5px',
                    color: activeTab === 'My Leave Records' ? '#6a1b9a' : '#555',
                    border: activeTab === 'My Leave Records' ? '1px solid #ccc' : 'none', borderBottom: 'none'
                 }}>
                 My Leave Records
              </div>
          </div>

          {activeTab === 'My Attendance' && (
              <div style={{ textAlign: 'center', marginTop: '60px' }}>
                 <h4 style={{ color: '#333' }}>Daily Attendance</h4>
                 <button disabled style={{ padding: '10px 40px', backgroundColor: '#c8e6c9', border: 'none', borderRadius: '5px', fontSize: '14px', color: '#388e3c', cursor: 'not-allowed', marginTop: '15px' }}>
                     Punch In Today
                 </button>
                 <p style={{ fontSize: '12px', color: '#6a1b9a', marginTop: '10px' }}>You've already punched in today</p>
              </div>
          )}

          {activeTab === 'My Leave Records' && (
              <div style={{ marginTop: '30px' }}>
                  <div style={{ display: 'flex', justifyContent: 'center', gap: '20px', marginBottom: '30px' }}>
                       <div style={{ backgroundColor: '#fff3e0', padding: '15px 30px', borderRadius: '10px', textAlign: 'center' }}>
                            <div style={{ fontSize: '12px', color: '#555' }}>Leave Days Taken</div>
                            <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#ffb300', marginTop: '5px' }}>2</div>
                       </div>
                       <div style={{ backgroundColor: '#e3f2fd', padding: '15px 30px', borderRadius: '10px', textAlign: 'center' }}>
                            <div style={{ fontSize: '12px', color: '#555' }}>Leave Days Remaining</div>
                            <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#42a5f5', marginTop: '5px' }}>18</div>
                       </div>
                  </div>

                  <h5 style={{ color: '#4a148c', marginBottom: '10px' }}>Recent Leave Records</h5>
                  <table style={{ width: '100%', textAlign: 'left', borderCollapse: 'collapse', fontSize: '13px' }}>
                       <thead>
                           <tr style={{ backgroundColor: '#eeeeee', color: '#333' }}>
                               <th style={{ padding: '8px', borderRight: '1px solid #ddd' }}>Date</th>
                               <th style={{ padding: '8px' }}>Reason</th>
                           </tr>
                       </thead>
                       <tbody>
                           <tr style={{ borderBottom: '1px solid #f0f0f0' }}>
                               <td style={{ padding: '8px', borderRight: '1px solid #ddd' }}>2025-05-18</td>
                               <td style={{ padding: '8px' }}>sick</td>
                           </tr>
                           <tr style={{ borderBottom: '1px solid #f0f0f0' }}>
                               <td style={{ padding: '8px', borderRight: '1px solid #ddd' }}>2025-05-17</td>
                               <td style={{ padding: '8px' }}>sick</td>
                           </tr>
                       </tbody>
                   </table>
              </div>
          )}
      </div>

    </div>
  );
};
export default EmployeeDashboard;
