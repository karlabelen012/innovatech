import { createContext, useContext, useState, useCallback, useRef } from 'react'

const NotifContext = createContext(null)

export function NotifProvider({ children }) {
  const [toasts, setToasts] = useState([])
  const counter = useRef(0)

  const push = useCallback((msg, type = 'info', duration = 3500) => {
    const id = ++counter.current
    setToasts(t => [...t, { id, msg, type }])
    setTimeout(() => setToasts(t => t.filter(x => x.id !== id)), duration)
  }, [])

  const remove = useCallback((id) => setToasts(t => t.filter(x => x.id !== id)), [])

  return (
    <NotifContext.Provider value={{ toasts, push, remove }}>
      {children}
    </NotifContext.Provider>
  )
}

export const useNotif = () => useContext(NotifContext)
