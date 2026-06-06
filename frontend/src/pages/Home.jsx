import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { getAllProducts, searchProducts, filterByCategory } from '../services/productService';
import { addToCart } from '../services/cartService';
import { addToWishlist } from '../services/wishlistService';

const CATEGORIES = ['All', 'Electronics', 'Fashion', 'Footwear', 'Groceries', 'Accessories'];

function Home() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('All');
  const [searchQuery, setSearchQuery] = useState('');
  const [cartMsg, setCartMsg] = useState({});

  const location = useLocation();
  const navigate = useNavigate();

  
  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const q = params.get('search') || '';
    setSearchQuery(q);
  }, [location.search]);

  useEffect(() => {
    fetchProducts();
  }, [searchQuery, selectedCategory]);

  const fetchProducts = async () => {
    setLoading(true);
    setError('');
    try {
      let res;
      if (searchQuery.trim()) {
        res = await searchProducts(searchQuery);
      } else if (selectedCategory !== 'All') {
        res = await filterByCategory(selectedCategory);
      } else {
        res = await getAllProducts();
      }
      setProducts(res.data);
    } catch (err) {
      setError('Could not load products. Please make sure the backend is running.');
    } finally {
      setLoading(false);
    }
  };

  const handleAddToCart = async (product) => {
    const userStr = localStorage.getItem('user');
    if (!userStr) {
      navigate('/login');
      return;
    }
    const user = JSON.parse(userStr);
    try {
      await addToCart(user.id, product.id, 1);
      setCartMsg((prev) => ({ ...prev, [product.id]: 'Added!' }));
      setTimeout(() => {
        setCartMsg((prev) => ({ ...prev, [product.id]: '' }));
      }, 2000);
    } catch (err) {
      setCartMsg((prev) => ({ ...prev, [product.id]: 'Failed' }));
      setTimeout(() => {
        setCartMsg((prev) => ({ ...prev, [product.id]: '' }));
      }, 2000);
    }
  };

  const handleAddToWishlist = async (product) => {
    const userStr = localStorage.getItem('user');
    if (!userStr) {
      navigate('/login');
      return;
    }
    const user = JSON.parse(userStr);
    try {
      await addToWishlist(user.id, product.id);
      alert('Added to wishlist!');
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to add to wishlist');
    }
  };

  const handleCategoryChange = (cat) => {
    setSelectedCategory(cat);
    setSearchQuery('');
    navigate('/');
  };

  return (
    <div className="container mt-5 pt-4">

      {/* Page Header */}
      <div className="mb-5 text-center">
        <h2 className="fw-bold display-6 mb-2">Discover Our Products</h2>
        <p className="text-muted fs-5">Premium quality goods just for you</p>
      </div>

      {/* Category Filter */}
      <div className="mb-4 d-flex flex-wrap gap-2" id="categoryFilter">
        {CATEGORIES.map((cat) => (
          <button
            key={cat}
            id={`cat-${cat}`}
            onClick={() => handleCategoryChange(cat)}
            className={`btn btn-sm ${
              selectedCategory === cat && !searchQuery
                ? 'btn-dark'
                : 'btn-outline-secondary'
            }`}
          >
            {cat}
          </button>
        ))}
      </div>

      {/* Active filter label */}
      {searchQuery && (
        <div className="mb-3">
          <span className="badge bg-secondary fs-6">
            Search: "{searchQuery}"
          </span>
          <button
            className="btn btn-link btn-sm text-danger ms-2 p-0"
            onClick={() => { setSearchQuery(''); navigate('/'); }}
          >
            Clear
          </button>
        </div>
      )}

      {/* Loading / Error */}
      {loading && (
        <div className="text-center py-5">
          <div className="spinner-border text-dark" role="status">
            <span className="visually-hidden">Loading...</span>
          </div>
          <p className="mt-2 text-muted">Loading products...</p>
        </div>
      )}

      {error && !loading && (
        <div className="alert alert-warning" role="alert">
          <strong>⚠️ {error}</strong>
        </div>
      )}

      {/* No Products */}
      {!loading && !error && products.length === 0 && (
        <div className="text-center py-5">
          <p className="text-muted fs-5">No products found.</p>
          <button className="btn btn-outline-dark" onClick={() => { setSearchQuery(''); setSelectedCategory('All'); navigate('/'); }}>
            Show All Products
          </button>
        </div>
      )}

      {/* Product Grid */}
      {!loading && !error && products.length > 0 && (
        <div className="row g-4">
          {products.map((product) => (
            <div key={product.id} className="col-12 col-sm-6 col-lg-4">
              <div className="card h-100 shadow-sm border sc-product-card position-relative">
                {/* Wishlist Heart Icon */}
                <button 
                  className="btn btn-light btn-sm position-absolute top-0 end-0 m-2 rounded-circle shadow-sm"
                  onClick={() => handleAddToWishlist(product)}
                  style={{ zIndex: 1 }}
                  title="Add to Wishlist"
                >
                  ❤️
                </button>

                {/* Product Image */}
                <Link to={`/product/${product.id}`}>
                  <img
                    src={product.imageUrl || `https://via.placeholder.com/400x250?text=${encodeURIComponent(product.name)}`}
                    className="card-img-top sc-product-img"
                    alt={product.name}
                  />
                </Link>

                <div className="card-body d-flex flex-column">
                  {/* Category badge */}
                  {product.category && (
                    <span className="badge bg-light text-dark border mb-2" style={{ width: 'fit-content', fontSize: '0.75rem' }}>
                      {product.category}
                    </span>
                  )}

                  <h6 className="card-title fw-semibold mb-1">
                    <Link to={`/product/${product.id}`} className="text-dark text-decoration-none sc-product-link">
                      {product.name}
                    </Link>
                  </h6>

                  <p className="text-muted small mb-2 flex-grow-1" style={{ fontSize: '0.85rem' }}>
                    {product.description
                      ? product.description.length > 80
                        ? product.description.substring(0, 80) + '...'
                        : product.description
                      : 'No description available.'}
                  </p>

                  <div className="d-flex justify-content-between align-items-center mt-2">
                    <span className="fw-bold fs-5">₹{product.price?.toFixed(2)}</span>
                    <span className={`small ${product.stockQuantity > 0 ? 'text-success' : 'text-danger'}`}>
                      {product.stockQuantity > 0 ? `In Stock (${product.stockQuantity})` : 'Out of Stock'}
                    </span>
                  </div>

                  <button
                    id={`addCart-${product.id}`}
                    className="btn btn-primary mt-3 w-100 py-2"
                    onClick={() => handleAddToCart(product)}
                    disabled={product.stockQuantity <= 0}
                  >
                    {cartMsg[product.id]
                      ? cartMsg[product.id]
                      : product.stockQuantity <= 0
                      ? 'Out of Stock'
                      : 'Add to Cart'}
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default Home;
