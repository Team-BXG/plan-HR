import React, { useState } from 'react';

const ReportGeneration = () => {
    return (
        <div className="glass-panel" style={{ padding: '30px', margin: '20px', backgroundColor: 'var(--green-100)', flex: 1 }}>
            <h3 style={{ color: 'var(--green-900)', marginTop: 0, marginBottom: '20px' }}>Report Generation</h3>
            
            <div style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                
                <div style={{ display: 'flex', flexDirection: 'column', gap: '5px' }}>
                    <label style={{ fontSize: '13px', color: 'var(--text-primary)' }}>Report Type:</label>
                    <select className="input-field" style={{ width: '220px' }}>
                        <option>Select Report Type</option>
                        <option>Employee List</option>
                        <option>Department Summary</option>
                        <option>Attendance Summary</option>
                    </select>
                </div>

                <div style={{ marginTop: '10px' }}>
                    <select className="input-field" style={{ width: '180px' }}>
                        <option>All Departments</option>
                    </select>
                </div>

                <div style={{ display: 'flex', gap: '10px', marginTop: '15px' }}>
                    <button className="btn-primary" style={{ padding: '8px 20px', borderRadius: '4px' }}>Generate Report</button>
                    <button style={{ padding: '8px 20px', backgroundColor: 'var(--green-600)', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Export to PDF</button>
                    <button style={{ padding: '8px 20px', backgroundColor: 'var(--green-800)', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Export to Excel</button>
                </div>
            </div>

            <div style={{ marginTop: '30px', border: '1px solid var(--green-300)', minHeight: '200px', backgroundColor: 'white', display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
                <span style={{ color: 'var(--green-600)', fontSize: '14px' }}>No columns in table</span>
            </div>
        </div>
    );
};

export default ReportGeneration;
