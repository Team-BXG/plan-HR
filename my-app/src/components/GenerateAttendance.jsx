import React, { useState } from 'react';
import axios from 'axios';

const GenerateAttendance = () => {
  const [employees, setEmployees] = useState([]);
  const [activeEmployee, setActiveEmployee] = useState('');
  const [fromDate, setFromDate] = useState('2026-04-01');
  const [toDate, setToDate] = useState('2026-04-30');
  const [reportData, setReportData] = useState([]);

  React.useEffect(() => {
     fetchEmployees();
  }, []);

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

  const handleGenerate = async () => {
      try {
          const resp = await axios.post('http://localhost:8000/api/attendance/report/', {
              employee_id: activeEmployee,
              from_date: fromDate,
              to_date: toDate
          });
          setReportData(resp.data.report_data || []);
      } catch (e) {
          console.error("Failed to generate report", e);
          alert('Failed to generate report: ' + (e.response?.data?.error || e.message));
      }
  };

  return (
    <div style={{ padding: '20px' }}>
      <h3 style={{ color: 'var(--green-900)', marginBottom: '20px' }}>Daily Attendance Report</h3>
      
      <div style={{ display: 'flex', flexDirection: 'column', gap: '15px', marginBottom: '20px' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
          <label style={{ width: '80px', color: 'var(--text-primary)' }}>Employee:</label>
          <select className="input-field" style={{ width: '250px' }} value={activeEmployee} onChange={e => setActiveEmployee(e.target.value)}>
             {employees.map(emp => {
                 const empId = emp.id || emp.employee_id;
                 return <option key={empId} value={empId}>{empId} - {emp.name}</option>;
             })}
          </select>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
           <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
              <label style={{ color: 'var(--text-primary)' }}>From:</label>
              <input type="date" className="input-field" value={fromDate} onChange={e => setFromDate(e.target.value)} />
           </div>
           
           <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
              <label style={{ color: 'var(--text-primary)' }}>To:</label>
              <input type="date" className="input-field" value={toDate} onChange={e => setToDate(e.target.value)} />
           </div>

           <button className="btn-primary" onClick={handleGenerate} style={{ padding: '8px 20px', borderRadius: '4px' }}>
              Generate Report
           </button>
        </div>
      </div>

      <div className="glass-panel" style={{ padding: '0', overflow: 'hidden' }}>
        <table style={{ width: '100%', textAlign: 'left', borderCollapse: 'collapse', fontSize: '14px' }}>
          <thead>
            <tr style={{ borderBottom: '2px solid var(--green-300)', backgroundColor: 'var(--green-100)', color: 'var(--green-900)' }}>
              <th style={{ padding: '12px' }}>Date</th>
              <th style={{ padding: '12px' }}>Status</th>
            </tr>
          </thead>
          <tbody>
            {reportData.map((record, index) => (
              <tr key={index} style={{ borderBottom: '1px solid #eee' }}>
                <td style={{ padding: '12px' }}>{record.date}</td>
                <td style={{ padding: '12px', fontWeight: 'bold', color: record.status === 'Present' ? '#4caf50' : record.status === 'Leave' ? '#2196f3' : '#f44336' }}>{record.status}</td>
              </tr>
            ))}
            {reportData.length === 0 && (
                <tr><td colSpan="2" style={{textAlign: 'center', padding: '20px', color: '#888'}}>No data to display. Click Generate.</td></tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default GenerateAttendance;
