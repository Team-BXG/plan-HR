import React, { useState } from 'react';
import { Routes, Route, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { LogOut, Users, Building, ShieldCheck, Clock, Calendar, Key, UserPlus, Search, FileText, UserX, FileSpreadsheet, PlusCircle, MinusCircle, Edit } from 'lucide-react';
import EmployeeManager from '../components/EmployeeManager';
import DepartmentManager from '../components/DepartmentManager';
import AttendancePunch from '../components/AttendancePunch';
import LeaveManager from '../components/LeaveManager';
import EmployeeDashboard from '../components/EmployeeDashboard';
import ReportGeneration from '../components/ReportGeneration';

const Dashboard = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const navItemStyle = {
    padding: '8px 15px',
    borderRadius: '10px',
    cursor: 'pointer',
    color: '#6a1b9a',
    fontSize: '14px',
    display: 'flex',
    alignItems: 'center',
    gap: '10px',
    transition: '0.2s',
    marginBottom: '2px'
  };

  const categoryStyle = {
    fontWeight: 'bold',
    color: '#6a1b9a',
    fontSize: '15px',
    padding: '10px 0 5px 5px',
    display: 'flex',
    alignItems: 'center',
    gap: '8px'
  };

  return (
    <div className="app-container">
      {/* Sidebar */}
      <div className="sidebar" style={{ width: '280px', padding: '15px', backgroundColor: '#f3e5f5' }}>
        <div style={{ paddingBottom: '15px', borderBottom: '1px solid #ce93d8' }}>
          <h2 style={{ margin: 0, color: '#6a1b9a', display: 'flex', alignItems: 'center', gap: '10px', fontSize: '18px' }}>
            <ShieldCheck /> {user?.role === 'Admin' ? 'Admin Dashboard' : user?.role === 'HR Manager' ? 'HR Manager Dashboard' : 'Employee Dashboard'}
          </h2>
        </div>
        
        <div style={{ flex: 1, overflowY: 'auto' }}>
          {/* Change Password - All roles */}
          <div style={categoryStyle}><Key size={18}/> Change Password</div>
          <div className="sidebar-sub">
            <div style={navItemStyle} onClick={() => navigate('/password')}>Change Password</div>
          </div>

          {/* Admin / HR Manager features */}
          {(user?.role === 'Admin' || user?.role === 'HR Manager') && (
            <>
              <div style={categoryStyle}><Users size={18}/> Employee Manage...</div>
              <div className="sidebar-sub" style={{ paddingLeft: '20px' }}>
                <div style={navItemStyle} onClick={() => navigate('/employees')}><Search size={14}/> View Employees</div>
                <div style={navItemStyle} onClick={() => navigate('/employees/add')}><UserPlus size={14}/> Add Employee</div>
                <div style={navItemStyle} onClick={() => navigate('/employees/search')}><Search size={14}/> Search Employees</div>
                <div style={navItemStyle} onClick={() => navigate('/leave/generate')}><FileText size={14}/> Generate Leave</div>
                <div style={navItemStyle} onClick={() => navigate('/employees/inactive')}><UserX size={14}/> Manage Inactive</div>
                <div style={navItemStyle} onClick={() => navigate('/attendance/generate')}><FileSpreadsheet size={14}/> Generate Attendance</div>
                <div style={navItemStyle} onClick={() => navigate('/leave/register')}><Calendar size={14}/> Register Leave</div>
                <div style={navItemStyle} onClick={() => navigate('/reports/generate')}><FileSpreadsheet size={14}/> Generate Reports</div>
              </div>
            </>
          )}

          {/* Department Management - Admin Only */}
          {user?.role === 'Admin' && (
            <>
              <div style={categoryStyle}><Building size={18}/> Department Mana...</div>
              <div className="sidebar-sub" style={{ paddingLeft: '20px' }}>
                <div style={navItemStyle} onClick={() => navigate('/departments')}><Search size={14}/> View Departments</div>
                <div style={navItemStyle} onClick={() => navigate('/departments/add')}><PlusCircle size={14}/> Add Department</div>
                <div style={navItemStyle} onClick={() => navigate('/departments/remove')}><MinusCircle size={14}/> Remove Department</div>
                <div style={navItemStyle} onClick={() => navigate('/departments/search')}><Search size={14}/> Search Department</div>
                <div style={navItemStyle} onClick={() => navigate('/departments/update')}><Edit size={14}/> Update Department</div>
              </div>
            </>
          )}

          {/* Employee specifics */}
          {user?.role === 'Employee' && (
            <>
              <div style={categoryStyle}><Users size={18}/> Employee Actions</div>
              <div className="sidebar-sub" style={{ paddingLeft: '20px' }}>
                <div style={navItemStyle} onClick={() => navigate('/')}>Dashboard</div>
                <div style={navItemStyle} onClick={() => navigate('/attendance')}>Punch In/Out</div>
                <div style={navItemStyle} onClick={() => navigate('/profile')}>View My Profile</div>
              </div>
            </>
          )}
        </div>

        <div style={{...navItemStyle, color: '#d32f2f', fontWeight: 'bold'}} onClick={handleLogout}>
          <LogOut size={18}/> Log Out
        </div>
      </div>

      {/* Main Content Area */}
      <div className="main-content">
        <Routes>
          <Route path="/" element={user?.role === 'Employee' ? <EmployeeDashboard /> : <h1 style={{color: '#6a1b9a'}}>Welcome, {user?.name}!</h1>} />
          <Route path="/employees/*" element={<EmployeeManager />} />
          <Route path="/departments/*" element={<DepartmentManager />} />
          <Route path="/attendance/*" element={<AttendancePunch />} />
          <Route path="/leave/*" element={<LeaveManager />} />
          <Route path="/reports/*" element={<ReportGeneration />} />
          <Route path="/password" element={<h2 style={{color: '#6a1b9a'}}>Change Password Interface</h2>} />
          <Route path="/profile" element={<h2 style={{color: '#6a1b9a'}}>My Profile Interface</h2>} />
        </Routes>
      </div>
    </div>
  );
};

export default Dashboard;
