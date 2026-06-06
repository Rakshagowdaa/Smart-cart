import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getOrdersByUser } from '../services/orderService';

const STATUS_BADGES = {
  CREATED: 'bg-secondary',
  PROCESSING: 'bg-warning text-dark',
  SHIPPED: 'bg-info text-dark',
  DELIVERED: 'bg-success',
};

function Orders() {
  const navigate = useNavigate();
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const userStr = localStorage.getItem('user');
  const user = userStr ? JSON.parse(userStr) : null;

  useEffect(() => {
    if (!user) {
      navigate('/login');
      return;
    }
    fetchOrders();
  }, []);

  const fetchOrders = async () => {
    setLoading(true);
    setError('');
    try {
      const res = await getOrdersByUser(user.id);
      setOrders(res.data);
    } catch (err) {
      setError('Could not load orders.');
    } finally {
      setLoading(false);
    }
  };

  const getStatusBadge = (status) => {
    const cls = STATUS_BADGES[status] || 'bg-secondary';
    return <span className={`badge ${cls}`}>{status}</span>;
  };

  const formatDate = (dateStr) => {
    if (!dateStr) return 'N/A';
    return new Date(dateStr).toLocaleDateString('en-IN', {
      year: 'numeric', month: 'short', day: 'numeric',
    });
  };

  const getTrackingSteps = (status) => {
    const steps = ['CREATED', 'PROCESSING', 'SHIPPED', 'DELIVERED'];
    let currentIndex = steps.indexOf(status);
    if (currentIndex === -1) currentIndex = 0;

    return steps.map((step, index) => {
      let stepClass = "step";
      if (index < currentIndex) stepClass += " completed";
      if (index === currentIndex) stepClass += " active";
      
      return (
        <div key={step} className={stepClass}>
          <div className="step-icon"></div>
          <div className="step-label">{step}</div>
        </div>
      );
    });
  };

  return (
    <div className="container mt-5 pt-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="fw-bold display-6 mb-1">My Orders</h2>
          <p className="text-muted mb-0 fs-5">Track all your orders here</p>
        </div>
        <button className="btn btn-outline-dark btn-sm" onClick={fetchOrders}>
          🔄 Refresh
        </button>
      </div>

      {loading && (
        <div className="text-center py-5">
          <div className="spinner-border text-dark" role="status">
            <span className="visually-hidden">Loading...</span>
          </div>
        </div>
      )}

      {error && !loading && (
        <div className="alert alert-warning">{error}</div>
      )}

      {!loading && !error && orders.length === 0 && (
        <div className="card border shadow-sm text-center p-5">
          <p className="text-muted fs-5 mb-3">You haven't placed any orders yet.</p>
          <button className="btn btn-dark" onClick={() => navigate('/')}>Start Shopping</button>
        </div>
      )}

      {!loading && !error && orders.length > 0 && (
        <>
          {/* Async status note */}
          <div className="alert alert-info py-2 mb-4" style={{ fontSize: '0.88rem' }}>
            📬 Order statuses are updated asynchronously by the backend. Click <strong>Refresh</strong> to see the latest status.
          </div>

          <div className="row g-3">
            {orders.map((order) => (
              <div key={order.id} className="col-12">
                <div className="card border shadow-sm sc-order-card">
                  <div className="card-body">
                    <div className="row align-items-center">
                      <div className="col-12 col-md-3 mb-2 mb-md-0">
                        <p className="mb-0 text-muted small">Order ID</p>
                        <p className="fw-bold mb-0">#{order.id}</p>
                      </div>
                      <div className="col-6 col-md-2 mb-2 mb-md-0">
                        <p className="mb-0 text-muted small">Total</p>
                        <p className="fw-bold mb-0 text-dark">
                          ₹{order.totalAmount?.toFixed(2) ?? order.total?.toFixed(2) ?? '—'}
                        </p>
                      </div>
                      <div className="col-6 col-md-2 mb-2 mb-md-0">
                        <p className="mb-0 text-muted small">Date</p>
                        <p className="mb-0">{formatDate(order.createdAt || order.orderDate)}</p>
                      </div>
                      <div className="col-6 col-md-2 mb-2 mb-md-0">
                        <p className="mb-0 text-muted small">Status</p>
                        <div className="mt-1">{getStatusBadge(order.status)}</div>
                      </div>
                      <div className="col-6 col-md-3">
                        <p className="mb-0 text-muted small">Delivery Address</p>
                        <p className="mb-0 small">{order.deliveryAddress || order.address || '—'}</p>
                      </div>
                    </div>

                    {/* Order Items if available */}
                    {order.items && order.items.length > 0 && (
                      <div className="mt-3 pt-2 border-top">
                        <p className="mb-1 text-muted small fw-semibold">Items:</p>
                        {order.items.map((item, idx) => (
                          <span key={idx} className="badge bg-light text-dark border me-1 mb-1">
                            {item.productName || `Product #${item.productId}`} × {item.quantity}
                          </span>
                        ))}
                      </div>
                    )}
                    
                    {/* Visual Tracking Timeline */}
                    <div className="mt-4 pt-3 border-top">
                      <p className="mb-3 text-muted small fw-semibold">Order Tracking:</p>
                      <div className="tracking-container">
                        {getTrackingSteps(order.status || 'CREATED')}
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </>
      )}

      <style>{`
        .tracking-container {
          display: flex;
          justify-content: space-between;
          position: relative;
          margin-bottom: 20px;
          padding: 0 10px;
        }
        .tracking-container::before {
          content: '';
          position: absolute;
          top: 10px;
          left: 40px;
          right: 40px;
          height: 3px;
          background-color: #e9ecef;
          z-index: 1;
        }
        .step {
          text-align: center;
          position: relative;
          z-index: 2;
          width: 80px;
        }
        .step-icon {
          width: 24px;
          height: 24px;
          border-radius: 50%;
          background-color: #e9ecef;
          margin: 0 auto 8px auto;
          border: 3px solid #fff;
          box-shadow: 0 0 0 2px #e9ecef;
        }
        .step-label {
          font-size: 11px;
          font-weight: bold;
          color: #6c757d;
        }
        
        .step.completed .step-icon {
          background-color: #198754;
          box-shadow: 0 0 0 2px #198754;
        }
        .step.completed .step-label {
          color: #198754;
        }
        
        .step.active .step-icon {
          background-color: #0d6efd;
          box-shadow: 0 0 0 2px #0d6efd;
          animation: pulse 1.5s infinite;
        }
        .step.active .step-label {
          color: #0d6efd;
        }

        @keyframes pulse {
          0% { transform: scale(1); }
          50% { transform: scale(1.2); }
          100% { transform: scale(1); }
        }
      `}</style>
    </div>
  );
}

export default Orders;
