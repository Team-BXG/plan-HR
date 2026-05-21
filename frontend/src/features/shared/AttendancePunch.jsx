import React, { useState } from 'react';
import axios from 'axios';
import { useAuth } from '../../app/providers/AuthContext';

const AttendancePunch = () => {
  const { user } = useAuth();
  const [status, setStatus] = useState(null);

  const handlePunch = async (punchType) => {
    try {
      const empId = user?.employee_id || (user?.name === 'admin' ? 'E001' : 'E011');
      const resp = await axios.post('http://localhost:8000/api/attendance/punch/', {
        employee_id: empId,
        type: punchType
      });
      setStatus({ text: resp.data.message || `Successfully Punched ${punchType} at ${new Date().toLocaleTimeString()}`, type: 'success' });
    } catch (error) {
       console.error("Punch error", error);
       if (error.response?.data) {
          const errData = error.response.data;
          if (errData.status === 'already_punched') {
              setStatus({ text: errData.message, type: 'error' });
          } else if (errData.status === 'leave') {
              setStatus({ text: errData.message, type: 'warning' });
          } else {
              setStatus({ text: errData.error || errData.message || "Failed to punch", type: 'error' });
          }
       } else {
          setStatus({ text: `Successfully Punched ${punchType} at ${new Date().toLocaleTimeString()} (Simulation)`, type: 'success' });
       }
    }
  };

  const getStatusStyle = () => {
      if (!status) return {};
      if (status.type === 'success') return { backgroundColor: '#e8f5e9', color: '#2e7d32' };
      if (status.type === 'error') return { backgroundColor: '#ffebee', color: '#c62828' };
      if (status.type === 'warning') return { backgroundColor: '#fff3e0', color: '#ef6c00' };
      return { backgroundColor: '#e8f5e9', color: '#2e7d32' };
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

         {status && <div style={{ marginTop: '20px', padding: '10px', borderRadius: '10px', ...getStatusStyle() }}>{status.text}</div>}
      </div>
    </div>
  )
}
export default AttendancePunch;
