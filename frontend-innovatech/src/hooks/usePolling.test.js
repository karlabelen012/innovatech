import { describe, it, expect, vi } from 'vitest'
import { renderHook, waitFor } from '@testing-library/react'
import { usePolling } from './usePolling.js'

describe('usePolling', () => {
  it('ejecuta el callback inmediatamente al montar', () => {
    const cb = vi.fn()
    renderHook(() => usePolling(cb, 1000))
    expect(cb).toHaveBeenCalledTimes(1)
  })

  it('ejecuta el callback repetidamente cada intervalo', async () => {
    const cb = vi.fn()
    renderHook(() => usePolling(cb, 30))
    await waitFor(() => expect(cb.mock.calls.length).toBeGreaterThanOrEqual(3), { timeout: 2000 })
  })

  it('no ejecuta el callback si enabled es false', () => {
    const cb = vi.fn()
    renderHook(() => usePolling(cb, 1000, false))
    expect(cb).not.toHaveBeenCalled()
  })

  it('detiene el polling al desmontar', async () => {
    const cb = vi.fn()
    const { unmount } = renderHook(() => usePolling(cb, 30))
    await waitFor(() => expect(cb.mock.calls.length).toBeGreaterThanOrEqual(1))
    unmount()
    const callsAtUnmount = cb.mock.calls.length
    await new Promise(r => setTimeout(r, 150))
    expect(cb.mock.calls.length).toBe(callsAtUnmount)
  })

  it('usa siempre la versión más reciente del callback (sin closures obsoletas)', async () => {
    let valor = 'inicial'
    const cb = vi.fn(() => valor)
    const { rerender } = renderHook(({ callback }) => usePolling(callback, 30), {
      initialProps: { callback: cb },
    })

    valor = 'actualizado'
    rerender({ callback: cb })

    await waitFor(() => expect(cb.mock.results.some(r => r.value === 'actualizado')).toBe(true))
  })
})
