import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate, Link } from 'react-router-dom';
import { placeOrder, updateOrderStatus } from '../services/orderService';
import { clearCart } from '../services/cartService';
import { createRazorpayOrder, verifyRazorpaySignature } from '../services/paymentService';

function Checkout() {
  const navigate = useNavigate();
  const location = useLocation();
  const [loading, setLoading] = useState(false);
  const [orderSuccess, setOrderSuccess] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');

  const { address, total, cartItems } = location.state || {};
  const userStr = localStorage.getItem('user');
  const user = userStr ? JSON.parse(userStr) : null;

  useEffect(() => {
    if (!user || !address || !cartItems) {
      navigate('/cart');
    }
    // Load Razorpay script
    const script = document.createElement('script');
    script.src = 'https://checkout.razorpay.com/v1/checkout.js';
    script.async = true;
    document.body.appendChild(script);
    
    return () => {
      document.body.removeChild(script);
    };
  }, []);

  const handlePayment = async (e) => {
    e.preventDefault();
    setLoading(true);
    setErrorMsg('');

    try {
     
      const orderRes = await placeOrder(user.id, address);
      const dbOrder = orderRes.data;

      
      const paymentRes = await createRazorpayOrder(user.id, dbOrder.id, dbOrder.finalAmount);
      const paymentData = paymentRes.data;

     
      const options = {
        key: 'rzp_test_ScqHmVoI8WAn0s', 
        amount: dbOrder.finalAmount * 100, 
        currency: 'INR',
        name: 'Smart Cart',
        description: 'Order Payment',
        order_id: paymentData.razorpayOrderId,
        handler: async function (response) {
          try {
            
            await verifyRazorpaySignature({
              razorpay_order_id: response.razorpay_order_id,
              razorpay_payment_id: response.razorpay_payment_id,
              razorpay_signature: response.razorpay_signature
            });

           
            await updateOrderStatus(dbOrder.id, 'CONFIRMED');
            
           
            await clearCart(user.id);
            
            setLoading(false);
            setOrderSuccess(true);
          } catch (verifyErr) {
            setErrorMsg('Payment verification failed. Please contact support.');
            setLoading(false);
          }
        },
        prefill: {
          name: user.name,
          email: user.email,
          contact: '9999999999'
        },
        theme: {
          color: '#3399cc'
        }
      };

      const rzp = new window.Razorpay(options);
      rzp.on('payment.failed', function (response){
          setErrorMsg('Payment failed: ' + response.error.description);
          setLoading(false);
      });
      rzp.open();

    } catch (err) {
      console.error(err);
      setErrorMsg(err.response?.data?.message || err.message || 'Failed to initialize payment.');
      setLoading(false);
    }
  };

  if (orderSuccess) {
    return (
      <div className="container mt-5 pt-4">
        <div className="row justify-content-center">
          <div className="col-12 col-md-7">
            <div className="card border-0 shadow-sm text-center p-5">
              <div style={{ fontSize: '3rem' }}>✅</div>
              <h3 className="fw-bold mt-3 mb-2">Payment Successful!</h3>
              <p className="text-muted mb-4">
                Thank you for your order. Your items are being prepared for shipping.
              </p>
              <div className="alert alert-info py-2 mb-4" style={{ fontSize: '0.9rem' }}>
                📬 A confirmation notification has been sent. Track your order status on the Orders page.
              </div>
              <div className="d-flex justify-content-center gap-3">
                <Link to="/orders" className="btn btn-dark">View My Orders</Link>
                <Link to="/" className="btn btn-outline-dark">Continue Shopping</Link>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="animate-fade-in" style={{maxWidth: '600px', margin: '0 auto', marginTop: '3rem'}}>
      <div className="page-header text-center mb-5 border-bottom pb-3">
        <h2 className="fw-bold display-6 mb-0">Secure Checkout</h2>
      </div>
      
      {errorMsg && <div className="alert alert-danger">{errorMsg}</div>}

      <form className="card border shadow-sm p-4" onSubmit={handlePayment}>
        <h5 className="fw-bold">Shipping Details</h5>
        <div className="mb-3">
          <p className="text-muted small">{address}</p>
        </div>

        <hr />

        <h5 className="fw-bold">Order Summary</h5>
        <div className="d-flex justify-content-between mb-2">
          <span className="text-muted">Total Amount</span>
          <span className="fw-bold">₹{total?.toFixed(2)}</span>
        </div>
        


        <hr />
        
        <h5 className="fw-bold mb-3">Payment Options</h5>
        <div className="alert alert-light border text-center mb-4" style={{cursor: 'pointer'}}>
          💳 Credit/Debit Card, UPI, NetBanking via Razorpay
        </div>

        <button type="submit" className="btn btn-primary w-100 py-3 fw-bold fs-5 shadow-sm" disabled={loading}>
          {loading ? 'Processing...' : `Pay Securely (₹${total?.toFixed(2)})`}
        </button>
      </form>
    </div>
  );
}

export default Checkout;
