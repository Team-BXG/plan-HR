import React from 'react';
import { useAuth } from '../context/AuthContext';

const EmployeeDashboard = () => {
  const { user } = useAuth();
  
  // Decide which screen to show based on URL routing logic handled from the sidebar mappings
  const isProfileView = window.location.pathname.includes('/profile');
  
  // Hardcoded constants mirroring the screenshots accurately
  const todayDateStr = "Wednesday, April 29, 2026"; 

  // Basic mock profile
  const employeeData = {
     employee_id: user?.name ? 'E001' : 'E0019',
     name: user?.name || 'kebe Abuch',
     date_of_birth: '2026-01-04',
     phone: '0978652468',
     department: 'Management',
     position: 'Cleaner',
     join_date: '2026-01-04',
     education: 'High School',
     salary: '67880.00'
  };

  return (
    <div style={{ padding: '0px', height: '100%', position: 'relative' }}>
      
      {!isProfileView ? (
          <div style={{ textAlign: 'center', marginTop: '40px', marginBottom: '20px' }}>
             <h2 style={{ color: 'var(--green-900)', fontWeight: 'bold' }}>Welcome, {employeeData.name}!</h2>
             <p style={{ color: 'var(--text-primary)', fontSize: '13px' }}>{todayDateStr}</p>

             {/* Metrics Cards */}
             <div style={{ display: 'flex', justifyContent: 'center', gap: '20px', marginTop: '40px' }}>
                  <div style={{ backgroundColor: '#e8f5e9', padding: '15px 30px', borderRadius: '10px', textAlign: 'center', width: '120px' }}>
                       <div style={{ fontSize: '12px', color: '#555' }}>Present Days</div>
                       <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#4caf50', marginTop: '5px' }}>0</div>
                  </div>
                  <div style={{ backgroundColor: '#ffebee', padding: '15px 30px', borderRadius: '10px', textAlign: 'center', width: '120px' }}>
                       <div style={{ fontSize: '12px', color: '#555' }}>Absent Days</div>
                       <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#f44336', marginTop: '5px' }}>0</div>
                  </div>
                  <div style={{ backgroundColor: '#e3f2fd', padding: '15px 30px', borderRadius: '10px', textAlign: 'center', width: '120px' }}>
                       <div style={{ fontSize: '12px', color: '#555' }}>Leave Days</div>
                       <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#2196f3', marginTop: '5px' }}>0</div>
                  </div>
             </div>
          </div>
      ) : (
          <div style={{ padding: '40px 60px' }}>
             <h3 style={{ color: 'var(--green-900)', borderBottom: '1px solid var(--green-300)', paddingBottom: '10px' }}>Personal Information</h3>
             <div style={{ display: 'grid', gridTemplateColumns: '150px 1fr', gap: '10px', fontSize: '14px', marginBottom: '40px', marginTop: '20px', color: 'var(--text-primary)' }}>
                 <strong style={{color: 'var(--green-900)'}}>Employee ID:</strong> <span>{employeeData.employee_id}</span>
                 <strong style={{color: 'var(--green-900)'}}>Name:</strong> <span>{employeeData.name}</span>
                 <strong style={{color: 'var(--green-900)'}}>Date of Birth:</strong> <span>{employeeData.date_of_birth}</span>
                 <strong style={{color: 'var(--green-900)'}}>Phone:</strong> <span>{employeeData.phone}</span>
             </div>

             <h3 style={{ color: 'var(--green-900)', borderBottom: '1px solid var(--green-300)', paddingBottom: '10px' }}>Employment Information</h3>
             <div style={{ display: 'grid', gridTemplateColumns: '150px 1fr', gap: '10px', fontSize: '14px', marginTop: '20px', color: 'var(--text-primary)' }}>
                 <strong style={{color: 'var(--green-900)'}}>Department:</strong> <span>{employeeData.department}</span>
                 <strong style={{color: 'var(--green-900)'}}>Position:</strong> <span>{employeeData.position}</span>
                 <strong style={{color: 'var(--green-900)'}}>Join Date:</strong> <span>{employeeData.join_date}</span>
                 <strong style={{color: 'var(--green-900)'}}>Education:</strong> <span>{employeeData.education}</span>
                 <strong style={{color: 'var(--green-900)'}}>Salary:</strong> <span>{employeeData.salary}</span>
             </div>
          </div>
      )}

    </div>
  );
};
export default EmployeeDashboard;
