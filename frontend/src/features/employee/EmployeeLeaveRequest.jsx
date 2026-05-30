import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useAuth } from '../../app/providers/AuthContext';
import { Calendar, FileText, Send, Clock, CheckCircle, XCircle } from 'lucide-react';

const EmployeeLeaveRequest = () => {
    const { user } = useAuth();
    const [leaveRequests, setLeaveRequests] = useState([]);
    const [formData, setFormData] = useState({
        start_date: '',
        end_date: '',
        reason: ''
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(null);

    const empId = user?.employee_id || (user?.name === 'admin' ? 'E001' : 'E011');

    useEffect(() => {
        fetchLeaveRequests();
    }, [empId]);

    const fetchLeaveRequests = async () => {
        try {
            const resp = await axios.get(`http://localhost:8000/api/leave/records/?employee=${empId}`);
            setLeaveRequests(resp.data);
        } catch (e) {
            console.error("Failed to fetch leave requests", e);
        }
    };

    const handleInputChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError(null);
        setSuccess(null);

        try {
            await axios.post('http://localhost:8000/api/leave/records/apply/', {
                employee: empId,
                start_date: formData.start_date,
                end_date: formData.end_date,
                reason: formData.reason
            });
            setSuccess("Leave request submitted successfully!");
            setFormData({ start_date: '', end_date: '', reason: '' });
            fetchLeaveRequests();
        } catch (err) {
            setError(err.response?.data?.message || err.response?.data?.error || "Failed to submit request.");
        } finally {
            setLoading(false);
        }
    };

    const getStatusBadge = (status) => {
        switch (status) {
            case 'approved':
                return (
                    <span style={{ padding: '4px 10px', borderRadius: '12px', fontSize: '12px', backgroundColor: '#e8f5e9', color: '#2e7d32', display: 'inline-flex', alignItems: 'center', gap: '4px', textTransform: 'capitalize' }}>
                        <CheckCircle size={12} /> {status}
                    </span>
                );
            case 'rejected':
                return (
                    <span style={{ padding: '4px 10px', borderRadius: '12px', fontSize: '12px', backgroundColor: '#ffebee', color: '#c62828', display: 'inline-flex', alignItems: 'center', gap: '4px', textTransform: 'capitalize' }}>
                        <XCircle size={12} /> {status}
                    </span>
                );
            default:
                return (
                    <span style={{ padding: '4px 10px', borderRadius: '12px', fontSize: '12px', backgroundColor: '#fff3e0', color: '#ef6c00', display: 'inline-flex', alignItems: 'center', gap: '4px', textTransform: 'capitalize' }}>
                        <Clock size={12} /> {status}
                    </span>
                );
        }
    };

    return (
        <div style={{ padding: '30px', height: '100%', display: 'flex', flexDirection: 'column', gap: '30px', backgroundColor: 'var(--green-100)', overflowY: 'auto' }}>
            
            <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                <h2 style={{ color: 'var(--green-900)', margin: 0, fontSize: '24px' }}>My Leave Requests</h2>
                <p style={{ color: 'var(--text-primary)', margin: 0, fontSize: '14px' }}>Submit new leave requests and track your previous applications.</p>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 2fr', gap: '20px' }}>
                
                {/* Apply Form */}
                <div className="glass-panel" style={{ padding: '25px', display: 'flex', flexDirection: 'column', gap: '20px', height: 'fit-content' }}>
                    <h3 style={{ margin: 0, color: 'var(--green-800)', borderBottom: '1px solid var(--green-300)', paddingBottom: '10px', fontSize: '18px' }}>Apply for Leave</h3>
                    
                    {error && <div style={{ padding: '10px', backgroundColor: '#ffebee', color: '#c62828', borderRadius: '6px', fontSize: '13px' }}>{error}</div>}
                    {success && <div style={{ padding: '10px', backgroundColor: '#e8f5e9', color: '#2e7d32', borderRadius: '6px', fontSize: '13px' }}>{success}</div>}

                    <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                        <div style={{ display: 'flex', flexDirection: 'column', gap: '5px' }}>
                            <label style={{ fontSize: '13px', color: 'var(--text-primary)', display: 'flex', alignItems: 'center', gap: '5px' }}><Calendar size={14}/> Start Date</label>
                            <input 
                                type="date" 
                                name="start_date"
                                className="input-field" 
                                value={formData.start_date}
                                onChange={handleInputChange}
                                required 
                            />
                        </div>
                        <div style={{ display: 'flex', flexDirection: 'column', gap: '5px' }}>
                            <label style={{ fontSize: '13px', color: 'var(--text-primary)', display: 'flex', alignItems: 'center', gap: '5px' }}><Calendar size={14}/> End Date</label>
                            <input 
                                type="date" 
                                name="end_date"
                                className="input-field" 
                                value={formData.end_date}
                                onChange={handleInputChange}
                                required 
                            />
                        </div>
                        <div style={{ display: 'flex', flexDirection: 'column', gap: '5px' }}>
                            <label style={{ fontSize: '13px', color: 'var(--text-primary)', display: 'flex', alignItems: 'center', gap: '5px' }}><FileText size={14}/> Reason</label>
                            <textarea 
                                name="reason"
                                className="input-field" 
                                rows="3"
                                value={formData.reason}
                                onChange={handleInputChange}
                                placeholder="Explain why you are requesting leave..."
                                required 
                                style={{ resize: 'vertical' }}
                            ></textarea>
                        </div>
                        <button type="submit" className="btn-primary" disabled={loading} style={{ marginTop: '10px', display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '8px' }}>
                            {loading ? 'Submitting...' : <><Send size={16}/> Submit Request</>}
                        </button>
                    </form>
                </div>

                {/* History Table */}
                <div className="glass-panel" style={{ padding: '25px', display: 'flex', flexDirection: 'column' }}>
                    <h3 style={{ margin: 0, color: 'var(--green-800)', borderBottom: '1px solid var(--green-300)', paddingBottom: '10px', fontSize: '18px', marginBottom: '20px' }}>Request History</h3>
                    
                    <div style={{ flex: 1, overflowY: 'auto' }}>
                        <table style={{ width: '100%', textAlign: 'left', borderCollapse: 'collapse', fontSize: '14px' }}>
                            <thead>
                                <tr style={{ borderBottom: '2px solid var(--green-400)', color: 'var(--green-900)' }}>
                                    <th style={{ padding: '12px 10px' }}>Date Range</th>
                                    <th style={{ padding: '12px 10px' }}>Reason</th>
                                    <th style={{ padding: '12px 10px' }}>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                {leaveRequests.map((req, idx) => (
                                    <tr key={idx} style={{ borderBottom: '1px solid #eee' }}>
                                        <td style={{ padding: '12px 10px', color: 'var(--text-primary)' }}>{req.start_date} to {req.end_date}</td>
                                        <td style={{ padding: '12px 10px', color: '#666' }}>{req.reason}</td>
                                        <td style={{ padding: '12px 10px' }}>{getStatusBadge(req.status)}</td>
                                    </tr>
                                ))}
                                {leaveRequests.length === 0 && (
                                    <tr>
                                        <td colSpan="3" style={{ textAlign: 'center', padding: '30px', color: '#888' }}>
                                            <FileText size={32} style={{ opacity: 0.5, marginBottom: '10px' }} /><br />
                                            No leave requests found.
                                        </td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    </div>
                </div>

            </div>
        </div>
    );
};

export default EmployeeLeaveRequest;
