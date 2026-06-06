import React, { useState, useEffect } from 'react';
import { getWishlist, removeFromWishlist } from '../services/wishlistService';
import { getProductById } from '../services/productService';
import { Link } from 'react-router-dom';

function Wishlist() {
  const [wishlistItems, setWishlistItems] = useState([]);
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);

  const user = JSON.parse(localStorage.getItem('user'));

  useEffect(() => {
    if (user) {
      fetchWishlist();
    }
  }, []);

  const fetchWishlist = async () => {
    setLoading(true);
    try {
      const res = await getWishlist(user.id);
      const items = res.data;
      setWishlistItems(items);

      
      const productDetails = await Promise.all(
        items.map(item => getProductById(item.productId))
      );
      setProducts(productDetails.map(r => r.data));
    } catch (err) {
      console.error('Error fetching wishlist', err);
    } finally {
      setLoading(false);
    }
  };

  const handleRemove = async (productId) => {
    try {
      await removeFromWishlist(user.id, productId);
      fetchWishlist();
    } catch (err) {
      alert('Failed to remove from wishlist');
    }
  };

  if (loading) return <div className="container mt-5 pt-5 text-center">Loading wishlist...</div>;

  return (
    <div className="container mt-5 pt-4">
      <h2 className="fw-bold mb-4">My Wishlist</h2>
      {products.length === 0 ? (
        <div className="text-center py-5">
          <p className="text-muted">Your wishlist is empty.</p>
          <Link to="/" className="btn btn-dark">Browse Products</Link>
        </div>
      ) : (
        <div className="row g-4">
          {products.map(product => (
            <div className="col-12 col-md-4 col-lg-3" key={product.id}>
              <div className="card h-100 border-0 shadow-sm sc-product-card">
                <img
                  src={product.imageUrl || 'https://via.placeholder.com/300x200?text=Product'}
                  className="card-img-top p-3"
                  alt={product.name}
                  style={{ height: '200px', objectFit: 'contain' }}
                />
                <div className="card-body d-flex flex-column">
                  <h6 className="card-title fw-bold text-truncate">{product.name}</h6>
                  <p className="card-text text-primary fw-bold mb-3">₹{product.price}</p>
                  <div className="mt-auto d-flex gap-2">
                    <Link to={`/product/${product.id}`} className="btn btn-sm btn-outline-dark flex-grow-1">View</Link>
                    <button 
                      className="btn btn-sm btn-outline-danger" 
                      onClick={() => handleRemove(product.id)}
                      title="Remove from Wishlist"
                    >
                      🗑️
                    </button>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default Wishlist;
