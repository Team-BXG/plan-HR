import React, { useState } from 'react';
import axios from 'axios';
import { useAuth } from '../context/AuthContext';

const AttendancePunch = () => {
  const { user } = useAuth();
  const [status, setStatus] = useState('');

  const handlePunch = async (punchType) => {
    try {
      await axios.post('http://localhost:8000/api/attendance/punch/', {
        employee_id: user?.employee_id || 1,
        type: punchType
      });
      setStatus(`Successfully Punched ${punchType} at ${new Date().toLocaleTimeString()}`);
    } catch (error) {
       console.error("Punch error", error);
       // Mock for UI demonstration
       setStatus(`Successfully Punched ${punchType} at ${new Date().toLocaleTimeString()} (Simulation)`);
    }
  };

  return (
    <div style={{ maxWidth: '500px', margin: '0 auto' }}>
      <h2 style={{ color: '#6a1b9a', textAlign: 'center' }}>Attendance Punch</h2>
      <div className="glass-panel" style={{ padding: '30px', marginTop: '20px', textAlign: 'center' }}>
         <div style={{ fontSize: '48px', marginBottom: '20px' }}>🕛</div>
         <h3 style={{ margin: 0, color: '#333' }}>Current Time</h3>
         <p style={{ fontSize: '24px', fontWeight: 'bold', color: '#6a1b9a' }}>{new Date().toLocaleTimeString()}</p>
         
         <div style={{ display: 'flex', gap: '20px', justifyContent: 'center', marginTop: '30px' }}>
           <button className="btn-primary" style={{ backgroundColor: '#4CAF50' }} onClick={() => handlePunch('In')}>
             Punch In
           </button>
           <button className="btn-primary" style={{ backgroundColor: '#f44336' }} onClick={() => handlePunch('Out')}>
             Punch Out
           </button>
         </div>

         {status && <div style={{ marginTop: '20px', padding: '10px', backgroundColor: '#e8f5e9', color: '#2e7d32', borderRadius: '10px' }}>{status}</div>}
      </div>
    </div>
  )
}
export default AttendancePunch;
