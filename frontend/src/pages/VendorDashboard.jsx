import React, { useState, useEffect } from 'react';
import { getProductsByVendor, addProduct, deleteProduct } from '../services/productService';
import { getOrdersByVendor, getVendorPayouts } from '../services/orderService';

const CATEGORIES = ['Electronics', 'Fashion', 'Footwear', 'Groceries', 'Accessories'];

const emptyForm = {
  name: '',
  price: '',
  category: 'Electronics',
  imageUrl: '',
  stockQuantity: '',
  description: '',
};

function VendorDashboard() {
  const [activeTab, setActiveTab] = useState('products');
  const [products, setProducts] = useState([]);
  const [orders, setOrders] = useState([]);
  const [payouts, setPayouts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [form, setForm] = useState(emptyForm);
  const [msg, setMsg] = useState('');

  const user = JSON.parse(localStorage.getItem('user'));

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    setLoading(true);
    try {
      const prodRes = await getProductsByVendor(user.id);
      setProducts(prodRes.data);
      const ordRes = await getOrdersByVendor(user.id);
      setOrders(ordRes.data);
      const payRes = await getVendorPayouts(user.id);
      setPayouts(payRes.data);
    } catch (err) {
      console.error('Error fetching vendor data');
    } finally {
      setLoading(false);
    }
  };

  const handleAddProduct = async (e) => {
    e.preventDefault();
    try {
      await addProduct({ ...form, vendorId: user.id });
      setMsg('Product added successfully!');
      setForm(emptyForm);
      fetchData();
    } catch (err) {
      setMsg('Failed to add product.');
    }
  };

  return (
    <div className="container mt-5 pt-4 bg-light min-vh-100 px-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <div>
          <h2 className="fw-bold text-success mb-0">Vendor Business Center</h2>
          <p className="text-muted">Manage your store and track performance</p>
        </div>
        <div className="badge bg-success p-2 px-3 shadow-sm">Verified Merchant</div>
      </div>

      {/* Navigation Tabs */}
      <ul className="nav nav-pills mb-4 bg-white p-2 rounded shadow-sm">
        <li className="nav-item">
          <button 
            className={`nav-link rounded-pill px-4 me-2 ${activeTab === 'products' ? 'active bg-success' : 'text-success fw-bold'}`} 
            onClick={() => setActiveTab('products')}
          >
            My Catalog
          </button>
        </li>
        <li className="nav-item">
          <button 
            className={`nav-link rounded-pill px-4 me-2 ${activeTab === 'orders' ? 'active bg-success' : 'text-success fw-bold'}`} 
            onClick={() => setActiveTab('orders')}
          >
            Recent Orders
          </button>
        </li>
        <li className="nav-item">
          <button 
            className={`nav-link rounded-pill px-4 ${activeTab === 'payouts' ? 'active bg-success' : 'text-success fw-bold'}`} 
            onClick={() => setActiveTab('payouts')}
          >
            Earnings
          </button>
        </li>
      </ul>

      {msg && <div className="alert alert-info">{msg}</div>}

      {activeTab === 'products' && (
          <div className="col-12">
            <div className="card shadow-sm border-0 p-4">
              <h5 className="fw-bold mb-3">My Catalog (Managed by Admin)</h5>
              <div className="table-responsive">
                <table className="table">
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Name</th>
                      <th>Price</th>
                      <th>Stock</th>
                      <th>Action</th>
                    </tr>
                  </thead>
                  <tbody>
                    {products.map(p => (
                      <tr key={p.id}>
                        <td>#{p.id}</td>
                        <td>{p.name}</td>
                        <td>₹{p.price}</td>
                        <td>{p.stockQuantity}</td>
                        <td>
                          <span className="text-muted small">Contact Admin to delete</span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
      )}

      {activeTab === 'orders' && (
        <div className="card shadow-sm border-0 p-4">
          <h5 className="fw-bold mb-3">Orders Containing Your Items</h5>
          <div className="table-responsive">
            <table className="table">
              <thead>
                <tr>
                  <th>Order ID</th>
                  <th>Customer</th>
                  <th>Status</th>
                  <th>Date</th>
                </tr>
              </thead>
              <tbody>
                {orders.map(o => (
                  <tr key={o.id}>
                    <td>#{o.id}</td>
                    <td>User #{o.userId}</td>
                    <td><span className="badge bg-secondary">{o.status}</span></td>
                    <td>{new Date(o.createdAt).toLocaleDateString()}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {activeTab === 'payouts' && (
        <div className="card shadow-sm border-0 p-4">
          <h5 className="fw-bold mb-3">Earnings & Payouts</h5>
          <div className="table-responsive">
            <table className="table">
              <thead>
                <tr>
                  <th>Order ID</th>
                  <th>Amount Earned</th>
                  <th>Status</th>
                  <th>Date</th>
                </tr>
              </thead>
              <tbody>
                {payouts.map(p => (
                  <tr key={p.id}>
                    <td>#{p.orderId}</td>
                    <td>₹{p.amount.toFixed(2)}</td>
                    <td>
                      <span className={`badge ${p.status === 'PAID' ? 'bg-success' : 'bg-warning text-dark'}`}>
                        {p.status}
                      </span>
                    </td>
                    <td>{new Date(p.createdAt).toLocaleDateString()}</td>
                  </tr>
                ))}
                {payouts.length === 0 && (
                  <tr>
                    <td colSpan="4" className="text-center py-4 text-muted">No payouts recorded yet.</td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
}

export default VendorDashboard;
