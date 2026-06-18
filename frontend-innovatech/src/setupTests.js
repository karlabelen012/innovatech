import '@testing-library/jest-dom'

// Mock ResizeObserver (not available in jsdom, required by Recharts)
global.ResizeObserver = class ResizeObserver {
  observe() {}
  unobserve() {}
  disconnect() {}
}
