import { createContext, useContext, useState, useCallback, useRef } from 'react'

const NotifContext = createContext(null)

export const NOTIF_PREF_KEY = 'innovatech_notif_enabled'

function popupsEnabled() {
  try {
    const v = localStorage.getItem(NOTIF_PREF_KEY)
    return v === null ? true : v === 'true'
  } catch { return true }
}

export function NotifProvider({ children }) {
  const [toasts, setToasts] = useState([])
  const [historial, setHistorial] = useState([])
  const counter = useRef(0)

  const push = useCallback((msg, type = 'info', duration = 3500) => {
    const id = ++counter.current

    setHistorial(h => [{ id, msg, type, fecha: new Date(), leida: false }, ...h].slice(0, 30))

    if (popupsEnabled()) {
      setToasts(t => [...t, { id, msg, type }])
      setTimeout(() => setToasts(t => t.filter(x => x.id !== id)), duration)
    }
  }, [])

  const remove = useCallback((id) => setToasts(t => t.filter(x => x.id !== id)), [])

  const marcarHistorialLeido = useCallback(() => {
    setHistorial(h => h.map(n => ({ ...n, leida: true })))
  }, [])

  const noLeidas = historial.filter(n => !n.leida).length

  return (
    <NotifContext.Provider value={{ toasts, push, remove, historial, noLeidas, marcarHistorialLeido }}>
      {children}
    </NotifContext.Provider>
  )
}

export const useNotif = () => useContext(NotifContext)
