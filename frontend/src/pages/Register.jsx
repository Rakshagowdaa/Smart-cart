import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { register } from '../services/authService';

function Register() {
  const navigate = useNavigate();
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState('USER');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      console.log('FRONTEND DEBUG: Sending registration for', name, 'with role:', role);
      const res = await register(name, email, password, role);
      console.log('FRONTEND DEBUG: Server responded with user role:', res.data.user.role);
      localStorage.setItem('token', res.data.token);
      localStorage.setItem('user', JSON.stringify(res.data.user));
      
     
      if (res.data.user.role === 'ADMIN') {
        navigate('/admin');
      } else if (res.data.user.role === 'VENDOR') {
        navigate('/vendor');
      } else {
        navigate('/');
      }
    } catch (err) {
      if (err.response && err.response.data && err.response.data.message) {
        setError(err.response.data.message);
      } else {
        setError('Registration failed. This email may already be in use.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container mt-5 pt-4">
      <div className="row justify-content-center">
        <div className="col-12 col-sm-10 col-md-7 col-lg-5">
          <div className="card shadow-sm border-0 mt-4">
            <div className="card-body p-4 p-md-5">

              <h3 className="card-title mb-1 fw-bold text-center">Create Account</h3>
              <p className="text-muted text-center mb-4" style={{ fontSize: '0.9rem' }}>
                Join SmartCart and start shopping
              </p>

              {error && (
                <div className="alert alert-danger py-2" role="alert">
                  {error}
                </div>
              )}

              <form onSubmit={handleSubmit}>
                <div className="mb-3">
                  <label htmlFor="registerName" className="form-label fw-semibold">
                    Full Name
                  </label>
                  <input
                    type="text"
                    id="registerName"
                    className="form-control"
                    placeholder="John Doe"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    required
                  />
                </div>

                <div className="mb-3">
                  <label htmlFor="registerEmail" className="form-label fw-semibold">
                    Email Address
                  </label>
                  <input
                    type="email"
                    id="registerEmail"
                    className="form-control"
                    placeholder="you@example.com"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                  />
                </div>

                {/* Role selection removed, defaults to USER */}

                <div className="mb-4">
                  <label htmlFor="registerPassword" className="form-label fw-semibold">
                    Password
                  </label>
                  <div className="input-group">
                    <input
                      type={showPassword ? "text" : "password"}
                      id="registerPassword"
                      className="form-control"
                      placeholder="Choose a strong password"
                      value={password}
                      onChange={(e) => setPassword(e.target.value)}
                      required
                    />
                    <button 
                      type="button" 
                      className="btn btn-outline-secondary border-start-0" 
                      onClick={() => setShowPassword(!showPassword)}
                      title={showPassword ? "Hide password" : "Show password"}
                    >
                      {showPassword ? '🙈' : '👁️'}
                    </button>
                  </div>
                </div>

                <div className="d-grid">
                  <button
                    type="submit"
                    id="registerBtn"
                    className="btn btn-dark"
                    disabled={loading}
                  >
                    {loading ? 'Creating account...' : 'Create Account'}
                  </button>
                </div>
              </form>

              <hr className="my-4" />

              <p className="text-center mb-0" style={{ fontSize: '0.9rem' }}>
                Already have an account?{' '}
                <Link to="/login" className="text-dark fw-semibold">
                  Sign in here
                </Link>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Register;
