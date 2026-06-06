import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import React from 'react';
import { MemoryRouter } from 'react-router-dom';
import Navbar from './components/Navbar';

describe('Navbar Component', () => {
  it('renders brand name', () => {
    render(
      <MemoryRouter>
        <Navbar />
      </MemoryRouter>
    );
    expect(screen.getByText(/SmartCart/i)).toBeInTheDocument();
  });

  it('renders login link when not logged in', () => {
    localStorage.clear();
    render(
      <MemoryRouter>
        <Navbar />
      </MemoryRouter>
    );
    expect(screen.getByText(/Login/i)).toBeInTheDocument();
  });
});
