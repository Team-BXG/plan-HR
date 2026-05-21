import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useAuth } from '../../app/providers/AuthContext';
import { Clock } from 'lucide-react';

const ManagerAttendanceTracking = () => {
  const { user } = useAuth();
  const [attendanceRecords, setAttendanceRecords] = useState([]);

  useEffect(() => {
    fetchAttendance();
  }, [user]);

  const fetchAttendance = async () => {
    try {
      if (!user?.department) return;
      const resp = await axios.get(`http://localhost:8000/api/attendance/?department=${user.department}`);
      setAttendanceRecords(resp.data.results || resp.data || []);
    } catch (e) {
      console.error("Failed to fetch attendance", e);
    }
  };

  return (
    <div style={{ padding: '20px', height: '100%', display: 'flex', flexDirection: 'column' }}>
      <h2 style={{ color: 'var(--green-900)' }}>Attendance Tracking ({user?.department})</h2>
      
      <div className="glass-panel" style={{ flex: 1, padding: '20px', overflowY: 'auto', marginTop: '20px' }}>
        <table style={{ width: '100%', textAlign: 'left', borderCollapse: 'collapse', fontSize: '14px' }}>
          <thead>
            <tr style={{ borderBottom: '2px solid var(--green-400)', color: 'var(--green-900)' }}>
              <th style={{ padding: '10px' }}>Attendance ID</th>
              <th>Employee ID</th>
              <th>Date</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            {attendanceRecords.map((record, idx) => (
              <tr key={idx} style={{ borderBottom: '1px solid #eee' }}>
                <td style={{ padding: '12px 10px' }}>{record.attendance_id}</td>
                <td>{record.employee_id}</td>
                <td>{record.attendance_date}</td>
                <td>
                  <span style={{
                    padding: '3px 8px', borderRadius: '12px', fontSize: '12px',
                    backgroundColor: '#e8f5e9',
                    color: '#2e7d32'
                  }}>
                    Present
                  </span>
                </td>
              </tr>
            ))}
            {attendanceRecords.length === 0 && (
              <tr>
                <td colSpan="4" style={{ textAlign: 'center', padding: '20px', color: '#666' }}>No attendance records found.</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default ManagerAttendanceTracking;
