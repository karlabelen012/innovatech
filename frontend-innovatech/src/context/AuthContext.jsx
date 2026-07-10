import { createContext, useContext, useState, useCallback } from 'react'
import { login as apiLogin } from '../services/api.js'

const AuthContext = createContext(null)

// Perfil visual de los usuarios validos. La autenticacion real (contraseña,
// emision del JWT) la hace el BFF; esto solo enriquece el nombre/avatar a
// mostrar una vez que el login contra el backend fue exitoso.
const PERFILES = [
  { id: 1, nombre: 'Bryan', apellido: 'Muñoz', email: 'bryan@innovatech.cl', rol: 'Admin', avatar: 'BM' },
  { id: 2, nombre: 'Karla', apellido: 'Herrera', email: 'karla@innovatech.cl', rol: 'Gestor', avatar: 'KH' },
  { id: 3, nombre: 'Admin', apellido: 'Sistema', email: 'admin@innovatech.cl', rol: 'Admin', avatar: 'AS' },
]

// Usuarios del equipo disponibles para chatear (excluye al usuario autenticado)
export const TEAM_USERS = PERFILES

function perfilPorEmail(email) {
  return PERFILES.find(u => u.email === email)
    || { id: 0, nombre: email, apellido: '', email, rol: 'Usuario', avatar: '??' }
}

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    try {
      const saved = sessionStorage.getItem('innovatech_user')
      return saved ? JSON.parse(saved) : null
    } catch { return null }
  })

  const login = useCallback(async (email, password) => {
    try {
      const { token } = await apiLogin(email, password)
      const authenticatedUser = perfilPorEmail(email)
      sessionStorage.setItem('innovatech_token', token)
      sessionStorage.setItem('innovatech_user', JSON.stringify(authenticatedUser))
      setUser(authenticatedUser)
      return { ok: true }
    } catch {
      return { ok: false, error: 'Credenciales incorrectas' }
    }
  }, [])

  const logout = useCallback(() => {
    sessionStorage.removeItem('innovatech_user')
    sessionStorage.removeItem('innovatech_token')
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
