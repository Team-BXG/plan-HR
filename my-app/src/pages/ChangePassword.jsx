import React, { useState } from 'react';
import axios from 'axios';
import { useAuth } from '../context/AuthContext';

const ChangePassword = () => {
  const { user } = useAuth();
  const [oldPassword, setOldPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');

  const handleChangePassword = async (e) => {
    e.preventDefault();
    if (newPassword !== confirmPassword) {
      alert("Passwords do not match");
      return;
    }
    
    try {
      await axios.post('http://localhost:8000/api/login/change-password/', {
          username: user.id || user.employee_id,
          old_password: oldPassword,
          new_password: newPassword
      });
      alert("Password successfully changed!");
      setOldPassword('');
      setNewPassword('');
      setConfirmPassword('');
    } catch (err) {
      alert("Failed to change password: " + (err.response?.data?.error || err.message));
    }
  };

  return (
    <div style={{ padding: '20px', backgroundColor: 'transparent' }}>
      <h3 style={{ borderBottom: '1px solid var(--green-200)', paddingBottom: '10px', color: 'var(--text-primary)', marginBottom: '20px' }}>Change Password</h3>
      
      <form onSubmit={handleChangePassword} style={{ display: 'flex', flexDirection: 'column', gap: '15px', maxWidth: '600px' }}>
        <input 
          className="input-field"
          style={{ padding: '10px', borderRadius: '4px', border: '1px solid #ccc' }}
          type="password" 
          placeholder="Enter Old Password" 
          value={oldPassword}
          onChange={(e) => setOldPassword(e.target.value)}
          required
        />
        <input 
          className="input-field"
          style={{ padding: '10px', borderRadius: '4px', border: '1px solid #ccc' }}
          type="password" 
          placeholder="Enter New Password (6 letters/numbers)" 
          value={newPassword}
          onChange={(e) => setNewPassword(e.target.value)}
          required
          minLength={6}
        />
        <input 
          className="input-field"
          style={{ padding: '10px', borderRadius: '4px', border: '1px solid #ccc' }}
          type="password" 
          placeholder="Confirm New Password" 
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
          required
        />
        <div style={{ marginTop: '10px' }}>
          <button type="submit" className="btn-primary" style={{ borderRadius: '20px', padding: '8px 20px', fontSize: '14px', width: 'fit-content' }}>
            Change Password
          </button>
        </div>
      </form>
    </div>
  );
};

export default ChangePassword;
