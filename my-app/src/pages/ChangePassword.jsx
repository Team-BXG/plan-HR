import React, { useState } from 'react';

const ChangePassword = () => {
  const [oldPassword, setOldPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');

  const handleChangePassword = (e) => {
    e.preventDefault();
    if (newPassword !== confirmPassword) {
      alert("Passwords do not match");
      return;
    }
    alert("Password successfully changed!");
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
