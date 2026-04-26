import React, { useState } from 'react';

const ReportGeneration = () => {
    return (
        <div className="glass-panel" style={{ padding: '30px', margin: '20px', backgroundColor: '#fafafa', flex: 1 }}>
            <h3 style={{ color: '#4a148c', marginTop: 0, marginBottom: '20px' }}>Report Generation</h3>
            
            <div style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                
                <div style={{ display: 'flex', flexDirection: 'column', gap: '5px' }}>
                    <label style={{ fontSize: '13px', color: '#666' }}>Report Type:</label>
                    <select className="glass-input" style={{ width: '220px', backgroundColor: '#e1f5fe' }}>
                        <option>Select Report Type</option>
                        <option>Employee List</option>
                        <option>Department Summary</option>
                        <option>Attendance Summary</option>
                    </select>
                </div>

                <div style={{ marginTop: '10px' }}>
                    <select className="glass-input" style={{ width: '180px', backgroundColor: '#f5f5f5' }}>
                        <option>All Departments</option>
                    </select>
                </div>

                <div style={{ display: 'flex', gap: '10px', marginTop: '15px' }}>
                    <button style={{ padding: '8px 20px', backgroundColor: '#6a1b9a', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Generate Report</button>
                    <button style={{ padding: '8px 20px', backgroundColor: '#4caf50', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Export to PDF</button>
                    <button style={{ padding: '8px 20px', backgroundColor: '#2196f3', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Export to Excel</button>
                </div>
            </div>

            <div style={{ marginTop: '30px', border: '1px solid #ccc', minHeight: '200px', backgroundColor: 'white', display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
                <span style={{ color: '#999', fontSize: '14px' }}>No columns in table</span>
            </div>
        </div>
    );
};

export default ReportGeneration;
