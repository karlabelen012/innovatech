import '@testing-library/jest-dom'

// Mock ResizeObserver (not available in jsdom, required by Recharts)
global.ResizeObserver = class ResizeObserver {
  observe() {}
  unobserve() {}
  disconnect() {}
}

// Mock scrollIntoView (not implemented in jsdom, used by ChatPage)
if (!Element.prototype.scrollIntoView) {
  Element.prototype.scrollIntoView = function () {}
}
