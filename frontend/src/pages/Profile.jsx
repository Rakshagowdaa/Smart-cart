import React, { useState, useEffect } from 'react';
import { getUserProfile, updateProfile } from '../services/userService';

function Profile() {
  const [user, setUser] = useState(null);
  const [name, setName] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');
  const [message, setMessage] = useState('');
  const [loading, setLoading] = useState(true);

  const localUser = JSON.parse(localStorage.getItem('user'));

  useEffect(() => {
    if (localUser) {
      getUserProfile(localUser.id)
        .then(res => {
          setUser(res.data);
          setName(res.data.name || '');
          setPhoneNumber(res.data.phoneNumber || '');
          setLoading(false);
        })
        .catch(err => {
          console.error("Failed to load profile", err);
          setLoading(false);
        });
    }
  }, []);

  const handleUpdate = async (e) => {
    e.preventDefault();
    setMessage('');
    try {
      const res = await updateProfile(localUser.id, name, phoneNumber);
      setUser(res.data);
      setMessage('Profile updated successfully!');
      
      // Update local storage user data
      const updatedLocalUser = { ...localUser, name: res.data.name, phoneNumber: res.data.phoneNumber };
      localStorage.setItem('user', JSON.stringify(updatedLocalUser));
      
      setTimeout(() => setMessage(''), 3000);
    } catch (err) {
      setMessage('Failed to update profile.');
    }
  };

  if (loading) return <div className="text-center mt-5">Loading...</div>;

  return (
    <div className="container mt-5 pt-4">
      <div className="row justify-content-center">
        <div className="col-md-6">
          <div className="card shadow-sm p-4">
            <h3 className="fw-bold mb-4 text-center">My Profile</h3>
            {message && (
              <div className={`alert ${message.includes('success') ? 'alert-success' : 'alert-danger'} py-2`}>
                {message}
              </div>
            )}
            <form onSubmit={handleUpdate}>
              <div className="mb-3">
                <label className="form-label fw-bold">Email</label>
                <input type="email" className="form-control bg-light" value={user?.email || ''} disabled />
                <div className="form-text">Email cannot be changed.</div>
              </div>
              <div className="mb-3">
                <label className="form-label fw-bold">Role</label>
                <input type="text" className="form-control bg-light" value={user?.role || ''} disabled />
              </div>
              <div className="mb-3">
                <label className="form-label fw-bold">Full Name</label>
                <input
                  type="text"
                  className="form-control"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  required
                />
              </div>
              <div className="mb-4">
                <label className="form-label fw-bold">Phone Number</label>
                <input
                  type="tel"
                  className="form-control"
                  value={phoneNumber}
                  onChange={(e) => setPhoneNumber(e.target.value)}
                  placeholder="e.g. +91 9876543210"
                />
              </div>
              <button type="submit" className="btn btn-dark w-100 fw-bold">
                Update Profile
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Profile;
