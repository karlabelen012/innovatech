import { createContext, useContext, useState, useCallback } from 'react'

const AuthContext = createContext(null)

// Mock users for login (no backend auth)
const MOCK_USERS = [
  { id: 1, nombre: 'Bryan', apellido: 'Muñoz', email: 'bryan@innovatech.cl', rol: 'Admin', avatar: 'BM' },
  { id: 2, nombre: 'Karla', apellido: 'Herrera', email: 'karla@innovatech.cl', rol: 'Gestor', avatar: 'KH' },
  { id: 3, nombre: 'Admin', apellido: 'Sistema', email: 'admin@innovatech.cl', rol: 'Admin', avatar: 'AS' },
]

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    try {
      const saved = sessionStorage.getItem('innovatech_user')
      return saved ? JSON.parse(saved) : null
    } catch { return null }
  })

  const login = useCallback((email, password) => {
    const found = MOCK_USERS.find(u => u.email === email)
    if (found && password === '1234') {
      sessionStorage.setItem('innovatech_user', JSON.stringify(found))
      setUser(found)
      return { ok: true }
    }
    return { ok: false, error: 'Credenciales incorrectas' }
  }, [])

  const logout = useCallback(() => {
    sessionStorage.removeItem('innovatech_user')
    setUser(null)
  }, [])

  const updateProfile = useCallback((data) => {
    const updated = { ...user, ...data }
    sessionStorage.setItem('innovatech_user', JSON.stringify(updated))
    setUser(updated)
  }, [user])

  return (
    <AuthContext.Provider value={{ user, login, logout, updateProfile, isAuthenticated: !!user }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be inside AuthProvider')
  return ctx
}
