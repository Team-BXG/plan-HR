import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import logo from '../assets/logo.webp';

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch('http://localhost:8000/api/login/login/', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
      });
      
      const data = await response.json();
      if (!response.ok) {
        throw new Error(data.error || 'Login failed');
      }

      localStorage.setItem('token', data.token);
      localStorage.setItem('user', JSON.stringify(data.user));
      window.location.href = '/'; 
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
      <div className="glass-panel" style={{ padding: '40px', width: '340px', textAlign: 'center' }}>
        <div style={{ marginBottom: '20px' }}>
          <img src={logo} alt="Company Logo" style={{ maxWidth: '80px', height: '80px', objectFit: 'cover', borderRadius: '50%' }} />
        </div>
        <h1 style={{ color: 'var(--green-100)', textShadow: '2px 2px 4px rgba(0,50,0,0.5)', fontFamily: 'Verdana', marginTop: '0' }}>Welcome to HRX</h1>
        <form onSubmit={handleLogin} style={{ display: 'flex', flexDirection: 'column', gap: '20px', marginTop: '30px' }}>
          <input 
            type="text" 
            placeholder="Employee ID" 
            className="input-field"
            value={username}
            onChange={e => setUsername(e.target.value)}
            required
          />
          <input 
            type="password" 
            placeholder="Password" 
            className="input-field"
            value={password}
            onChange={e => setPassword(e.target.value)}
            required
          />
          {error && <p style={{ color: '#ffaaaa', fontSize: '14px', margin: 0 }}>{error}</p>}
          <button type="submit" className="btn-primary" style={{ marginTop: '10px' }}>Login</button>
        </form>
      </div>
    </div>
  );
};

export default Login;
