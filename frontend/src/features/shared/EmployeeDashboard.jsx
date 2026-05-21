import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useAuth } from '../../app/providers/AuthContext';

const EmployeeDashboard = () => {
  const { user } = useAuth();
  
  // Decide which screen to show based on URL routing logic handled from the sidebar mappings
  const isProfileView = window.location.pathname.includes('/profile');
  
  // Dynamic date showing today
  const todayDateStr = new Date().toLocaleDateString('en-US', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' }); 

  const [employeeData, setEmployeeData] = useState(null);
  const [stats, setStats] = useState({ present: 0, absent: 0, leave: 0 });

  useEffect(() => {
    const fetchData = async () => {
       try {
          const empId = user?.employee_id || (user?.name === 'admin' ? 'E001' : 'E011');
          
          const empResp = await axios.get(`http://localhost:8000/api/employees/${empId}/`);
          setEmployeeData(empResp.data);

          const statsResp = await axios.get(`http://localhost:8000/api/attendance/employee-stats/?employee_id=${empId}`);
          setStats(statsResp.data);
       } catch (e) {
          console.error("Error fetching dashboard data", e);
          // Fallback if failed
          setEmployeeData({
             employee_id: user?.employee_id || 'E001',
             name: user?.name || 'kebe Abuch',
             date_of_birth: '2026-01-04',
             phone_number: '0978652468',
             department: 'Management',
             position: 'Cleaner',
             join_date: '2026-01-04',
             education: 'High School',
             salary: '67880.00'
          });
       }
    };
    fetchData();
  }, [user]);

  if (!employeeData) return <div style={{padding: '20px'}}>Loading...</div>;


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
                       <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#4caf50', marginTop: '5px' }}>{stats.present}</div>
                  </div>
                  <div style={{ backgroundColor: '#ffebee', padding: '15px 30px', borderRadius: '10px', textAlign: 'center', width: '120px' }}>
                       <div style={{ fontSize: '12px', color: '#555' }}>Absent Days</div>
                       <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#f44336', marginTop: '5px' }}>{stats.absent}</div>
                  </div>
                  <div style={{ backgroundColor: '#e3f2fd', padding: '15px 30px', borderRadius: '10px', textAlign: 'center', width: '120px' }}>
                       <div style={{ fontSize: '12px', color: '#555' }}>Leave Days</div>
                       <div style={{ fontSize: '24px', fontWeight: 'bold', color: '#2196f3', marginTop: '5px' }}>{stats.leave}</div>
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
                 <strong style={{color: 'var(--green-900)'}}>Phone:</strong> <span>{employeeData.phone_number || employeeData.phone}</span>
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
