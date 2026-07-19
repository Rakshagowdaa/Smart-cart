import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getProductById, getReviews, addReview } from '../services/productService';
import { addToCart } from '../services/cartService';

function ProductDetail() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [product, setProduct] = useState(null);
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const [rating, setRating] = useState(5);
  const [hoverRating, setHoverRating] = useState(0);
  const [comment, setComment] = useState('');
  const [reviewMsg, setReviewMsg] = useState('');
  const [cartMsg, setCartMsg] = useState('');

  useEffect(() => {
    fetchProduct();
    fetchReviews();
  }, [id]);

  const fetchProduct = async () => {
    setLoading(true);
    setError('');
    try {
      const res = await getProductById(id);
      setProduct(res.data);
    } catch (err) {
      setError('Product not found.');
    } finally {
      setLoading(false);
    }
  };

  const fetchReviews = async () => {
    try {
      const res = await getReviews(id);
      setReviews(res.data);
    } catch (err) {
      // reviews optional — ignore error
    }
  };

  const handleAddToCart = async () => {
    const userStr = localStorage.getItem('user');
    if (!userStr) {
      navigate('/login');
      return;
    }
    const user = JSON.parse(userStr);
    try {
      await addToCart(user.id, product.id, 1);
      setCartMsg('✅ Added to cart!');
      setTimeout(() => setCartMsg(''), 2500);
    } catch (err) {
      setCartMsg('❌ Failed to add to cart.');
      setTimeout(() => setCartMsg(''), 2500);
    }
  };

  const handleReviewSubmit = async (e) => {
    e.preventDefault();
    const token = localStorage.getItem('token');
    if (!token) {
      navigate('/login');
      return;
    }
    try {
      await addReview(id, { rating, comment });
      setReviewMsg('Review submitted!');
      setComment('');
      setRating(5);
      fetchReviews();
      setTimeout(() => setReviewMsg(''), 2500);
    } catch (err) {
      setReviewMsg('Failed to submit review.');
      setTimeout(() => setReviewMsg(''), 2500);
    }
  };

  if (loading) {
    return (
      <div className="container mt-5 pt-4 text-center">
        <div className="spinner-border text-dark mt-5" role="status">
          <span className="visually-hidden">Loading...</span>
        </div>
      </div>
    );
  }

  if (error || !product) {
    return (
      <div className="container mt-5 pt-4">
        <div className="alert alert-danger">{error || 'Product not found.'}</div>
        <button className="btn btn-outline-dark" onClick={() => navigate('/')}>
          ← Back to Products
        </button>
      </div>
    );
  }

  return (
    <div className="container mt-5 pt-4">
      <button className="btn btn-link text-dark text-decoration-none ps-0 mb-3" onClick={() => navigate(-1)}>
        ← Back
      </button>

      <div className="row g-4">
        {/* Image */}
        <div className="col-12 col-md-5">
          <div className="card border-0 shadow-sm">
            <img
              src={product.imageUrl || `https://via.placeholder.com/500x400?text=${encodeURIComponent(product.name)}`}
              alt={product.name}
              className="card-img-top"
              style={{ maxHeight: '380px', objectFit: 'cover' }}
            />
          </div>
        </div>

        {/* Details */}
        <div className="col-12 col-md-7">
          {product.category && (
            <span className="badge bg-light text-dark border mb-2">{product.category}</span>
          )}
          <h2 className="fw-bold mb-2">{product.name}</h2>
          <h3 className="text-dark mb-3">₹{product.price?.toFixed(2)}</h3>

          <p className="text-muted mb-3">
            {product.description || 'No description available.'}
          </p>

          <p className="mb-3">
            <span className="fw-semibold">Stock: </span>
            <span className={product.stockQuantity > 0 ? 'text-success' : 'text-danger'}>
              {product.stockQuantity > 0 ? `${product.stockQuantity} units available` : 'Out of stock'}
            </span>
          </p>

          {cartMsg && <div className="alert alert-info py-2 mb-2">{cartMsg}</div>}

          <button
            id="detailAddCart"
            className="btn btn-dark px-4"
            onClick={handleAddToCart}
            disabled={product.stockQuantity <= 0}
          >
            {product.stockQuantity <= 0 ? 'Out of Stock' : 'Add to Cart'}
          </button>
        </div>
      </div>

      {/* Reviews Section */}
      <div className="row mt-5">
        <div className="col-12">
          <h4 className="fw-bold mb-3 border-bottom pb-2">Customer Reviews</h4>

          {reviews.length === 0 ? (
            <p className="text-muted">No reviews yet. Be the first to review!</p>
          ) : (
            <div className="mb-4">
              {reviews.map((rev, idx) => (
                <div key={idx} className="card mb-2 border shadow-sm">
                  <div className="card-body py-3">
                    <div className="d-flex justify-content-between mb-1">
                      <span className="fw-semibold">
                        {'⭐'.repeat(rev.rating)}
                        <span className="ms-1 text-muted fw-normal" style={{ fontSize: '0.85rem' }}>
                          ({rev.rating}/5)
                        </span>
                      </span>
                    </div>
                    <p className="mb-0 text-muted" style={{ fontSize: '0.9rem' }}>
                      {rev.comment}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          )}

          {/* Add Review Form */}
          <div className="card border shadow-sm">
            <div className="card-body p-4">
              <h6 className="fw-bold mb-3">Write a Review</h6>

              {reviewMsg && (
                <div className="alert alert-info py-2 mb-3">{reviewMsg}</div>
              )}

              <form onSubmit={handleReviewSubmit}>
                <div className="mb-3">
                  <label className="form-label fw-semibold d-block">Rating</label>
                  <div className="star-rating" style={{ fontSize: '2rem', cursor: 'pointer', display: 'inline-block' }}>
                    {[1, 2, 3, 4, 5].map((star) => {
                      const isStarred = star <= (hoverRating || rating);
                      return (
                        <span
                          key={star}
                          className="me-2 star-icon"
                          onClick={() => setRating(star)}
                          onMouseEnter={() => setHoverRating(star)}
                          onMouseLeave={() => setHoverRating(0)}
                          style={{
                            color: isStarred ? '#FFD700' : '#E0E0E0',
                            transition: 'color 0.15s ease-in-out, transform 0.15s ease-in-out',
                            display: 'inline-block',
                            transform: star <= (hoverRating || rating) ? 'scale(1.1)' : 'scale(1)'
                          }}
                        >
                          ★
                        </span>
                      );
                    })}
                    <span className="ms-2 text-muted" style={{ fontSize: '1rem', verticalAlign: 'middle' }}>
                      ({rating}/5 Star{rating > 1 ? 's' : ''})
                    </span>
                  </div>
                </div>
                <div className="mb-3">
                  <label htmlFor="reviewComment" className="form-label fw-semibold">Comment</label>
                  <textarea
                    id="reviewComment"
                    className="form-control"
                    rows={3}
                    placeholder="Share your experience..."
                    value={comment}
                    onChange={(e) => setComment(e.target.value)}
                    required
                  />
                </div>
                <button type="submit" id="reviewSubmitBtn" className="btn btn-dark">
                  Submit Review
                </button>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default ProductDetail;
