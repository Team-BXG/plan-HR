import React from 'react';

const DepartmentManager = () => {
    const isUpdate = window.location.pathname.includes('/departments/update');
    const isSearch = window.location.pathname.includes('/departments/search');
    const isRemove = window.location.pathname.includes('/departments/remove');

    const departments = [
        { id: 2, name: 'Management', count: 7 },
        { id: 4, name: 'IT', count: 6 },
        { id: 9, name: 'Marketing', count: 1 },
        { id: 10, name: 'Finance', count: 1 }
    ];

    return (
        <div style={{ padding: '20px', height: '100%', position: 'relative' }}>
            
            {/* Update Department Layout (Screenshot #4) */}
            {isUpdate && (
                <div style={{ maxWidth: '600px' }}>
                    <h3 style={{ color: '#4a148c', marginTop: 0 }}>Update Department</h3>
                    <input className="glass-input" placeholder="Enter Department ID" style={{ display: 'block', marginBottom: '10px', width: '400px' }} />
                    <input className="glass-input" placeholder="New Department Name" style={{ display: 'block', marginBottom: '15px', width: '400px' }} />
                    <button style={{ padding: '6px 15px', backgroundColor: '#6a1b9a', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                        Update Department
                    </button>
                </div>
            )}

            {/* Search/View Table (Screenshot #5) */}
            {(isSearch || isRemove || (!isUpdate)) && (
                <div>
                   {isSearch && (
                       <div style={{ marginBottom: '20px' }}>
                           <h4 style={{ color: '#666', margin: '0 0 10px 0', fontWeight: 'normal' }}>Search Departments</h4>
                           <div style={{ display: 'flex', gap: '10px' }}>
                              <input className="glass-input" placeholder="Enter department name..." style={{ borderRadius: '20px', padding: '5px 15px' }} />
                              <button style={{ padding: '5px 15px', borderRadius: '20px', backgroundColor: '#9c27b0', color: 'white', border: 'none', cursor: 'pointer' }}>Search</button>
                           </div>
                       </div>
                   )}

                   <table style={{ width: '100%', textAlign: 'center', borderCollapse: 'collapse', fontSize: '13px' }}>
                       <thead>
                           <tr style={{ backgroundColor: '#eeeeee', color: '#333' }}>
                               <th style={{ padding: '8px' }}>ID</th>
                               <th>Name</th>
                               <th>Employees</th>
                           </tr>
                       </thead>
                       <tbody>
                           {departments.map(d => (
                               <tr key={d.id} style={{ borderBottom: '1px solid #f0f0f0' }}>
                                   <td style={{ padding: '10px 8px' }}>{d.id}</td>
                                   <td>{d.name}</td>
                                   <td>{d.count}</td>
                               </tr>
                           ))}
                       </tbody>
                   </table>
                </div>
            )}

            {/* Remove Department Modal Overlay (Screenshot #3) */}
            {isRemove && (
               <div style={{
                 position: 'absolute', top: 0, left: 0, right: 0, bottom: 0,
                 display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 100
               }}>
                 <div style={{
                   width: '350px', backgroundColor: '#f3e5f5', borderRadius: '10px', padding: '15px',
                   boxShadow: '0 10px 30px rgba(0,0,0,0.3)', border: '1px solid #dba6e5'
                 }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid #dba6e5', paddingBottom: '5px' }}>
                       <span style={{ fontSize: '13px', color: '#6a1b9a' }}>Remove Department</span>
                       <span style={{ cursor: 'pointer' }} onClick={() => window.history.back()}>X</span>
                    </div>
                    
                    <div style={{ marginTop: '20px', display: 'flex', alignItems: 'center', gap: '10px' }}>
                       <div style={{ fontWeight: 'bold', fontSize: '13px', color: '#444', flex: 1 }}>Enter Department Name to Remove:</div>
                       <div style={{ width: '25px', height: '25px', borderRadius: '50%', backgroundColor: '#1976d2', color: 'white', display: 'flex', justifyContent: 'center', alignItems: 'center', fontWeight: 'bold' }}>?</div>
                    </div>
                    
                    <input className="glass-input" style={{ width: '100%', marginTop: '15px', border: '2px solid #29b6f6',  padding: '6px' }} />
                    
                    <div style={{ display: 'flex', justifyContent: 'center', gap: '10px', marginTop: '20px' }}>
                       <button style={{ padding: '5px 20px', backgroundColor: '#81d4fa', border: '1px solid #b3e5fc', borderRadius: '4px', cursor: 'pointer' }}>OK</button>
                       <button onClick={() => window.history.back()} style={{ padding: '5px 15px', backgroundColor: '#e0e0e0', border: '1px solid #ccc', borderRadius: '4px', cursor: 'pointer' }}>Cancel</button>
                    </div>
                 </div>
               </div>
            )}
        </div>
    );
};
export default DepartmentManager;
