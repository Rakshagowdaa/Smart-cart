import React from 'react';
import { Link, useNavigate } from 'react-router-dom';

function Navbar() {
  const navigate = useNavigate();
  const token = localStorage.getItem('token');
  const userStr = localStorage.getItem('user');
  const user = userStr ? JSON.parse(userStr) : null;

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    navigate('/login');
  };

  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-dark fixed-top shadow-sm">
      <div className="container">
        <Link className="navbar-brand fw-bold d-flex align-items-center" to="/">
          <span className="fs-3 me-2">🛒</span> SmartCart
        </Link>
        <button
          className="navbar-toggler border-0"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarContent"
          aria-controls="navbarContent"
          aria-expanded="false"
          aria-label="Toggle navigation"
        >
          <span className="navbar-toggler-icon"></span>
        </button>

        <div className="collapse navbar-collapse" id="navbarContent">
          {/* Search Bar */}
          <form className="d-flex mx-auto col-lg-6 mt-3 mt-lg-0" onSubmit={(e) => e.preventDefault()}>
            <input
              className="form-control rounded-start-pill border-0 px-4 shadow-none"
              type="search"
              placeholder="Search products..."
              aria-label="Search"
            />
            <button className="btn btn-warning rounded-end-pill px-4 fw-semibold shadow-none" type="submit">
              Search
            </button>
          </form>

          {/* Right Links */}
          <ul className="navbar-nav ms-auto mb-2 mb-lg-0 align-items-lg-center">
            {/* Show shopping links ONLY to Customers or when not logged in */}
            {(!user || user.role === 'USER') && (
              <>
                <li className="nav-item">
                  <Link to="/wishlist" className="nav-link text-white" id="navWishlist">
                    ❤️ Wishlist
                  </Link>
                </li>
                <li className="nav-item">
                  <Link to="/cart" className="nav-link text-white" id="navCart">
                    🛍️ Cart
                  </Link>
                </li>
                <li className="nav-item">
                  <Link to="/orders" className="nav-link text-white" id="navOrders">
                    📦 Orders
                  </Link>
                </li>
              </>
            )}

            {token ? (
              <>
                {user && user.role === 'ADMIN' && (
                  <li className="nav-item">
                    <Link to="/admin" className="nav-link text-warning fw-semibold" id="navAdmin">
                      ⚙️ Admin Dashboard
                    </Link>
                  </li>
                )}
                {user && user.role === 'VENDOR' && (
                  <li className="nav-item">
                    <Link to="/vendor" className="nav-link text-info fw-semibold" id="navVendor">
                      🏪 Vendor Panel
                    </Link>
                  </li>
                )}
                <li className="nav-item ms-lg-2">
                  <div className="d-flex flex-column align-items-lg-end">
                    <span className="text-white small fw-bold">
                      {user ? user.name : 'User'}
                    </span>
                    <span className={`badge ${
                      user?.role === 'ADMIN' ? 'bg-danger' : 
                      user?.role === 'VENDOR' ? 'bg-info text-dark' : 'bg-success text-dark'
                    }`} style={{fontSize: '0.7rem'}}>
                      {user?.role || 'USER'}
                    </span>
                  </div>
                </li>
                <li className="nav-item ms-lg-2 mt-2 mt-lg-0">
                  <Link to="/profile" className="btn btn-outline-light btn-sm rounded-pill px-3 shadow-none me-2">
                    Profile
                  </Link>
                  <button
                    onClick={handleLogout}
                    className="btn btn-outline-warning btn-sm rounded-pill px-3 shadow-none"
                    id="btnLogout"
                  >
                    Logout
                  </button>
                </li>
              </>
            ) : (
              <>
                <li className="nav-item">
                  <Link to="/login" className="nav-link text-white" id="navLogin">Login</Link>
                </li>
                <li className="nav-item">
                  <Link to="/register" className="btn btn-warning btn-sm rounded-pill px-3 fw-semibold ms-lg-2 mt-2 mt-lg-0">
                    Register
                  </Link>
                </li>
              </>
            )}
          </ul>
        </div>
      </div>
    </nav>
  );
}

export default Navbar;
