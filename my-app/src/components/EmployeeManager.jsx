import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Search, Filter } from 'lucide-react';

const EmployeeManager = () => {
  const [employees, setEmployees] = useState([]);
  const [selectedEmp, setSelectedEmp] = useState(null);
  const [showAddModal, setShowAddModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [editTab, setEditTab] = useState('Basic Info');
  const [currentPage, setCurrentPage] = useState(1);
  const rowsPerPage = 5;
  
  useEffect(() => {
    fetchEmployees();
  }, []);

  const fetchEmployees = async () => {
    try {
      const resp = await axios.get('http://localhost:8000/api/employees/');
      setEmployees(resp.data.results || resp.data || []);
    } catch (e) {
        // UI Fallback using realistic mock data
        setEmployees([
          { id: 'E001', employee_id: 'E001', name: 'Abebe Kebede', department: 'Management', position: 'Admin', phone_number: '0912345678', education: 'BSc', salary: '50000.00', is_active: true },
          { id: 'E0019', employee_id: 'E0019', name: 'kebe Abuch', department: 'Management', position: 'Cleaner', phone_number: '0978652468', education: 'High School', salary: '67880.00', is_active: true },
          { id: 'E002', employee_id: 'E002', name: 'Tigist Worku', department: 'Finance', position: 'Accountant', phone_number: '0923456789', education: 'MSc', salary: '62000.00', is_active: true },
          { id: 'E0020', employee_id: 'E0020', name: 'yuiiiiiii iiiiiiiiii', department: 'IT', position: 'Cleaner', phone_number: '0997654667', education: 'High School', salary: '6786.00', is_active: true },
          { id: 'E0021', employee_id: 'E0021', name: 'yene deseee', department: 'Management', position: 'Developer', phone_number: '0906765433', education: 'High School', salary: '456666.00', is_active: true },
          { id: 'E003', employee_id: 'E003', name: 'Mekonnen Alemu', department: 'Marketing', position: 'Marketing Executive', phone_number: '0934567890', education: 'BSc', salary: '47000.00', is_active: true },
          { id: 'E004', employee_id: 'E004', name: 'Alemnesh Kassahune', department: 'Management', position: 'Developer', phone_number: '0945678901', education: 'MBA', salary: '59000.00', is_active: true },
          { id: 'E005', employee_id: 'E005', name: 'Dawit Solomon', department: 'IT', position: 'HR Manager', phone_number: '0956789012', education: 'BSc', salary: '51000.00', is_active: true },
          { id: 'E006', employee_id: 'E006', name: 'Tsion Habtu', department: 'IT', position: 'Manager', phone_number: '0987654323', education: 'Bachelor\'s', salary: '52000.00', is_active: true },
          { id: 'E007', employee_id: 'E007', name: 'Hermela Belay', department: 'Security', position: 'Developer', phone_number: '0989898989', education: 'Bachelor\'s', salary: '56000.00', is_active: true },
          { id: 'E008', employee_id: 'E008', name: 'Deborah Habtu', department: 'IT', position: 'Analyst', phone_number: '0945678989', education: 'Master\'s', salary: '45000.00', is_active: true },
          { id: 'E009', employee_id: 'E009', name: 'beza dememe', department: 'IT', position: 'Analyst', phone_number: '0967567889', education: 'Bachelor\'s', salary: '45000.00', is_active: true },
          { id: 'E011', employee_id: 'E011', name: 'Abraham Kebede', department: 'Management', position: 'Developer', phone_number: '0912345667', education: 'BSc', salary: '50000.00', is_active: true },
          { id: 'E012', employee_id: 'E012', name: 'Bemnet Worku', department: 'Finance', position: 'Intern', phone_number: '0908765455', education: 'Bachelor\'s', salary: '4500.00', is_active: false },
          { id: 'E013', employee_id: 'E013', name: 'Mahilet Tesfaye', department: 'Management', position: 'Developer', phone_number: '0906543234', education: 'Bachelor\'s', salary: '340000.00', is_active: true },
          { id: 'E014', employee_id: 'E014', name: 'Genet Tesafye', department: 'IT', position: 'Manager', phone_number: '0967564534', education: 'High School', salary: '567800.00', is_active: true },
        ]);
    }
  };

  // Pagination Logic
  const indexOfLastRow = currentPage * rowsPerPage;
  const indexOfFirstRow = indexOfLastRow - rowsPerPage;
  const currentEmployees = employees.slice(indexOfFirstRow, indexOfLastRow);
  const totalPages = Math.ceil(employees.length / rowsPerPage);

  const isAdvancedSearch = window.location.pathname.includes('/employees/search/advanced');
  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
      
      {/* Top Search bar area matching Screenshot Addons */}
      {window.location.pathname.includes('/search') && (
        <div className="glass-panel" style={{ padding: '0px', marginBottom: '20px', borderBottom: '1px solid #ccc' }}>
          <div style={{ display: 'flex', borderBottom: '1px solid var(--green-200)', backgroundColor: 'var(--green-100)', padding: '5px 5px 0 5px' }}>
                <div onClick={() => window.location.href = '/employees/search'} style={{ 
                      padding: '8px 15px', fontSize: '13px', cursor: 'pointer',
                      backgroundColor: isAdvancedSearch ? 'transparent' : 'white', borderTopLeftRadius: '5px', borderTopRightRadius: '5px',
                      color: isAdvancedSearch ? 'var(--green-800)' : 'var(--green-700)', border: isAdvancedSearch ? 'none' : '1px solid var(--green-200)', borderBottom: 'none', display: 'flex', alignItems: 'center', gap: '5px'
                   }}>
                   <Search size={14}/> Basic Search {(!isAdvancedSearch) && <span>x</span>}
                </div>
                <div onClick={() => window.location.href = '/employees/search/advanced'} style={{ 
                      padding: '8px 15px', fontSize: '13px', cursor: 'pointer',
                      backgroundColor: isAdvancedSearch ? 'white' : 'transparent', borderTopLeftRadius: '5px', borderTopRightRadius: '5px',
                      color: isAdvancedSearch ? 'var(--green-700)' : 'var(--green-800)', border: isAdvancedSearch ? '1px solid var(--green-200)' : 'none', borderBottom: 'none', display: 'flex', alignItems: 'center', gap: '5px'
                   }}>
                   <Filter size={14}/> Advanced Filter {(isAdvancedSearch) && <span>x</span>}
                </div>
            </div>
            
            <div style={{ padding: '15px' }}>
               {!isAdvancedSearch ? (
                   <div style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
                       <input className="input-field" placeholder="Search criteria..." style={{ borderRadius: '20px', padding: '5px 15px' }} />
                       <button style={{ padding: '5px 20px', backgroundColor: 'var(--green-500)', color: 'white', border: 'none', borderRadius: '20px', cursor: 'pointer' }}>Search</button>
                   </div>
               ) : (
                   <div style={{ display: 'grid', gridTemplateColumns: '120px 200px', gap: '10px', fontSize: '13px', color: 'var(--text-primary)' }}>
                       <label>Department:</label><select className="input-field" style={{ width: '180px' }}><option>All Departments</option></select>
                       <label>Position:</label><select className="input-field" style={{ width: '180px' }}><option>All Positions</option></select>
                       <label>Gender:</label><select className="input-field" style={{ width: '180px' }}><option>All</option></select>
                       <label>Seniority:</label><select className="input-field" style={{ width: '180px' }}><option>All</option></select>
                       <label>Salary:</label><select className="input-field" style={{ width: '180px' }}><option>
                         All</option>
                         <option>&lt;10,000</option>
                         <option>10,000-30,000</option>
                         <option>30,000-50,000</option>
                         <option>&gt;50,000</option>
                       </select>
                   </div>
               )}
            </div>
        </div>
      )}

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
            {currentEmployees.map(e => (
              <tr 
                key={e.employee_id} 
                onClick={() => setSelectedEmp(e)}
                style={{ 
                  borderBottom: '1px solid #eee', 
                  cursor: 'pointer',
                  backgroundColor: selectedEmp?.employee_id === e.employee_id ? 'var(--green-200)' : 'transparent',
                  color: selectedEmp?.employee_id === e.employee_id ? 'var(--green-900)' : '#333'
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
        
        {/* Pagination Controls */}
        {employees.length > rowsPerPage && (
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '15px', color: 'var(--text-primary)' }}>
            <span style={{ fontSize: '13px' }}>Page {currentPage} of {totalPages}</span>
            <div style={{ display: 'flex', gap: '5px' }}>
               <button 
                  onClick={() => setCurrentPage(prev => Math.max(prev - 1, 1))}
                  disabled={currentPage === 1}
                  style={{ padding: '4px 10px', borderRadius: '4px', border: '1px solid var(--green-300)', backgroundColor: currentPage === 1 ? '#f5f5f5' : 'white', cursor: currentPage === 1 ? 'not-allowed' : 'pointer', color: 'var(--green-900)' }}>
                  Previous
               </button>
               <button 
                  onClick={() => setCurrentPage(prev => Math.min(prev + 1, totalPages))}
                  disabled={currentPage === totalPages}
                  style={{ padding: '4px 10px', borderRadius: '4px', border: '1px solid var(--green-300)', backgroundColor: currentPage === totalPages ? '#f5f5f5' : 'white', cursor: currentPage === totalPages ? 'not-allowed' : 'pointer', color: 'var(--green-900)' }}>
                  Next
               </button>
            </div>
          </div>
        )}
      </div>

      {/* Right side: Employee Details Master View */}
      <div className="glass-panel" style={{ flex: 1, padding: '20px', backgroundColor: 'var(--green-100)' }}>
        <h4 style={{ color: 'var(--green-800)', marginTop: 0, borderBottom: '1px solid var(--green-300)', paddingBottom: '10px' }}>Employee Details</h4>
        
        {selectedEmp ? (
          <div style={{ fontSize: '13px', lineHeight: '1.8' }}>
            <div style={{ display: 'flex' }}><span style={{ width: '90px', color: '#666' }}>Department:</span> {selectedEmp.department}</div>
            <div style={{ display: 'flex' }}><span style={{ width: '90px', color: '#666' }}>Position:</span> {selectedEmp.position}</div>
            <div style={{ display: 'flex' }}><span style={{ width: '90px', color: '#666' }}>Phone:</span> {selectedEmp.phone_number}</div>
            <div style={{ display: 'flex' }}><span style={{ width: '90px', color: '#666' }}>ID:</span> {selectedEmp.employee_id}</div>
            <div style={{ display: 'flex' }}><span style={{ width: '90px', color: '#666' }}>Name:</span> {selectedEmp.name}</div>
            
            <div style={{ marginTop: '20px', display: 'flex', gap: '10px' }}>
               <button onClick={() => setShowEditModal(true)} style={{ padding: '5px 15px', color: 'var(--green-800)', border: '1px solid var(--green-400)', borderRadius: '4px', cursor: 'pointer', backgroundColor: 'white' }}>Edit</button>
               <button onClick={() => setShowDeleteModal(true)} style={{ padding: '5px 15px', color: 'var(--green-800)', border: '1px solid var(--green-400)', borderRadius: '4px', cursor: 'pointer', backgroundColor: 'white' }}>Remove</button>
            </div>
          </div>
        ) : (
          <p style={{ color: 'var(--green-600)', fontSize: '14px' }}>Select an employee to view details</p>
        )}
      </div>
      </div>

      {/* Edit Modal Overlay */}
      {showEditModal && selectedEmp && (
        <div style={{
          position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
          backgroundColor: 'rgba(255,255,255,0.6)', backdropFilter: 'blur(3px)',
          display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 1000
        }}>
          <div style={{
            width: '450px', backgroundColor: 'white', borderRadius: '8px', padding: '15px',
            boxShadow: '0 4px 15px rgba(0,0,0,0.2)', border: '1px solid var(--green-400)'
          }}>
             <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid var(--green-200)', paddingBottom: '10px', marginBottom: '10px' }}>
                <h4 style={{ margin: 0, color: 'var(--green-800)', display: 'flex', gap: '10px' }}>
                   <span style={{color: 'green'}}>📘</span> Edit Employee - {selectedEmp.name}
                </h4>
                <div style={{ cursor: 'pointer' }} onClick={() => setShowEditModal(false)}>✕</div>
             </div>
             
             <div style={{ display: 'flex', gap: '5px', marginBottom: '15px', backgroundColor: '#e0e0e0', padding: '5px', borderRadius: '4px' }}>
                <div 
                   onClick={() => setEditTab('Basic Info')}
                   style={{ backgroundColor: editTab === 'Basic Info' ? 'white' : 'transparent', padding: '5px 10px', border: editTab === 'Basic Info' ? '1px solid #ccc' : 'none', fontSize: '13px', cursor: 'pointer', color: editTab === 'Basic Info' ? '#333' : '#555' }}>
                   Basic Info
                </div>
                <div 
                   onClick={() => setEditTab('Employment')}
                   style={{ backgroundColor: editTab === 'Employment' ? 'white' : 'transparent', padding: '5px 10px', border: editTab === 'Employment' ? '1px solid #ccc' : 'none', color: editTab === 'Employment' ? '#333' : '#555', fontSize: '13px', cursor: 'pointer' }}>
                   Employment
                </div>
             </div>

             {editTab === 'Basic Info' && (
               <div style={{ display: 'grid', gridTemplateColumns: '150px 1fr', gap: '15px', fontSize: '13px', color: 'var(--green-900)', fontWeight: 'bold' }}>
                  <label style={{color: 'var(--green-900)'}}>ID:</label>
                  <input className="input-field" value={selectedEmp.employee_id} readOnly style={{backgroundColor: '#f5f5f5'}} />
                  
                  <label style={{color: 'var(--green-900)'}}>Name* (10-20 chars):</label>
                  <input className="input-field" defaultValue={selectedEmp.name} />
                  
                  <label style={{color: 'var(--green-900)'}}>Department*:</label>
                  <select className="input-field"><option>{selectedEmp.department}</option><option>IT</option><option>Marketing</option></select>
                  
                  <label style={{color: 'var(--green-900)'}}>Phone* (09xxxxxxxx):</label>
                  <input className="input-field" defaultValue={selectedEmp.phone_number} />
               </div>
             )}

             {editTab === 'Employment' && (
               <div style={{ display: 'grid', gridTemplateColumns: '150px 1fr', gap: '15px', fontSize: '13px', color: 'var(--green-900)', fontWeight: 'bold' }}>
                  <label style={{color: 'var(--green-900)'}}>Position*:</label>
                  <select className="input-field"><option>{selectedEmp.position}</option><option>Manager</option><option>Developer</option></select>
                  
                  <label style={{color: 'var(--green-900)'}}>Education*:</label>
                  <select className="input-field"><option>{selectedEmp.education || 'BSc'}</option><option>MBA</option><option>MSc</option></select>
                  
                  <label style={{color: 'var(--green-900)'}}>Salary*:</label>
                  <input className="input-field" defaultValue={selectedEmp.salary || '0.00'} />
               </div>
             )}

             <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '30px', gap: '10px' }}>
                <button onClick={() => setShowEditModal(false)} style={{ padding: '5px 20px', borderRadius: '4px', border: '1px solid #ccc', background: 'transparent', cursor: 'pointer' }}>Cancel</button>
                <button style={{ padding: '5px 20px', borderRadius: '4px', border: '1px solid #5abed6', background: '#5abed6', color: 'white', cursor: 'pointer' }}>OK</button>
             </div>
          </div>
        </div>
      )}

      {/* Delete Modal Overlay */}
      {showDeleteModal && selectedEmp && (
        <div style={{
          position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
          backgroundColor: 'rgba(255,255,255,0.6)', backdropFilter: 'blur(3px)',
          display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 1000
        }}>
          <div style={{
            width: '350px', backgroundColor: '#f0f4f8', borderRadius: '8px', padding: '15px',
            boxShadow: '0 4px 15px rgba(0,0,0,0.2)', border: '1px solid var(--green-400)'
          }}>
             <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid white', paddingBottom: '10px' }}>
                <h4 style={{ margin: 0, color: 'var(--green-800)', display: 'flex', alignItems: 'center', gap:'10px' }}>
                   <span style={{color: 'green'}}>📘</span> Confirm Deletion
                </h4>
                <div style={{ cursor: 'pointer' }} onClick={() => setShowDeleteModal(false)}>✕</div>
             </div>
             
             <div style={{ margin: '20px 0', fontSize: '14px', color: 'var(--text-primary)' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                   <span>Delete Employee</span>
                   <div style={{ background: '#5abed6', color: 'white', borderRadius: '50%', width: '30px', height: '30px', display: 'flex', justifyContent: 'center', alignItems: 'center', fontWeight: 'bold' }}>?</div>
                </div>
                Are you sure you want to delete employee {selectedEmp.employee_id}?
             </div>

             <div style={{ display: 'flex', justifyContent: 'center', marginTop: '20px', gap: '10px' }}>
                <button style={{ padding: '5px 20px', borderRadius: '4px', border: '1px solid #5abed6', background: '#5abed6', color: 'white', cursor: 'pointer' }}>OK</button>
                <button onClick={() => setShowDeleteModal(false)} style={{ padding: '5px 20px', borderRadius: '4px', border: '1px solid #ccc', background: 'transparent', cursor: 'pointer' }}>Cancel</button>
             </div>
          </div>
        </div>
      )}

      {/* Add New Employee Modal Overlay */}
      {window.location.pathname.includes('/employees/add') && (
        <div style={{
          position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
          backgroundColor: 'rgba(255,255,255,0.6)', backdropFilter: 'blur(3px)',
          display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 1000
        }}>
          <div style={{
            width: '450px', backgroundColor: 'var(--green-100)', borderRadius: '15px', padding: '20px',
            boxShadow: '0 4px 15px rgba(0,0,0,0.2)', border: '1px solid var(--green-400)'
          }}>
             <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid var(--green-400)', paddingBottom: '10px' }}>
                <h4 style={{ margin: 0, color: 'var(--green-900)' }}>+ Add New Employee</h4>
                <div style={{ cursor: 'pointer' }} onClick={() => window.history.back()}>X</div>
             </div>
             
             <div style={{ display: 'grid', gridTemplateColumns: '120px 1fr', gap: '10px', marginTop: '15px', fontSize: '13px', color: 'var(--green-800)', fontWeight: 'bold' }}>
                <label>Employee ID*:</label><input className="input-field" placeholder="4-5 letters/numbers" />
                <label>Full Name*:</label><input className="input-field" placeholder="First Last (10-20 chars)" />
                <label>Date of Birth:</label><input className="input-field" type="date" />
                <label>Department*:</label><select className="input-field"><option>Administration</option><option>HR</option></select>
                <label>Position*:</label><select className="input-field"><option>Admin</option><option>HR Specialist</option></select>
                <label>Education*:</label><select className="input-field"><option>BSc</option><option>MSc</option></select>
                <label>Gender:</label><select className="input-field"><option>Female</option><option>Male</option></select>
                <label>Join Date:</label><input className="input-field" type="date" />
                <label>Salary*:</label><input className="input-field" />
                <label>Phone*:</label><input className="input-field" placeholder="09xxxxxxxx" />
             </div>

             <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: '20px' }}>
                <button style={{ padding: '8px 15px', borderRadius: '20px', border: '1px solid var(--green-800)', background: 'transparent', cursor: 'pointer' }}>Clear Form</button>
                <div>
                   <button style={{ padding: '8px 15px', borderRadius: '20px', border: 'none', background: 'var(--green-600)', color: 'white', cursor: 'pointer', marginRight: '10px' }}>Add Employee</button>
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
