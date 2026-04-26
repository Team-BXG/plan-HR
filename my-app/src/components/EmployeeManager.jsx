import React, { useState, useEffect } from 'react';
import axios from 'axios';

const EmployeeManager = () => {
  const [employees, setEmployees] = useState([]);
  const [selectedEmp, setSelectedEmp] = useState(null);
  const [showAddModal, setShowAddModal] = useState(false);
  
  useEffect(() => {
    fetchEmployees();
  }, []);

  const fetchEmployees = async () => {
    try {
      const resp = await axios.get('http://localhost:8000/api/employees/');
      setEmployees(resp.data.results || resp.data || []);
    } catch (e) {
      // Mock for UI dev
      setEmployees([
        { id: 'E001', employee_id: 'E001', name: 'Abebe Kebede', department: 'Administration', position: 'Admin', phone_number: '0912345678', is_active: true },
        { id: 'E004', employee_id: 'E004', name: 'Alemnesh Kassahun', department: 'HR', position: 'HR Specialist', phone_number: '0945678901', is_active: true },
        { id: 'E005', employee_id: 'E005', name: 'Dawit Solomon', department: 'IT', position: 'HR Manager', phone_number: '0956789012', is_active: true }
      ]);
    }
  };

  const isAdvancedSearch = window.location.pathname.includes('/employees/search/advanced');
  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
      
      {/* Top Search bar area matching Screenshot Addons */}
      <div className="glass-panel" style={{ padding: '0px', marginBottom: '20px', borderBottom: '1px solid #ccc' }}>
        <div style={{ display: 'flex', borderBottom: '2px solid #e0e0e0', backgroundColor: '#e0e0e0', padding: '5px 5px 0 5px' }}>
              <div onClick={() => window.location.href = '/employees/search'} style={{ 
                    padding: '8px 15px', fontSize: '13px', cursor: 'pointer',
                    backgroundColor: isAdvancedSearch ? 'transparent' : 'white', borderTopLeftRadius: '5px', borderTopRightRadius: '5px',
                    color: isAdvancedSearch ? '#555' : '#6a1b9a', border: isAdvancedSearch ? 'none' : '1px solid #ccc', borderBottom: 'none', display: 'flex', alignItems: 'center', gap: '5px'
                 }}>
                 🔍 Basic Search {(!isAdvancedSearch) && <span>x</span>}
              </div>
              <div onClick={() => window.location.href = '/employees/search/advanced'} style={{ 
                    padding: '8px 15px', fontSize: '13px', cursor: 'pointer',
                    backgroundColor: isAdvancedSearch ? 'white' : 'transparent', borderTopLeftRadius: '5px', borderTopRightRadius: '5px',
                    color: isAdvancedSearch ? '#6a1b9a' : '#555', border: isAdvancedSearch ? '1px solid #ccc' : 'none', borderBottom: 'none', display: 'flex', alignItems: 'center', gap: '5px'
                 }}>
                 ⧉ Advanced Filter {(isAdvancedSearch) && <span>x</span>}
              </div>
          </div>
          
          <div style={{ padding: '15px' }}>
             {!isAdvancedSearch ? (
                 <div style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
                     <input className="glass-input" placeholder="abebe" style={{ borderRadius: '20px', padding: '5px 15px' }} />
                     <button style={{ padding: '5px 20px', backgroundColor: '#9c27b0', color: 'white', border: 'none', borderRadius: '20px', cursor: 'pointer' }}>Search</button>
                 </div>
             ) : (
                 <div style={{ display: 'grid', gridTemplateColumns: '120px 200px', gap: '10px', fontSize: '13px', color: '#555' }}>
                     <label>Department:</label><select className="glass-input" style={{ width: '180px' }}><option>All Departments</option></select>
                     <label>Position:</label><select className="glass-input" style={{ width: '180px' }}><option>All Positions</option></select>
                     <label>Gender:</label><select className="glass-input" style={{ width: '180px' }}><option>All</option></select>
                     <label>Seniority:</label><select className="glass-input" style={{ width: '180px' }}><option>All</option></select>
                     <label>Salary:</label><select className="glass-input" style={{ width: '180px' }}><option>All</option></select>
                 </div>
             )}
          </div>
      </div>

      <div style={{ display: 'flex', flex: 1, gap: '20px' }}>
        {/* Left Side: Master List */}
      <div className="glass-panel" style={{ flex: 2, padding: '20px', overflowY: 'auto' }}>
        <table style={{ width: '100%', textAlign: 'left', borderCollapse: 'collapse', fontSize: '13px' }}>
          <thead>
            <tr style={{ borderBottom: '2px solid #ce93d8', color: '#6a1b9a' }}>
              <th style={{ padding: '8px' }}>ID</th>
              <th>Name</th>
              <th>Department</th>
              <th>Position</th>
              <th>Phone</th>
            </tr>
          </thead>
          <tbody>
            {employees.map(e => (
              <tr 
                key={e.employee_id} 
                onClick={() => setSelectedEmp(e)}
                style={{ 
                  borderBottom: '1px solid #eee', 
                  cursor: 'pointer',
                  backgroundColor: selectedEmp?.employee_id === e.employee_id ? '#0097a7' : 'transparent',
                  color: selectedEmp?.employee_id === e.employee_id ? 'white' : '#333'
                }}
              >
                <td style={{ padding: '10px 8px' }}>{e.employee_id}</td>
                <td>{e.name}</td>
                <td>{e.department}</td>
                <td>{e.position}</td>
                <td>{e.phone_number}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Right side: Employee Details Master View */}
      <div className="glass-panel" style={{ flex: 1, padding: '20px', backgroundColor: '#fafafa' }}>
        <h4 style={{ color: '#4a148c', marginTop: 0, borderBottom: '1px solid #e0e0e0', paddingBottom: '10px' }}>Employee Details</h4>
        
        {selectedEmp ? (
          <div style={{ fontSize: '13px', lineHeight: '1.8' }}>
            <div style={{ display: 'flex' }}><span style={{ width: '90px', color: '#666' }}>Department:</span> {selectedEmp.department}</div>
            <div style={{ display: 'flex' }}><span style={{ width: '90px', color: '#666' }}>Position:</span> {selectedEmp.position}</div>
            <div style={{ display: 'flex' }}><span style={{ width: '90px', color: '#666' }}>Phone:</span> {selectedEmp.phone_number}</div>
            <div style={{ display: 'flex' }}><span style={{ width: '90px', color: '#666' }}>ID:</span> {selectedEmp.employee_id}</div>
            <div style={{ display: 'flex' }}><span style={{ width: '90px', color: '#666' }}>Name:</span> {selectedEmp.name}</div>
            
            <div style={{ marginTop: '20px', display: 'flex', gap: '10px' }}>
               <button style={{ padding: '5px 15px', color: '#666', border: '1px solid #ccc', borderRadius: '4px', cursor: 'pointer' }}>Edit</button>
               <button style={{ padding: '5px 15px', color: '#666', border: '1px solid #ccc', borderRadius: '4px', cursor: 'pointer' }}>Remove</button>
            </div>
          </div>
        ) : (
          <p style={{ color: '#999', fontSize: '14px' }}>Select an employee to view details</p>
        )}
      </div>
      </div>

      {/* Add New Employee Modal Overlay */}
      {window.location.pathname.includes('/employees/add') && (
        <div style={{
          position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
          backgroundColor: 'rgba(255,255,255,0.4)', backdropFilter: 'blur(3px)',
          display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 1000
        }}>
          <div style={{
            width: '450px', backgroundColor: '#f3e5f5', borderRadius: '15px', padding: '20px',
            boxShadow: '0 4px 15px rgba(0,0,0,0.2)', border: '1px solid #dba6e5'
          }}>
             <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid #dba6e5', paddingBottom: '10px' }}>
                <h4 style={{ margin: 0, color: '#4a148c' }}>+ Add New Employee</h4>
                <div style={{ cursor: 'pointer' }} onClick={() => window.history.back()}>X</div>
             </div>
             
             <div style={{ display: 'grid', gridTemplateColumns: '120px 1fr', gap: '10px', marginTop: '15px', fontSize: '13px', color: '#6a1b9a', fontWeight: 'bold' }}>
                <label>Employee ID*:</label><input className="glass-input" placeholder="4-5 letters/numbers" />
                <label>Full Name*:</label><input className="glass-input" placeholder="First Last (10-20 chars)" />
                <label>Date of Birth:</label><input className="glass-input" type="date" />
                <label>Department*:</label><select className="glass-input"><option>Administration</option><option>HR</option></select>
                <label>Position*:</label><select className="glass-input"><option>Admin</option><option>HR Specialist</option></select>
                <label>Education*:</label><select className="glass-input"><option>BSc</option><option>MSc</option></select>
                <label>Gender:</label><select className="glass-input"><option>Female</option><option>Male</option></select>
                <label>Join Date:</label><input className="glass-input" type="date" />
                <label>Salary*:</label><input className="glass-input" />
                <label>Phone*:</label><input className="glass-input" placeholder="09xxxxxxxx" />
             </div>

             <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '20px' }}>
                <button style={{ padding: '8px 15px', borderRadius: '20px', border: '1px solid #6a1b9a', background: 'transparent', cursor: 'pointer' }}>Clear Form</button>
                <div>
                   <button style={{ padding: '8px 15px', borderRadius: '20px', border: 'none', background: '#6a1b9a', color: 'white', cursor: 'pointer', marginRight: '10px' }}>Add Employee</button>
                   <button onClick={() => window.history.back()} style={{ padding: '8px 15px', borderRadius: '20px', border: 'none', background: 'transparent', cursor: 'pointer' }}>Cancel</button>
                </div>
             </div>
          </div>
        </div>
      )}
    </div>
  )
}
export default EmployeeManager;
