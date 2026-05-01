import React, { useState } from 'react';

const LeaveManager = () => {
    const [showNoRecords, setShowNoRecords] = useState(false);
    const isGenerate = window.location.pathname.includes('/leave/generate');

    const employees = [
        "E001 - Abebe Kebede",
        "E011 - Abraham Kebede",
        "E004 - Alemnesh Kassahune",
        "E009 - beza dememe",
        "E005 - Dawit Solomon",
        "E008 - Deborah Habtu",
        "E014 - Genet Tesafye",
        "E007 - Hermela Belay",
        "E0019 - kebe Abuch",
        "E013 - Mahilet Tesfaye",
        "E003 - Mekonnen Alemu",
        "E006 - Tsion Habtu",
        "E002 - Tigist Worku",
        "E012 - Bemnet Worku",
        "E0021 - yene deseee",
        "E0020 - yuiiiiiii iiiiiiiiii"
    ];

    return (
        <div style={{ padding: '20px', height: '100%', position: 'relative' }}>
            
            {isGenerate ? (
                <div style={{ maxWidth: '900px' }}>
                    <div style={{ marginBottom: '20px' }}>
                        <span style={{ fontSize: '14px', color: 'var(--text-primary)' }}>Leave Records</span>
                    </div>

                    <div style={{ marginBottom: '20px', display: 'flex', flexDirection: 'column', gap: '5px' }}>
                        <label style={{ fontSize: '13px', color: 'var(--text-primary)' }}>Employee:</label>
                        <select className="input-field" style={{ width: '250px', background: '#dcecdb' }}>
                            {employees.map((emp, i) => (
                                <option key={i} value={emp} style={{ padding: '4px' }}>{emp}</option>
                            ))}
                        </select>
                    </div>

                    <div style={{ display: 'flex', alignItems: 'center', gap: '15px', marginBottom: '20px' }}>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '5px' }}>
                            <span style={{ fontSize: '13px', color: 'var(--text-primary)' }}>From:</span>
                            <input type="date" defaultValue="2026-03-12" className="input-field" style={{ padding: '4px 8px', width: '130px' }} />
                        </div>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '5px' }}>
                            <span style={{ fontSize: '13px', color: 'var(--text-primary)' }}>To:</span>
                            <input type="date" defaultValue="2026-04-09" className="input-field" style={{ padding: '4px 8px', width: '130px' }} />
                        </div>
                        <button className="btn-primary" onClick={() => setShowNoRecords(true)} style={{ padding: '6px 15px', borderRadius: '4px' }}>Generate Leave Report</button>
                    </div>

                    <div className="glass-panel" style={{ padding: '0', overflow: 'hidden' }}>
                        <table style={{ width: '100%', textAlign: 'center', borderCollapse: 'collapse', fontSize: '13px' }}>
                            <thead>
                                <tr style={{ backgroundColor: '#e0e0e0', color: '#555' }}>
                                    <th style={{ padding: '10px' }}>Date</th>
                                    <th style={{ padding: '10px' }}>Reason</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr>
                                    <td colSpan="2" style={{ padding: '40px', color: '#888' }}>No content in table</td>
                                </tr>
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
                            <select className="input-field" style={{ width: '250px' }}>
                                {employees.map((emp, i) => (
                                    <option key={i} value={emp}>{emp}</option>
                                ))}
                            </select>
                        </div>
                        <div style={{ display: 'flex', flexDirection: 'column' }}>
                            <label style={{ fontSize: '13px', color: 'var(--text-primary)', marginBottom: '5px' }}>From:</label>
                            <input className="input-field" type="date" defaultValue="2026-04-26" style={{ width: '160px' }} />
                        </div>
                        <div style={{ display: 'flex', flexDirection: 'column' }}>
                            <label style={{ fontSize: '13px', color: 'var(--text-primary)', marginBottom: '5px' }}>To:</label>
                            <input className="input-field" type="date" defaultValue="2026-04-27" style={{ width: '160px' }} />
                        </div>
                        <div style={{ display: 'flex', flexDirection: 'column' }}>
                            <label style={{ fontSize: '13px', color: 'var(--text-primary)', marginBottom: '5px' }}>Reason:</label>
                            <textarea className="input-field" placeholder="Leave reason..." style={{ height: '120px', width: '100%', resize: 'none' }}></textarea>
                        </div>
                        <button className="btn-primary" style={{ padding: '8px 20px', width: 'fit-content', borderRadius: '4px' }}>Register Leave</button>
                    </div>
                </div>
            )}
            
        </div>
    );
};

export default LeaveManager;
