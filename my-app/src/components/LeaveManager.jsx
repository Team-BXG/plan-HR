import React from 'react';
import { useAuth } from '../context/AuthContext';

const LeaveManager = () => {
    return (
        <div className="glass-panel" style={{ padding: '30px', margin: '20px', backgroundColor: '#fafafa', flex: 1 }}>
            <h4 style={{ color: '#4a148c', marginTop: 0, marginBottom: '20px', fontWeight: 'normal' }}>Register Leave Period</h4>
            
            <div style={{ display: 'flex', flexDirection: 'column', gap: '15px', maxWidth: '600px' }}>
                <div style={{ display: 'flex', flexDirection: 'column' }}>
                    <label style={{ fontSize: '13px', color: '#666', marginBottom: '5px' }}>Employee:</label>
                    <select className="glass-input" style={{ width: '200px' }}>
                        <option>E001 - Abebe Kebede</option>
                        <option>E004 - Alemnesh Kassahun</option>
                    </select>
                </div>

                <div style={{ display: 'flex', flexDirection: 'column' }}>
                    <label style={{ fontSize: '13px', color: '#666', marginBottom: '5px' }}>From:</label>
                    <div style={{ display: 'flex' }}>
                       <input className="glass-input" type="date" defaultValue="2026-04-26" style={{ width: '160px', borderRadius: '4px 0 0 4px' }} />
                       <span style={{ backgroundColor: '#e0e0e0', padding: '8px 10px', border: '1px solid #ccc', borderLeft: 'none', borderRadius: '0 4px 4px 0' }}>📅</span>
                    </div>
                </div>

                <div style={{ display: 'flex', flexDirection: 'column' }}>
                    <label style={{ fontSize: '13px', color: '#666', marginBottom: '5px' }}>To:</label>
                    <div style={{ display: 'flex' }}>
                       <input className="glass-input" type="date" defaultValue="2026-04-27" style={{ width: '160px', borderRadius: '4px 0 0 4px' }} />
                       <span style={{ backgroundColor: '#e0e0e0', padding: '8px 10px', border: '1px solid #ccc', borderLeft: 'none', borderRadius: '0 4px 4px 0' }}>📅</span>
                    </div>
                </div>

                <div style={{ display: 'flex', flexDirection: 'column' }}>
                    <label style={{ fontSize: '13px', color: '#666', marginBottom: '5px' }}>Reason:</label>
                    <textarea 
                       className="glass-input" 
                       placeholder="Leave reason..."
                       style={{ height: '120px', width: '100%', resize: 'none' }}
                    ></textarea>
                </div>

                <button style={{ 
                    padding: '8px 20px', 
                    backgroundColor: '#6a1b9a', 
                    color: 'white', 
                    border: 'none', 
                    borderRadius: '4px',
                    width: 'fit-content',
                    cursor: 'pointer',
                    marginTop: '10px'
                }}>
                    Register Leave
                </button>
            </div>
        </div>
    );
};

export default LeaveManager;
