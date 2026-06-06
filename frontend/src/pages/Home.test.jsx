import { render, screen, waitFor } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import React from 'react';
import { MemoryRouter } from 'react-router-dom';
import Home from './Home';
import * as productService from '../services/productService';

// Mock the product service
vi.mock('../services/productService', () => ({
  getAllProducts: vi.fn(),
  searchProducts: vi.fn(),
  filterByCategory: vi.fn()
}));

describe('Home Page', () => {
  it('renders loading state initially', () => {
    productService.getAllProducts.mockResolvedValue({ data: [] });
    render(
      <MemoryRouter>
        <Home />
      </MemoryRouter>
    );
    expect(screen.getByText(/Loading products.../i)).toBeInTheDocument();
  });

  it('renders products after fetching', async () => {
    const mockProducts = [
      { id: 1, name: 'Test Product', price: 99.99, stockQuantity: 10, description: 'Test Description' }
    ];
    productService.getAllProducts.mockResolvedValue({ data: mockProducts });

    render(
      <MemoryRouter>
        <Home />
      </MemoryRouter>
    );

    await waitFor(() => {
      expect(screen.getByText('Test Product')).toBeInTheDocument();
      expect(screen.getByText('₹99.99')).toBeInTheDocument();
    });
  });

  it('shows error message if API fails', async () => {
    productService.getAllProducts.mockRejectedValue(new Error('API Error'));

    render(
      <MemoryRouter>
        <Home />
      </MemoryRouter>
    );

    await waitFor(() => {
      expect(screen.getByText(/Could not load products/i)).toBeInTheDocument();
    });
  });
});
