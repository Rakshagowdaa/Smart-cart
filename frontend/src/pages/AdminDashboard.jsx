import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { getAllProducts, addProduct, deleteProduct } from '../services/productService';
import { getAllOrders, updateOrderStatus, getSalesSummary } from '../services/orderService';
import { getAllUsers } from '../services/userService';
import { createVendor } from '../services/authService';

const CATEGORIES = ['Electronics', 'Fashion', 'Footwear', 'Groceries', 'Accessories'];

const emptyForm = {
  name: '',
  price: '',
  category: 'Electronics',
  imageUrl: '',
  stockQuantity: '',
  description: '',
  vendorId: '',
};

function AdminDashboard() {
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('products');

  // Products state
  const [products, setProducts] = useState([]);
  const [prodLoading, setProdLoading] = useState(true);
  const [form, setForm] = useState(emptyForm);
  const [formMsg, setFormMsg] = useState('');
  const [formError, setFormError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  // Orders state
  const [orders, setOrders] = useState([]);
  const [ordLoading, setOrdLoading] = useState(true);
  const [summary, setSummary] = useState(null);
  const [statusMsg, setStatusMsg] = useState('');

  const userStr = localStorage.getItem('user');
  const user = userStr ? JSON.parse(userStr) : null;

  // Vendors state
  const [vendors, setVendors] = useState([]);
  const [vendorForm, setVendorForm] = useState({ name: '', email: '', password: '' });
  const [vendorMsg, setVendorMsg] = useState('');
  const [venLoading, setVenLoading] = useState(false);

  useEffect(() => {
    fetchProducts();
    fetchOrders();
    fetchSummary();
    fetchVendors();
  }, []);

  const fetchVendors = async () => {
    try {
      const res = await getAllUsers();
      setVendors(res.data.filter(u => u.role === 'VENDOR'));
    } catch (err) {
      console.error('Failed to load vendors');
    }
  };

  const handleCreateVendor = async (e) => {
    e.preventDefault();
    setVendorMsg('');
    setVenLoading(true);
    try {
      await createVendor(vendorForm.name, vendorForm.email, vendorForm.password);
      setVendorMsg('✅ Vendor created successfully!');
      setVendorForm({ name: '', email: '', password: '' });
      fetchVendors();
      setTimeout(() => setVendorMsg(''), 3000);
    } catch (err) {
      setVendorMsg('❌ Failed to create vendor.');
      setTimeout(() => setVendorMsg(''), 3000);
    } finally {
      setVenLoading(false);
    }
  };

  const fetchProducts = async () => {
    setProdLoading(true);
    try {
      const res = await getAllProducts();
      setProducts(res.data);
    } catch (err) {
      console.error('Failed to load products');
    } finally {
      setProdLoading(false);
    }
  };

  const fetchOrders = async () => {
    setOrdLoading(true);
    try {
      const res = await getAllOrders();
      setOrders(res.data);
    } catch (err) {
      console.error('Failed to load orders');
    } finally {
      setOrdLoading(false);
    }
  };

  const fetchSummary = async () => {
    try {
      const res = await getSalesSummary();
      setSummary(res.data);
    } catch (err) {
      console.error('Failed to load summary');
    }
  };

  const handleFormChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleAddProduct = async (e) => {
    e.preventDefault();
    setFormMsg('');
    setFormError('');
    setSubmitting(true);
    try {
      await addProduct({
        name: form.name,
        price: parseFloat(form.price),
        category: form.category,
        imageUrl: form.imageUrl,
        stockQuantity: parseInt(form.stockQuantity),
        description: form.description,
        vendorId: form.vendorId ? parseInt(form.vendorId) : null,
      });
      setFormMsg('✅ Product added successfully!');
      setForm(emptyForm);
      fetchProducts();
      setTimeout(() => setFormMsg(''), 3000);
    } catch (err) {
      setFormError('❌ Failed to add product.');
      setTimeout(() => setFormError(''), 3000);
    } finally {
      setSubmitting(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this product?')) return;
    try {
      await deleteProduct(id);
      fetchProducts();
    } catch (err) {
      alert('Failed to delete product.');
    }
  };

  const handleStatusUpdate = async (orderId, status) => {
    try {
      await updateOrderStatus(orderId, status);
      setStatusMsg(`Order #${orderId} updated to ${status}`);
      fetchOrders();
      setTimeout(() => setStatusMsg(''), 3000);
    } catch (err) {
      alert('Failed to update order status.');
    }
  };

  const ORDER_STATUSES = ['CREATED', 'PROCESSING', 'SHIPPED', 'DELIVERED'];

  return (
    <div className="container mt-5 pt-4">
      <h2 className="fw-bold mb-1">Admin Dashboard</h2>
      <p className="text-muted mb-4">Manage products and orders</p>

      {/* Summary Cards */}
      {summary && (
        <div className="row g-3 mb-4">
          <div className="col-6 col-md-3">
            <div className="card border shadow-sm text-center p-3">
              <div className="fs-2 fw-bold">{products.length}</div>
              <div className="text-muted small">Total Products</div>
            </div>
          </div>
          <div className="col-6 col-md-3">
            <div className="card border shadow-sm text-center p-3">
              <div className="fs-2 fw-bold">{summary.totalOrders}</div>
              <div className="text-muted small">Total Orders</div>
            </div>
          </div>
          <div className="col-6 col-md-3">
            <div className="card border shadow-sm text-center p-3">
              <div className="fs-2 fw-bold">₹{summary.totalRevenue?.toFixed(0)}</div>
              <div className="text-muted small">Total Revenue</div>
            </div>
          </div>
        </div>
      )}

      {/* Tabs */}
      <ul className="nav nav-tabs mb-4" id="adminTabs">
        <li className="nav-item">
          <button
            id="tabProducts"
            className={`nav-link ${activeTab === 'products' ? 'active fw-semibold' : 'text-dark'}`}
            onClick={() => setActiveTab('products')}
          >
            Products
          </button>
        </li>
        <li className="nav-item">
          <button
            id="tabOrders"
            className={`nav-link ${activeTab === 'orders' ? 'active fw-semibold' : 'text-dark'}`}
            onClick={() => setActiveTab('orders')}
          >
            Orders
          </button>
        </li>
        <li className="nav-item">
          <button
            id="tabVendors"
            className={`nav-link ${activeTab === 'vendors' ? 'active fw-semibold' : 'text-dark'}`}
            onClick={() => setActiveTab('vendors')}
          >
            Vendors
          </button>
        </li>
      </ul>

      {/* Products Tab */}
      {activeTab === 'products' && (
        <div className="row g-4">
          {/* Add Product Form */}
          <div className="col-12 col-lg-4">
            <div className="card border shadow-sm">
              <div className="card-body p-4">
                <h6 className="fw-bold mb-3">Add New Product</h6>

                {formMsg && <div className="alert alert-success py-2">{formMsg}</div>}
                {formError && <div className="alert alert-danger py-2">{formError}</div>}

                <form onSubmit={handleAddProduct} id="addProductForm">
                  <div className="mb-2">
                    <label htmlFor="prodName" className="form-label small fw-semibold">Product Name *</label>
                    <input type="text" id="prodName" name="name" className="form-control form-control-sm"
                      placeholder="e.g. iPhone 15" value={form.name} onChange={handleFormChange} required />
                  </div>
                  <div className="mb-2">
                    <label htmlFor="prodPrice" className="form-label small fw-semibold">Price (₹) *</label>
                    <input type="number" id="prodPrice" name="price" className="form-control form-control-sm"
                      placeholder="e.g. 29999" step="0.01" value={form.price} onChange={handleFormChange} required />
                  </div>
                  <div className="mb-2">
                    <label htmlFor="prodCategory" className="form-label small fw-semibold">Category *</label>
                    <select id="prodCategory" name="category" className="form-select form-select-sm"
                      value={form.category} onChange={handleFormChange} required>
                      {CATEGORIES.map((c) => <option key={c} value={c}>{c}</option>)}
                    </select>
                  </div>
                  <div className="mb-2">
                    <label htmlFor="prodStock" className="form-label small fw-semibold">Stock Quantity *</label>
                    <input type="number" id="prodStock" name="stockQuantity" className="form-control form-control-sm"
                      placeholder="e.g. 50" value={form.stockQuantity} onChange={handleFormChange} required />
                  </div>
                  <div className="mb-2">
                    <label htmlFor="prodImage" className="form-label small fw-semibold">Image URL</label>
                    <input type="url" id="prodImage" name="imageUrl" className="form-control form-control-sm"
                      placeholder="https://..." value={form.imageUrl} onChange={handleFormChange} />
                  </div>
                  <div className="mb-2">
                    <label htmlFor="prodVendor" className="form-label small fw-semibold">Vendor (Optional)</label>
                    <select id="prodVendor" name="vendorId" className="form-select form-select-sm"
                      value={form.vendorId} onChange={handleFormChange}>
                      <option value="">-- No Vendor (Admin Managed) --</option>
                      {vendors.map(v => <option key={v.id} value={v.id}>{v.name}</option>)}
                    </select>
                  </div>
                  <div className="mb-3">
                    <label htmlFor="prodDesc" className="form-label small fw-semibold">Description</label>
                    <textarea id="prodDesc" name="description" className="form-control form-control-sm" rows={2}
                      placeholder="Brief product description..." value={form.description} onChange={handleFormChange} />
                  </div>
                  <div className="d-grid">
                    <button type="submit" id="addProductBtn" className="btn btn-dark btn-sm" disabled={submitting}>
                      {submitting ? 'Adding...' : 'Add Product'}
                    </button>
                  </div>
                </form>
              </div>
            </div>
          </div>

          {/* Product List */}
          <div className="col-12 col-lg-8">
            <div className="card border shadow-sm">
              <div className="card-body p-3">
                <div className="d-flex justify-content-between align-items-center mb-3">
                  <h6 className="fw-bold mb-0">All Products ({products.length})</h6>
                  <button className="btn btn-outline-secondary btn-sm" onClick={fetchProducts}>Refresh</button>
                </div>

                {prodLoading ? (
                  <div className="text-center py-3">
                    <div className="spinner-border spinner-border-sm" role="status"></div>
                  </div>
                ) : products.length === 0 ? (
                  <p className="text-muted text-center py-3">No products found. Add your first product!</p>
                ) : (
                  <div className="table-responsive">
                    <table className="table table-sm table-hover align-middle mb-0">
                      <thead className="table-light">
                        <tr>
                          <th>ID</th>
                          <th>Name</th>
                          <th>Category</th>
                          <th>Price</th>
                          <th>Stock</th>
                          <th>Action</th>
                        </tr>
                      </thead>
                      <tbody>
                        {products.map((p) => (
                          <tr key={p.id}>
                            <td className="text-muted small">#{p.id}</td>
                            <td className="fw-semibold" style={{ maxWidth: '150px' }}>
                              <span title={p.name}>
                                {p.name.length > 25 ? p.name.substring(0, 25) + '...' : p.name}
                              </span>
                            </td>
                            <td><span className="badge bg-light text-dark border">{p.category}</span></td>
                            <td>₹{p.price?.toFixed(2)}</td>
                            <td>
                              <span className={p.stockQuantity > 0 ? 'text-success' : 'text-danger'}>
                                {p.stockQuantity}
                              </span>
                            </td>
                            <td>
                              <button
                                id={`deleteProd-${p.id}`}
                                className="btn btn-sm btn-outline-danger"
                                onClick={() => handleDelete(p.id)}
                              >
                                Delete
                              </button>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Orders Tab */}
      {activeTab === 'orders' && (
        <div>
          {statusMsg && <div className="alert alert-success py-2 mb-3">{statusMsg}</div>}

          {ordLoading ? (
            <div className="text-center py-5">
              <div className="spinner-border text-dark" role="status"></div>
            </div>
          ) : orders.length === 0 ? (
            <p className="text-muted text-center py-4">No orders found.</p>
          ) : (
            <div className="table-responsive">
              <table className="table table-bordered table-hover align-middle">
                <thead className="table-dark">
                  <tr>
                    <th>Order ID</th>
                    <th>User ID</th>
                    <th>Total</th>
                    <th>Status</th>
                    <th>Address</th>
                    <th>Update Status</th>
                  </tr>
                </thead>
                <tbody>
                  {orders.map((order) => (
                    <tr key={order.id}>
                      <td className="fw-semibold">#{order.id}</td>
                      <td>{order.userId}</td>
                      <td>₹{order.totalAmount?.toFixed(2) ?? '—'}</td>
                      <td>
                        <span className={`badge ${order.status === 'DELIVERED' ? 'bg-success' :
                            order.status === 'SHIPPED' ? 'bg-info text-dark' :
                              order.status === 'PROCESSING' ? 'bg-warning text-dark' : 'bg-secondary'
                          }`}>
                          {order.status}
                        </span>
                      </td>
                      <td className="small text-muted">
                        {(order.deliveryAddress || order.address || '—').substring(0, 30)}
                      </td>
                      <td>
                        <select
                          className="form-select form-select-sm"
                          value={order.status}
                          id={`statusSelect-${order.id}`}
                          onChange={(e) => handleStatusUpdate(order.id, e.target.value)}
                          disabled={order.status === 'DELIVERED'}
                        >
                          {ORDER_STATUSES.map((s) => {
                            const currentIndex = ORDER_STATUSES.indexOf(order.status);
                            const thisIndex = ORDER_STATUSES.indexOf(s);
                            const disabled = thisIndex < currentIndex;
                            return (
                              <option key={s} value={s} disabled={disabled}>
                                {s}
                              </option>
                            );
                          })}
                        </select>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      )}

      {/* Vendors Tab */}
      {activeTab === 'vendors' && (
        <div className="row g-4">
          <div className="col-12 col-lg-4">
            <div className="card border shadow-sm">
              <div className="card-body p-4">
                <h6 className="fw-bold mb-3">Create Vendor Account</h6>
                {vendorMsg && (
                  <div className={`alert ${vendorMsg.includes('✅') ? 'alert-success' : 'alert-danger'} py-2`}>
                    {vendorMsg}
                  </div>
                )}
                <form onSubmit={handleCreateVendor}>
                  <div className="mb-2">
                    <label className="form-label small fw-semibold">Name *</label>
                    <input type="text" className="form-control form-control-sm"
                      value={vendorForm.name} onChange={e => setVendorForm({...vendorForm, name: e.target.value})} required />
                  </div>
                  <div className="mb-2">
                    <label className="form-label small fw-semibold">Email *</label>
                    <input type="email" className="form-control form-control-sm"
                      value={vendorForm.email} onChange={e => setVendorForm({...vendorForm, email: e.target.value})} required />
                  </div>
                  <div className="mb-3">
                    <label className="form-label small fw-semibold">Temporary Password *</label>
                    <input type="password" className="form-control form-control-sm"
                      value={vendorForm.password} onChange={e => setVendorForm({...vendorForm, password: e.target.value})} required minLength="6" />
                  </div>
                  <div className="d-grid">
                    <button type="submit" className="btn btn-dark btn-sm" disabled={venLoading}>
                      {venLoading ? 'Creating...' : 'Create Vendor'}
                    </button>
                  </div>
                </form>
              </div>
            </div>
          </div>
          
          <div className="col-12 col-lg-8">
            <div className="card border shadow-sm">
              <div className="card-body p-3">
                <h6 className="fw-bold mb-3">Registered Vendors ({vendors.length})</h6>
                {vendors.length === 0 ? (
                  <p className="text-muted text-center py-3">No vendors found.</p>
                ) : (
                  <div className="table-responsive">
                    <table className="table table-sm table-hover align-middle mb-0">
                      <thead className="table-light">
                        <tr>
                          <th>ID</th>
                          <th>Name</th>
                          <th>Email</th>
                        </tr>
                      </thead>
                      <tbody>
                        {vendors.map(v => (
                          <tr key={v.id}>
                            <td className="text-muted small">#{v.id}</td>
                            <td className="fw-semibold">{v.name}</td>
                            <td>{v.email}</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default AdminDashboard;
