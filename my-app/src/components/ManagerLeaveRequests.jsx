import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useAuth } from '../context/AuthContext';
import { CheckCircle, XCircle } from 'lucide-react';

const ManagerLeaveRequests = () => {
  const { user } = useAuth();
  const [leaveRequests, setLeaveRequests] = useState([]);

  useEffect(() => {
    fetchLeaveRequests();
  }, [user]);

  const fetchLeaveRequests = async () => {
    try {
      if (!user?.department) return;
      const resp = await axios.get(`http://localhost:8000/api/leave/records/?department=${user.department}`);
      setLeaveRequests(resp.data);
    } catch (e) {
      console.error("Failed to fetch leave requests", e);
    }
  };

  const handleUpdateStatus = async (id, newStatus) => {
    try {
      await axios.patch(`http://localhost:8000/api/leave/records/${id}/update_status/`, { status: newStatus });
      fetchLeaveRequests(); // Refresh data
    } catch (e) {
      console.error("Failed to update status", e);
      alert("Failed to update status");
    }
  };

  return (
    <div style={{ padding: '20px', height: '100%', display: 'flex', flexDirection: 'column' }}>
      <h2 style={{ color: 'var(--green-900)' }}>Leave Requests ({user?.department})</h2>
      
      <div className="glass-panel" style={{ flex: 1, padding: '20px', overflowY: 'auto', marginTop: '20px' }}>
        <table style={{ width: '100%', textAlign: 'left', borderCollapse: 'collapse', fontSize: '14px' }}>
          <thead>
            <tr style={{ borderBottom: '2px solid var(--green-400)', color: 'var(--green-900)' }}>
              <th style={{ padding: '10px' }}>Employee</th>
              <th>Date Range</th>
              <th>Reason</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {leaveRequests.map((req, idx) => (
              <tr key={idx} style={{ borderBottom: '1px solid #eee' }}>
                <td style={{ padding: '12px 10px' }}>{req.employee_name} ({req.employee})</td>
                <td>{req.start_date} to {req.end_date}</td>
                <td>{req.reason}</td>
                <td>
                  <span style={{
                    padding: '3px 8px', borderRadius: '12px', fontSize: '12px', textTransform: 'capitalize',
                    backgroundColor: req.status === 'approved' ? '#e8f5e9' : req.status === 'rejected' ? '#ffebee' : '#fff3e0',
                    color: req.status === 'approved' ? '#2e7d32' : req.status === 'rejected' ? '#c62828' : '#ef6c00'
                  }}>
                    {req.status}
                  </span>
                </td>
                <td>
                  {req.status === 'pending' && (
                    <div style={{ display: 'flex', gap: '10px' }}>
                      <button onClick={() => handleUpdateStatus(req.id, 'approved')} style={{ background: 'transparent', border: 'none', cursor: 'pointer', color: '#2e7d32' }} title="Approve">
                        <CheckCircle size={20} />
                      </button>
                      <button onClick={() => handleUpdateStatus(req.id, 'rejected')} style={{ background: 'transparent', border: 'none', cursor: 'pointer', color: '#c62828' }} title="Reject">
                        <XCircle size={20} />
                      </button>
                    </div>
                  )}
                </td>
              </tr>
            ))}
            {leaveRequests.length === 0 && (
              <tr>
                <td colSpan="5" style={{ textAlign: 'center', padding: '20px', color: '#666' }}>No leave requests found.</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default ManagerLeaveRequests;
