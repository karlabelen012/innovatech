import { useEffect, useRef } from 'react'

/**
 * Ejecuta `callback` inmediatamente y luego cada `intervalMs` mientras el
 * componente esté montado. Se detiene automáticamente al desmontar el
 * componente o si `enabled` es false (ej: pestaña inactiva).
 */
export function usePolling(callback, intervalMs, enabled = true) {
  const callbackRef = useRef(callback)
  callbackRef.current = callback

  useEffect(() => {
    if (!enabled) return
    callbackRef.current()
    const id = setInterval(() => callbackRef.current(), intervalMs)
    return () => clearInterval(id)
  }, [intervalMs, enabled])
}
