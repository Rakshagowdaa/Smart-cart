import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getCart, removeFromCart, clearCart } from '../services/cartService';
import { placeOrder } from '../services/orderService';

function Cart() {
  const navigate = useNavigate();
  const [cartData, setCartData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [address, setAddress] = useState('');
  const [placing, setPlacing] = useState(false);
  const [orderSuccess, setOrderSuccess] = useState(false);

  const userStr = localStorage.getItem('user');
  const user = userStr ? JSON.parse(userStr) : null;

  useEffect(() => {
    if (!user) {
      navigate('/login');
      return;
    }
    fetchCart();
  }, []);

  const fetchCart = async () => {
    setLoading(true);
    setError('');
    try {
      const res = await getCart(user.id);
      setCartData(res.data);
    } catch (err) {
      setError('Could not load cart.');
    } finally {
      setLoading(false);
    }
  };

  const handleRemove = async (productId) => {
    try {
      const res = await removeFromCart(user.id, productId);
      setCartData(res.data);
    } catch (err) {
      alert('Failed to remove item.');
    }
  };

  const handlePlaceOrder = () => {
    if (!address.trim()) {
      alert('Please enter a delivery address.');
      return;
    }
    navigate('/checkout', { state: { address, total, cartItems } });
  };

  const cartItems = cartData?.items || [];
  const total = cartData?.totalPrice || cartItems.reduce((acc, item) => acc + (item.price * item.quantity), 0);

  if (loading) {
    return (
      <div className="container mt-5 pt-4 text-center">
        <div className="spinner-border text-dark mt-5" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    );
  }

  // Order Success Screen
  if (orderSuccess) {
    return (
      <div className="container mt-5 pt-4">
        <div className="row justify-content-center">
          <div className="col-12 col-md-7">
            <div className="card border-0 shadow-sm text-center p-5" id="orderSuccessCard">
              <div style={{ fontSize: '3rem' }}>✅</div>
              <h3 className="fw-bold mt-3 mb-2">Order Placed Successfully!</h3>
              <p className="text-muted mb-4">
                Thank you for your order. You will receive a notification shortly with updates on your delivery.
              </p>
              <div className="alert alert-info py-2 mb-4" style={{ fontSize: '0.9rem' }}>
                📬 A confirmation notification has been sent via our messaging system. Track your order status on the Orders page.
              </div>
              <div className="d-flex justify-content-center gap-3">
                <Link to="/orders" className="btn btn-dark" id="viewOrdersBtn">View My Orders</Link>
                <Link to="/" className="btn btn-outline-dark" id="continueShoppingBtn">Continue Shopping</Link>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="container mt-5 pt-4">
      <div className="mb-5 border-bottom pb-3">
        <h2 className="fw-bold display-6 mb-0">Shopping Cart</h2>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      {cartItems.length === 0 ? (
        <div className="card border shadow-sm text-center p-5">
          <p className="text-muted fs-5 mb-3">Your cart is empty.</p>
          <Link to="/" className="btn btn-dark" id="emptyCartShop">Start Shopping</Link>
        </div>
      ) : (
        <div className="row g-4">
          {/* Cart Items */}
          <div className="col-12 col-lg-8">
            {cartItems.map((item) => (
              <div key={item.id} className="card mb-3 border shadow-sm">
                <div className="card-body">
                  <div className="d-flex align-items-center gap-3">
                    <div className="flex-grow-1">
                      <h6 className="fw-semibold mb-1">{item.productName}</h6>
                      <p className="text-muted mb-1 small">Product ID: {item.productId}</p>
                      <div className="d-flex align-items-center gap-3">
                        <span className="fw-bold">₹{item.price?.toFixed(2)}</span>
                        <span className="badge bg-light text-dark border">Qty: {item.quantity}</span>
                        <span className="text-muted small">Subtotal: ₹{(item.price * item.quantity).toFixed(2)}</span>
                      </div>
                    </div>
                    <button
                      className="btn btn-sm btn-outline-danger"
                      id={`removeItem-${item.productId}`}
                      onClick={() => handleRemove(item.productId)}
                    >
                      Remove
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>

          {/* Order Summary */}
          <div className="col-12 col-lg-4">
            <div className="card border shadow-sm">
              <div className="card-body p-4">
                <h5 className="fw-bold mb-3">Order Summary</h5>

                <div className="d-flex justify-content-between mb-2">
                  <span className="text-muted">Items ({cartItems.length})</span>
                  <span>₹{total.toFixed(2)}</span>
                </div>
                <hr />
                <div className="d-flex justify-content-between mb-3 fw-bold fs-5">
                  <span>Total</span>
                  <span>₹{total.toFixed(2)}</span>
                </div>


                {/* Delivery Address */}
                <div className="mb-3">
                  <label htmlFor="addressInput" className="form-label small fw-semibold">
                    Delivery Address <span className="text-danger">*</span>
                  </label>
                  <textarea
                    id="addressInput"
                    className="form-control form-control-sm"
                    rows={2}
                    placeholder="Enter your delivery address..."
                    value={address}
                    onChange={(e) => setAddress(e.target.value)}
                    required
                  />
                </div>

                <div className="d-grid">
                  <button
                    className="btn btn-primary py-2 fw-bold"
                    id="placeOrderBtn"
                    onClick={handlePlaceOrder}
                    disabled={placing}
                  >
                    {placing ? 'Processing...' : 'Proceed to Checkout'}
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default Cart;
