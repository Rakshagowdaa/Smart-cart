import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { login } from '../services/authService';

function Login() {
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const res = await login(email, password);
      localStorage.setItem('token', res.data.token);
      localStorage.setItem('user', JSON.stringify(res.data.user));
      
      
      const role = res.data.user.role;
      if (role === 'ADMIN') {
        navigate('/admin');
      } else if (role === 'VENDOR') {
        navigate('/vendor');
      } else {
        navigate('/');
      }
    } catch (err) {
      setError('Invalid email or password. Please try again.');
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

              <h3 className="card-title mb-1 fw-bold text-center">Welcome Back</h3>
              <p className="text-muted text-center mb-4" style={{ fontSize: '0.9rem' }}>
                Sign in to your SmartCart account
              </p>

              {error && (
                <div className="alert alert-danger py-2" role="alert">
                  {error}
                </div>
              )}

              <form onSubmit={handleSubmit}>
                <div className="mb-3">
                  <label htmlFor="loginEmail" className="form-label fw-semibold">
                    Email Address
                  </label>
                  <input
                    type="email"
                    id="loginEmail"
                    className="form-control"
                    placeholder="you@example.com"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                  />
                </div>

                <div className="mb-4">
                  <label htmlFor="loginPassword" className="form-label fw-semibold">
                    Password
                  </label>
                  <div className="input-group">
                    <input
                      type={showPassword ? "text" : "password"}
                      id="loginPassword"
                      className="form-control"
                      placeholder="Enter your password"
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
                  <div className="text-end mt-1">
                    <Link to="/forgot-password" style={{ fontSize: '0.85rem' }} className="text-muted text-decoration-none hover-dark">
                      Forgot Password?
                    </Link>
                  </div>
                </div>

                <div className="d-grid">
                  <button
                    type="submit"
                    id="loginBtn"
                    className="btn btn-dark"
                    disabled={loading}
                  >
                    {loading ? 'Signing in...' : 'Sign In'}
                  </button>
                </div>
              </form>

              <hr className="my-4" />

              <p className="text-center mb-0" style={{ fontSize: '0.9rem' }}>
                Don't have an account?{' '}
                <Link to="/register" className="text-dark fw-semibold">
                  Register here
                </Link>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Login;
