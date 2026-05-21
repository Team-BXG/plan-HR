import React, { useState } from 'react';
import axios from 'axios';

const LeaveManager = () => {
    const [showNoRecords, setShowNoRecords] = useState(false);
    const isGenerate = window.location.pathname.includes('/leave/generate');

    const [employees, setEmployees] = useState([]);
    const [activeEmployee, setActiveEmployee] = useState('');

    React.useEffect(() => {
        const fetchEmployees = async () => {
            try {
                const resp = await axios.get('http://localhost:8000/api/employees/');
                const emps = resp.data.results || resp.data;
                setEmployees(emps);
                if (emps.length > 0) setActiveEmployee(emps[0].id || emps[0].employee_id);
            } catch (e) {
                console.error("Failed to fetch employees", e);
            }
        };
        fetchEmployees();
    }, []);
    const [fromDate, setFromDate] = useState(new Date().toISOString().split('T')[0]);
    const [toDate, setToDate] = useState(new Date().toISOString().split('T')[0]);
    const [reason, setReason] = useState('');
    const [leaveRecords, setLeaveRecords] = useState([]);

    const handleRegisterLeave = async () => {
        if (!reason || !activeEmployee) return alert('Fill out all fields');
        try {
            await axios.post('http://localhost:8000/api/leave/records/apply/', {
                employee_id: activeEmployee,
                start_date: fromDate,
                end_date: toDate,
                reason: reason
            });
            alert('Leave registered successfully!');
            setReason('');
        } catch (e) {
            // Note: If they are already on leave, tell them that at the bottom text.
            const errMsg = e.response?.data?.message || e.response?.data?.error || e.message;
            alert('Failed to register leave: ' + errMsg);
        }
    };

    const handleGenerateLeave = async () => {
        try {
            const resp = await axios.get(`http://localhost:8000/api/leave/records/?employee=${activeEmployee}&start_date=${fromDate}&end_date=${toDate}`);
            const data = resp.data.results || resp.data;
            if (data.length > 0) {
                setLeaveRecords(data);
                setShowNoRecords(false);
            } else {
                setLeaveRecords([]);
                setShowNoRecords(true);
            }
        } catch (e) {
            setLeaveRecords([]);
            setShowNoRecords(true);
        }
    };

    return (
        <div style={{ padding: '20px', height: '100%', position: 'relative' }}>
            
            {isGenerate ? (
                <div style={{ maxWidth: '900px' }}>
                    <div style={{ marginBottom: '20px' }}>
                        <span style={{ fontSize: '14px', color: 'var(--text-primary)' }}>Leave Records</span>
                    </div>

                    <div style={{ marginBottom: '20px', display: 'flex', flexDirection: 'column', gap: '5px' }}>
                        <label style={{ fontSize: '13px', color: 'var(--text-primary)' }}>Employee:</label>
                        <select className="input-field" value={activeEmployee} onChange={(e) => setActiveEmployee(e.target.value)} style={{ width: '250px', background: '#dcecdb' }}>
                            {employees.map((emp) => {
                                const empId = emp.id || emp.employee_id;
                                return <option key={empId} value={empId} style={{ padding: '4px' }}>{empId} - {emp.name}</option>;
                            })}
                        </select>
                    </div>

                    <div style={{ display: 'flex', alignItems: 'center', gap: '15px', marginBottom: '20px' }}>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '5px' }}>
                            <span style={{ fontSize: '13px', color: 'var(--text-primary)' }}>From:</span>
                            <input type="date" value={fromDate} onChange={(e) => setFromDate(e.target.value)} className="input-field" style={{ padding: '4px 8px', width: '130px' }} />
                        </div>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '5px' }}>
                            <span style={{ fontSize: '13px', color: 'var(--text-primary)' }}>To:</span>
                            <input type="date" value={toDate} onChange={(e) => setToDate(e.target.value)} className="input-field" style={{ padding: '4px 8px', width: '130px' }} />
                        </div>
                        <button className="btn-primary" onClick={handleGenerateLeave} style={{ padding: '6px 15px', borderRadius: '4px' }}>Generate Leave Report</button>
                    </div>

                    <div className="glass-panel" style={{ padding: '0', overflow: 'hidden' }}>
                        <table style={{ width: '100%', textAlign: 'center', borderCollapse: 'collapse', fontSize: '13px' }}>
                            <thead>
                                <tr style={{ backgroundColor: '#e0e0e0', color: '#555' }}>
                                    <th style={{ padding: '10px' }}>Start Date</th>
                                    <th style={{ padding: '10px' }}>End Date</th>
                                    <th style={{ padding: '10px' }}>Reason</th>
                                </tr>
                            </thead>
                            <tbody>
                                {leaveRecords.length > 0 ? leaveRecords.map((l, i) => (
                                    <tr key={i} style={{ borderBottom: '1px solid #eee' }}>
                                        <td style={{ padding: '10px' }}>{l.start_date}</td>
                                        <td>{l.end_date}</td>
                                        <td>{l.reason}</td>
                                    </tr>
                                )) : (
                                    <tr>
                                        <td colSpan="3" style={{ padding: '40px', color: '#888' }}>No content in table</td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    </div>

            {showNoRecords && (
                <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, backgroundColor: 'rgba(255,255,255,0.6)', backdropFilter: 'blur(3px)', display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 1000 }}>
                    <div style={{ width: '400px', backgroundColor: '#f0f4f8', borderRadius: '8px', padding: '15px', boxShadow: '0 4px 15px rgba(0,0,0,0.2)', border: '1px solid var(--green-400)' }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid white', paddingBottom: '10px' }}>
                            <h4 style={{ margin: 0, color: 'var(--green-800)', display: 'flex', alignItems: 'center', gap:'10px' }}>
                                <span style={{color: 'green'}}>📘</span> Information
                            </h4>
                            <div style={{ cursor: 'pointer' }} onClick={() => setShowNoRecords(false)}>✕</div>
                        </div>
                        <div style={{ margin: '20px 0', fontSize: '14px', color: 'var(--text-primary)' }}>
                            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                                <span>No Records</span>
                                <div style={{ background: '#2196f3', color: 'white', borderRadius: '50%', width: '30px', height: '30px', display: 'flex', justifyContent: 'center', alignItems: 'center', fontWeight: 'bold' }}>i</div>
                            </div>
                            No leave records found for the selected period.
                        </div>
                        <div style={{ display: 'flex', justifyContent: 'center', marginTop: '20px', gap: '10px' }}>
                            <button onClick={() => setShowNoRecords(false)} style={{ padding: '5px 30px', borderRadius: '4px', border: '1px solid #5abed6', background: '#5abed6', color: 'white', cursor: 'pointer' }}>OK</button>
                        </div>
                    </div>
                </div>
            )}
                </div>
            ) : (
                <div className="glass-panel" style={{ padding: '30px', maxWidth: '600px' }}>
                    <h4 style={{ color: 'var(--green-900)', marginTop: 0, marginBottom: '20px', fontWeight: 'normal' }}>Register Leave Period</h4>
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                        <div style={{ display: 'flex', flexDirection: 'column' }}>
                            <label style={{ fontSize: '13px', color: 'var(--text-primary)', marginBottom: '5px' }}>Employee:</label>
                            <select className="input-field" value={activeEmployee} onChange={(e) => setActiveEmployee(e.target.value)} style={{ width: '250px' }}>
                                {employees.map((emp) => {
                                    const empId = emp.id || emp.employee_id;
                                    return <option key={empId} value={empId}>{empId} - {emp.name}</option>;
                                })}
                            </select>
                        </div>
                        <div style={{ display: 'flex', flexDirection: 'column' }}>
                            <label style={{ fontSize: '13px', color: 'var(--text-primary)', marginBottom: '5px' }}>From:</label>
                            <input className="input-field" type="date" value={fromDate} onChange={(e) => setFromDate(e.target.value)} style={{ width: '160px' }} />
                        </div>
                        <div style={{ display: 'flex', flexDirection: 'column' }}>
                            <label style={{ fontSize: '13px', color: 'var(--text-primary)', marginBottom: '5px' }}>To:</label>
                            <input className="input-field" type="date" value={toDate} onChange={(e) => setToDate(e.target.value)} style={{ width: '160px' }} />
                        </div>
                        <div style={{ display: 'flex', flexDirection: 'column' }}>
                            <label style={{ fontSize: '13px', color: 'var(--text-primary)', marginBottom: '5px' }}>Reason:</label>
                            <textarea className="input-field" value={reason} onChange={(e) => setReason(e.target.value)} placeholder="Leave reason..." style={{ height: '120px', width: '100%', resize: 'none' }}></textarea>
                        </div>
                        <button className="btn-primary" onClick={handleRegisterLeave} style={{ padding: '8px 20px', width: 'fit-content', borderRadius: '4px' }}>Register Leave</button>
                    </div>
                </div>
            )}
            
        </div>
    );
};

export default LeaveManager;
