import React, { useState, useEffect } from 'react';
import axios from 'axios';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

const ReportGeneration = () => {
    const [departments, setDepartments] = useState([]);
    const [reportType, setReportType] = useState('Employee List');
    const [selectedDepartment, setSelectedDepartment] = useState('All');
    const [reportData, setReportData] = useState([]);
    const [columns, setColumns] = useState([]);

    useEffect(() => {
        fetchDepartments();
    }, []);

    const fetchDepartments = async () => {
        try {
            const resp = await axios.get('http://localhost:8000/api/departments/');
            const data = resp.data.results || resp.data || [];
            setDepartments(data);
        } catch (e) {
            console.error("Failed to load departments", e);
        }
    };

    const generateReport = async () => {
        setReportData([]);
        setColumns([]);
        try {
            if (reportType === 'Employee List') {
                const resp = await axios.get(`http://localhost:8000/api/employees/`);
                let emps = resp.data.results || resp.data || [];
                if (selectedDepartment !== 'All') {
                    emps = emps.filter(e => e.department === selectedDepartment);
                }
                setColumns(['ID', 'Name', 'Department', 'Position', 'Phone', 'Join Date']);
                setReportData(emps.map(e => [e.id || e.employee_id, e.name, e.department, e.position, e.phone_number, e.join_date]));
            } else if (reportType === 'Department Summary') {
                const resp = await axios.get('http://localhost:8000/api/departments/');
                let depts = resp.data.results || resp.data || [];
                if (selectedDepartment !== 'All') {
                    depts = depts.filter(d => d.department_name === selectedDepartment);
                }
                setColumns(['ID', 'Department Name', 'Employee Count']);
                setReportData(depts.map(d => [d.department_id, d.department_name, d.employee_count]));
            } else if (reportType === 'Leave Summary') {
                const resp = await axios.get('http://localhost:8000/api/leave/records/');
                const records = resp.data.results || resp.data || [];
                setColumns(['Employee ID', 'Start Date', 'End Date', 'Reason', 'Status']);
                setReportData(records.map(r => [r.employee, r.start_date, r.end_date, r.reason, r.status]));
            }
        } catch (err) {
            alert("Failed to generate report: " + err.message);
        }
    };

    const exportToCSV = () => {
        if (reportData.length === 0) return alert('No data to export');
        const csvContent = [
            columns.join(','),
            ...reportData.map(row => row.map(cell => `"${cell || ''}"`).join(','))
        ].join('\n');

        const blob = new Blob([csvContent], { type: 'text/csv' });
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `${reportType.replace(' ', '_')}.csv`;
        a.click();
    };

    const exportToPDF = () => {
        if (reportData.length === 0) return alert('No data to export');
        const doc = new jsPDF();
        doc.text(`${reportType}`, 14, 15);
        autoTable(doc, {
            head: [columns],
            body: reportData,
            startY: 20,
            theme: 'striped'
        });
        doc.save(`${reportType.replace(' ', '_')}.pdf`);
    };

    return (
        <div className="glass-panel" style={{ padding: '30px', margin: '20px', backgroundColor: 'var(--green-100)', flex: 1 }}>
            <style>
                {`
                    @media print {
                        .no-print { display: none !important; }
                        .print-area { border: none !important; margin: 0 !important; padding: 0 !important; }
                        .print-only { display: block !important; }
                        body { background: white !important; }
                        .glass-panel { background: white !important; box-shadow: none !important; border: none !important; }
                    }
                `}
            </style>
            <h3 style={{ color: 'var(--green-900)', marginTop: 0, marginBottom: '20px' }} className="no-print">Report Generation</h3>
            
            <div style={{ display: 'flex', flexDirection: 'column', gap: '15px' }} className="no-print">
                
                <div style={{ display: 'flex', flexDirection: 'column', gap: '5px' }}>
                    <label style={{ fontSize: '13px', color: 'var(--text-primary)' }}>Report Type:</label>
                    <select className="input-field" style={{ width: '220px' }} value={reportType} onChange={e => setReportType(e.target.value)}>
                        <option>Employee List</option>
                        <option>Department Summary</option>
                        <option>Leave Summary</option>
                    </select>
                </div>

                <div style={{ marginTop: '10px' }}>
                    <label style={{ fontSize: '13px', color: 'var(--text-primary)', display: 'block', marginBottom: '5px' }}>Department Filter:</label>
                    <select className="input-field" style={{ width: '180px' }} value={selectedDepartment} onChange={e => setSelectedDepartment(e.target.value)}>
                        <option value="All">All Departments</option>
                        {departments.map((d, i) => (
                            <option key={i} value={d.department_name}>{d.department_name}</option>
                        ))}
                    </select>
                </div>

                <div style={{ display: 'flex', gap: '10px', marginTop: '15px' }}>
                    <button className="btn-primary" onClick={generateReport} style={{ padding: '8px 20px', borderRadius: '4px' }}>Generate Report</button>
                    <button onClick={exportToPDF} style={{ padding: '8px 20px', backgroundColor: 'var(--green-600)', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Export to PDF</button>
                    <button onClick={exportToCSV} style={{ padding: '8px 20px', backgroundColor: 'var(--green-800)', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>Export to Excel</button>
                </div>
            </div>

            <div style={{ marginTop: '30px', border: '1px solid var(--green-300)', minHeight: '200px', backgroundColor: 'white', padding: '10px' }} className="print-area">
                <h2 className="print-only" style={{ display: 'none', color: 'black', textAlign: 'center' }}>{reportType}</h2>
                {reportData.length > 0 ? (
                    <table style={{ width: '100%', textAlign: 'left', borderCollapse: 'collapse', fontSize: '13px' }}>
                        <thead>
                            <tr style={{ backgroundColor: 'var(--green-100)', color: 'var(--green-900)' }}>
                                {columns.map((col, i) => (
                                    <th key={i} style={{ padding: '10px', borderBottom: '2px solid var(--green-300)' }}>{col}</th>
                                ))}
                            </tr>
                        </thead>
                        <tbody>
                            {reportData.map((row, i) => (
                                <tr key={i} style={{ borderBottom: '1px solid #eee' }}>
                                    {row.map((cell, j) => (
                                        <td key={j} style={{ padding: '10px' }}>{cell}</td>
                                    ))}
                                </tr>
                            ))}
                        </tbody>
                    </table>
                ) : (
                    <div style={{ height: '100%', display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
                        <span style={{ color: 'var(--green-600)', fontSize: '14px' }}>No columns in table</span>
                    </div>
                )}
            </div>
        </div>
    );
};

export default ReportGeneration;
