import React, { useState } from 'react';
import { Routes, Route, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { LogOut, Users, Building, ShieldCheck, Clock, Calendar, Key, UserPlus, Search, FileText, UserX, FileSpreadsheet, PlusCircle, MinusCircle, Edit, FilePlus } from 'lucide-react';
import EmployeeManager from '../components/EmployeeManager';
import DepartmentManager from '../components/DepartmentManager';
import AttendancePunch from '../components/AttendancePunch';
import LeaveManager from '../components/LeaveManager';
import EmployeeDashboard from '../components/EmployeeDashboard';
import ReportGeneration from '../components/ReportGeneration';
import ChangePassword from './ChangePassword';
import ManageInactive from '../components/ManageInactive';
import GenerateAttendance from '../components/GenerateAttendance';
import logo from '../assets/logo.webp';

const Dashboard = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const categoryStyle = {
    fontWeight: 'bold',
    color: 'var(--green-300)',
    fontSize: '15px',
    padding: '10px 0 5px 5px',
    display: 'flex',
    alignItems: 'center',
    gap: '8px'
  };

  return (
    <div className="app-container">
      {/* Sidebar */}
      <div className="sidebar" style={{ width: '280px', padding: '15px', backgroundColor: 'var(--green-700)' }}>
        <div style={{ marginBottom: '20px', textAlign: 'center' }}>
          <img src={logo} alt="Company Logo" style={{ maxWidth: '60px', height: '60px', objectFit: 'cover', borderRadius: '50%' }} />
        </div>
        <div style={{ paddingBottom: '15px', borderBottom: '1px solid var(--green-600)' }}>
          <h2 style={{ margin: 0, color: 'var(--green-100)', display: 'flex', alignItems: 'center', gap: '10px', fontSize: '18px' }}>
            <ShieldCheck /> {user?.role === 'Admin' ? 'Admin Dashboard' : user?.role === 'HR Manager' ? 'HR Manager Dashboard' : 'Employee Dashboard'}
          </h2>
        </div>
        
        <div style={{ flex: 1, overflowY: 'auto' }}>
          {/* Change Password - All roles */}
          <div style={categoryStyle}><Key size={18}/> Change Password</div>
          <div className="sidebar-sub">
            <div className="nav-item-custom" onClick={() => navigate('/password')}>Change Password</div>
          </div>

          {/* Admin only */}
          {user?.role === 'Admin' && (
            <>
              <div style={categoryStyle}><Users size={18}/> Employee Management</div>
              <div className="sidebar-sub">
                <div className="nav-item-custom" onClick={() => navigate('/employees/view')}><Users size={14}/> View Employees</div>
                <div className="nav-item-custom" onClick={() => navigate('/employees/add')}><UserPlus size={14}/> Add Employee</div>
                <div className="nav-item-custom" onClick={() => navigate('/employees/search')}><Search size={14}/> Search Employees</div>
                <div className="nav-item-custom" onClick={() => navigate('/leave/generate')}><FilePlus size={14}/> Generate Leave</div>
                <div className="nav-item-custom" onClick={() => navigate('/employees/inactive')}><UserX size={14}/> Manage Inactive</div>
                <div className="nav-item-custom" onClick={() => navigate('/attendance/generate')}><FileSpreadsheet size={14}/> Generate Attendance</div>
                <div className="nav-item-custom" onClick={() => navigate('/leave/register')}><FilePlus size={14}/> Register Leave</div>
                <div className="nav-item-custom" onClick={() => navigate('/reports')}><FileSpreadsheet size={14}/> Generate Reports</div>
                <div className="nav-item-custom" onClick={() => navigate('/attendance/punch')}><Clock size={14}/> Punch In</div>
              </div>

              <div style={categoryStyle}><Building size={18}/> Department Management</div>
              <div className="sidebar-sub">
                <div className="nav-item-custom" onClick={() => navigate('/departments/view')}><Building size={14}/> View Departments</div>
                <div className="nav-item-custom" onClick={() => navigate('/departments/add')}><PlusCircle size={14}/> Add Department</div>
                <div className="nav-item-custom" onClick={() => navigate('/departments/remove')}><MinusCircle size={14}/> Remove Department</div>
                <div className="nav-item-custom" onClick={() => navigate('/departments/search')}><Search size={14}/> Search Department</div>
                <div className="nav-item-custom" onClick={() => navigate('/departments/update')}><Edit size={14}/> Update Department</div>
              </div>
            </>
          )}

          {/* HR Manager only */}
          {user?.role === 'HR Manager' && (
            <>
              <div style={categoryStyle}><Users size={18}/> Employee Management</div>
              <div className="sidebar-sub">
                <div className="nav-item-custom" onClick={() => navigate('/employees/view')}><Users size={14}/> View Employees</div>
                <div className="nav-item-custom" onClick={() => navigate('/employees/add')}><UserPlus size={14}/> Add Employee</div>
                <div className="nav-item-custom" onClick={() => navigate('/employees/search')}><Search size={14}/> Search Employees</div>
                <div className="nav-item-custom" onClick={() => navigate('/leave/generate')}><FilePlus size={14}/> Generate Leave</div>
                <div className="nav-item-custom" onClick={() => navigate('/employees/inactive')}><UserX size={14}/> Manage Inactive</div>
                <div className="nav-item-custom" onClick={() => navigate('/attendance/generate')}><FileSpreadsheet size={14}/> Generate Attendance</div>
                <div className="nav-item-custom" onClick={() => navigate('/leave/register')}><FilePlus size={14}/> Register Leave</div>
                <div className="nav-item-custom" onClick={() => navigate('/reports')}><FileSpreadsheet size={14}/> Generate Reports</div>
                <div className="nav-item-custom" onClick={() => navigate('/attendance/punch')}><Clock size={14}/> Punch In</div>
              </div>
            </>
          )}

          {/* Employee only */}
          {user?.role === 'Employee' && (
            <>
              <div style={categoryStyle}><Users size={18}/> Employee Actions</div>
              <div className="sidebar-sub">
                <div className="nav-item-custom" onClick={() => navigate('/')}><Clock size={14}/> Dashboard</div>
                <div className="nav-item-custom" onClick={() => navigate('/attendance/punch')}><Clock size={14}/> Punch In</div>
                <div className="nav-item-custom" onClick={() => navigate('/profile')}><UserPlus size={14}/> View My Profile</div>
              </div>
            </>
          )}

          {/* Common for all */}
          <div style={categoryStyle}><LogOut size={18}/> Logout</div>
          <div className="sidebar-sub">
            <div className="nav-item-custom" onClick={handleLogout}><LogOut size={14}/> Logout</div>
          </div>
        </div>
      </div>

      {/* Main Content */}
      <div className="main-content" style={{ flex: 1, height: '100vh', overflowY: 'auto', backgroundColor: 'var(--green-100)' }}>
        <Routes>
          <Route path="/" element={<EmployeeDashboard />} />
          <Route path="/profile" element={<EmployeeDashboard />} />
          <Route path="/employees/inactive" element={<ManageInactive />} />
          <Route path="/employees/*" element={<EmployeeManager />} />
          <Route path="/departments/*" element={<DepartmentManager />} />
          <Route path="/attendance/generate" element={<GenerateAttendance />} />
          <Route path="/attendance/*" element={<AttendancePunch />} />
          <Route path="/leave/*" element={<LeaveManager />} />
          <Route path="/reports" element={<ReportGeneration />} />
          <Route path="/password" element={<ChangePassword />} />
        </Routes>
      </div>
    </div>
  );
};

export default Dashboard;
