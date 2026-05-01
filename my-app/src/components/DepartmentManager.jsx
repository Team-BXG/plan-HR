import React, { useState } from 'react';

const DepartmentManager = () => {
    const isUpdate = window.location.pathname.includes('/departments/update');
    const isSearch = window.location.pathname.includes('/departments/search');
    const isRemove = window.location.pathname.includes('/departments/remove');
    const isAdd = window.location.pathname.includes('/departments/add');

    const [showSuccessModal, setShowSuccessModal] = useState(false);

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
                    <h3 style={{ color: 'var(--green-900)', marginTop: 0 }}>Update Department</h3>
                    <input className="input-field" placeholder="Enter Department ID" style={{ display: 'block', marginBottom: '10px', width: '400px' }} />
                    <input className="input-field" placeholder="New Department Name" style={{ display: 'block', marginBottom: '15px', width: '400px' }} />
                    <button className="btn-primary" style={{ padding: '6px 15px', borderRadius: '4px' }}>
                        Update Department
                    </button>
                </div>
            )}

            {/* Search/View Table (Screenshot #5) */}
            <div>
               {isSearch && (
                   <div style={{ marginBottom: '20px' }}>
                       <h4 style={{ color: 'var(--green-800)', margin: '0 0 10px 0', fontWeight: 'normal' }}>Search Departments</h4>
                       <div style={{ display: 'flex', gap: '10px' }}>
                          <input className="input-field" placeholder="Enter department name..." style={{ borderRadius: '20px', padding: '5px 15px' }} />
                          <button style={{ padding: '5px 15px', borderRadius: '20px', backgroundColor: 'var(--green-700)', color: 'white', border: 'none', cursor: 'pointer' }}>Search</button>
                       </div>
                   </div>
               )}

               <table style={{ width: '100%', textAlign: 'center', borderCollapse: 'collapse', fontSize: '13px' }}>
                   <thead>
                       <tr style={{ backgroundColor: 'var(--green-100)', color: 'var(--green-900)' }}>
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

            {/* Remove Department Modal Overlay (Screenshot #3) */}
            {isRemove && (
               <div style={{
                 position: 'absolute', top: 0, left: 0, right: 0, bottom: 0,
                 backgroundColor: 'rgba(255,255,255,0.6)', backdropFilter: 'blur(3px)',
                 display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 100
               }}>
                 <div style={{
                   width: '350px', backgroundColor: 'var(--green-100)', borderRadius: '10px', padding: '15px',
                   boxShadow: '0 10px 30px rgba(0,0,0,0.3)', border: '1px solid var(--green-300)'
                 }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid var(--green-300)', paddingBottom: '5px' }}>
                       <span style={{ fontSize: '13px', color: 'var(--green-800)' }}>Remove Department</span>
                       <span style={{ cursor: 'pointer' }} onClick={() => window.history.back()}>X</span>
                    </div>
                    
                    <div style={{ marginTop: '20px', display: 'flex', alignItems: 'center', gap: '10px' }}>
                       <div style={{ fontWeight: 'bold', fontSize: '13px', color: '#444', flex: 1 }}>Enter Department Name to Remove:</div>
                       <div style={{ width: '25px', height: '25px', borderRadius: '50%', backgroundColor: 'var(--green-600)', color: 'white', display: 'flex', justifyContent: 'center', alignItems: 'center', fontWeight: 'bold' }}>?</div>
                    </div>
                    
                    <input className="input-field" style={{ width: '100%', marginTop: '15px' }} />
                    
                    <div style={{ display: 'flex', justifyContent: 'center', gap: '10px', marginTop: '20px' }}>
                       <button onClick={() => window.history.back()} style={{ padding: '5px 20px', backgroundColor: 'var(--green-500)', color: 'white', border: '1px solid var(--green-400)', borderRadius: '4px', cursor: 'pointer' }}>OK</button>
                       <button onClick={() => window.history.back()} style={{ padding: '5px 15px', backgroundColor: '#e0e0e0', border: '1px solid #ccc', borderRadius: '4px', cursor: 'pointer' }}>Cancel</button>
                    </div>
                 </div>
               </div>
            )}

            {/* Add Department Modal Overlay */}
            {isAdd && (
               <div style={{
                 position: 'absolute', top: 0, left: 0, right: 0, bottom: 0,
                 backgroundColor: 'rgba(255,255,255,0.6)', backdropFilter: 'blur(3px)',
                 display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 100
               }}>
                 <div style={{
                   width: '350px', backgroundColor: 'var(--green-100)', borderRadius: '10px', padding: '15px',
                   boxShadow: '0 10px 30px rgba(0,0,0,0.3)', border: '1px solid var(--green-300)'
                 }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid var(--green-300)', paddingBottom: '5px' }}>
                       <span style={{ fontSize: '13px', color: 'var(--green-800)' }}>Add Department</span>
                       <span style={{ cursor: 'pointer' }} onClick={() => window.history.back()}>X</span>
                    </div>
                    
                    <div style={{ marginTop: '20px', display: 'flex', alignItems: 'center', gap: '10px' }}>
                       <div style={{ fontWeight: 'bold', fontSize: '13px', color: '#444', flex: 1 }}>Enter Department Name to Add:</div>
                       <div style={{ width: '25px', height: '25px', borderRadius: '50%', backgroundColor: 'var(--green-600)', color: 'white', display: 'flex', justifyContent: 'center', alignItems: 'center', fontWeight: 'bold' }}>+</div>
                    </div>
                    
                    <input className="input-field" style={{ width: '100%', marginTop: '15px' }} />
                    
                    <div style={{ display: 'flex', justifyContent: 'center', gap: '10px', marginTop: '20px' }}>
                       <button onClick={() => setShowSuccessModal(true)} style={{ padding: '5px 20px', backgroundColor: 'var(--green-500)', color: 'white', border: '1px solid var(--green-400)', borderRadius: '4px', cursor: 'pointer' }}>OK</button>
                       <button onClick={() => window.history.back()} style={{ padding: '5px 15px', backgroundColor: '#e0e0e0', border: '1px solid #ccc', borderRadius: '4px', cursor: 'pointer' }}>Cancel</button>
                    </div>
                 </div>
               </div>
            )}

            {/* Success Modal */}
            {showSuccessModal && (
               <div style={{
                 position: 'absolute', top: 0, left: 0, right: 0, bottom: 0,
                 backgroundColor: 'rgba(255,255,255,0.6)', backdropFilter: 'blur(3px)',
                 display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 1000
               }}>
                 <div style={{
                   width: '400px', backgroundColor: '#f0f4f8', borderRadius: '8px', padding: '15px',
                   boxShadow: '0 4px 15px rgba(0,0,0,0.2)', border: '1px solid var(--green-400)'
                 }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid white', paddingBottom: '10px' }}>
                       <h4 style={{ margin: 0, color: 'var(--green-800)', display: 'flex', alignItems: 'center', gap:'10px' }}>
                          <span style={{color: 'green'}}>📘</span> Success
                       </h4>
                       <div style={{ cursor: 'pointer' }} onClick={() => { setShowSuccessModal(false); window.history.back(); }}>✕</div>
                    </div>
                    
                    <div style={{ margin: '20px 0', fontSize: '14px', color: 'var(--text-primary)' }}>
                       <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                          <span>Department Added</span>
                          <div style={{ background: '#5abed6', color: 'white', borderRadius: '50%', width: '30px', height: '30px', display: 'flex', justifyContent: 'center', alignItems: 'center', fontWeight: 'bold' }}>i</div>
                       </div>
                       The department 'Cleaning' was successfully added.
                    </div>

                    <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: '20px', gap: '10px' }}>
                       <button onClick={() => { setShowSuccessModal(false); window.history.back(); }} style={{ padding: '5px 20px', borderRadius: '4px', border: '1px solid #5abed6', background: '#5abed6', color: 'white', cursor: 'pointer' }}>OK</button>
                    </div>
                 </div>
               </div>
            )}
        </div>
    );
};
export default DepartmentManager;
