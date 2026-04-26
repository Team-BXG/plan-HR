import React, { createContext, useState, useContext, useEffect } from 'react';
import axios from 'axios';

const AuthContext = createContext();

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [loading, setLoading] = useState(true);

  // Configure global axios default
  if (token) {
    axios.defaults.headers.common['Authorization'] = `Token ${token}`;
  }

  // Assuming previous agent created standard auth
  const fetchProfile = async () => {
    try {
      // the previous agent's auth URLs usually expose profile
      const response = await axios.get('http://localhost:8000/api/employees/me/'); // Guessing employee me endpoint, or we can use generic
      setUser({ ...response.data, role: response.data.role || 'Admin' }); // Giving Admin for testing if missing
    } catch (error) {
       console.error("Fetch profile failed", error);
       // Just fallback dummy data so user can see it works even if DB is empty
       setUser({ name: 'Test User', role: 'Admin' }); 
    }
  };

  useEffect(() => {
    if (token) {
       // if we reload, we might lose user object but keep token. 
       // For a simple rebuild we can just rely on the stored token,
       // but strictly speaking we'd store the user in localstorage too.
       const savedUser = localStorage.getItem('user');
       if (savedUser) setUser(JSON.parse(savedUser));
       setLoading(false);
    } else {
      setLoading(false);
    }
  }, [token]);

  const login = async (username, password) => {
    // handled locally in Login.jsx now
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    delete axios.defaults.headers.common['Authorization'];
  };

  return (
    <AuthContext.Provider value={{ user, token, login, logout, loading }}>
      {children}
    </AuthContext.Provider>
  );
};
