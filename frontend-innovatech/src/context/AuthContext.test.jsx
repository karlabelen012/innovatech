import { describe, it, expect, beforeEach, vi } from 'vitest'
import { render, screen, act, fireEvent, waitFor } from '@testing-library/react'
import { AuthProvider, useAuth } from './AuthContext.jsx'

// El login real lo hace el BFF (POST /api/auth/login); se mockea la llamada HTTP.
vi.mock('../services/api.js', () => ({
  login: vi.fn((email, password) => {
    const validos = {
      'admin@innovatech.cl': '1234',
      'bryan@innovatech.cl': '1234',
      'karla@innovatech.cl': '1234',
    }
    if (validos[email] === password) {
      return Promise.resolve({ token: 'fake.jwt.token', email })
    }
    return Promise.reject(new Error('401 Credenciales inválidas'))
  }),
}))

// Componente auxiliar para exponer el contexto en los tests
function AuthConsumer({ action }) {
  const auth = useAuth()
  return (
    <div>
      <span data-testid="authenticated">{String(auth.isAuthenticated)}</span>
      <span data-testid="user">{auth.user ? auth.user.nombre : 'null'}</span>
      <span data-testid="rol">{auth.user ? auth.user.rol : 'null'}</span>
      <button onClick={() => action(auth)}>accion</button>
    </div>
  )
}

function renderAuth(action = () => {}) {
  return render(
    <AuthProvider>
      <AuthConsumer action={action} />
    </AuthProvider>
  )
}

describe('AuthContext', () => {
  beforeEach(() => sessionStorage.clear())

  it('inicia sin usuario autenticado', () => {
    renderAuth()
    expect(screen.getByTestId('authenticated').textContent).toBe('false')
    expect(screen.getByTestId('user').textContent).toBe('null')
  })

  it('login con credenciales válidas autentica al usuario', async () => {
    let resultado
    renderAuth((auth) => {
      auth.login('admin@innovatech.cl', '1234').then(r => { resultado = r })
    })

    fireEvent.click(screen.getByText('accion'))

    await waitFor(() => expect(resultado).toEqual({ ok: true }))
    expect(screen.getByTestId('authenticated').textContent).toBe('true')
    expect(screen.getByTestId('user').textContent).toBe('Admin')
  })

  it('login con credenciales incorrectas no autentica', async () => {
    let resultado
    renderAuth((auth) => {
      auth.login('admin@innovatech.cl', 'wrong').then(r => { resultado = r })
    })

    fireEvent.click(screen.getByText('accion'))

    await waitFor(() => expect(resultado).toEqual({ ok: false, error: 'Credenciales incorrectas' }))
    expect(screen.getByTestId('authenticated').textContent).toBe('false')
  })

  it('login con email inexistente no autentica', async () => {
    let resultado
    renderAuth((auth) => {
      auth.login('noexiste@x.cl', '1234').then(r => { resultado = r })
    })

    fireEvent.click(screen.getByText('accion'))

    await waitFor(() => expect(resultado).toBeDefined())
    expect(resultado.ok).toBe(false)
    expect(screen.getByTestId('authenticated').textContent).toBe('false')
  })

  it('logout elimina la sesión del usuario', async () => {
    let step = 0
    renderAuth((auth) => {
      if (step === 0) { auth.login('bryan@innovatech.cl', '1234'); step++ }
      else { auth.logout() }
    })

    fireEvent.click(screen.getByText('accion'))
    await waitFor(() => expect(screen.getByTestId('authenticated').textContent).toBe('true'))

    act(() => fireEvent.click(screen.getByText('accion')))
    expect(screen.getByTestId('authenticated').textContent).toBe('false')
    expect(screen.getByTestId('user').textContent).toBe('null')
    expect(sessionStorage.getItem('innovatech_user')).toBeNull()
    expect(sessionStorage.getItem('innovatech_token')).toBeNull()
  })

  it('persiste la sesión en sessionStorage al hacer login', async () => {
    renderAuth((auth) => auth.login('karla@innovatech.cl', '1234'))
    fireEvent.click(screen.getByText('accion'))

    await waitFor(() => expect(sessionStorage.getItem('innovatech_user')).not.toBeNull())

    const stored = JSON.parse(sessionStorage.getItem('innovatech_user'))
    expect(stored.email).toBe('karla@innovatech.cl')
    expect(stored.rol).toBe('Gestor')
    expect(sessionStorage.getItem('innovatech_token')).toBe('fake.jwt.token')
  })

  it('recupera la sesión desde sessionStorage al montar', () => {
    sessionStorage.setItem('innovatech_user', JSON.stringify({
      id: 1, nombre: 'Bryan', apellido: 'Muñoz',
      email: 'bryan@innovatech.cl', rol: 'Admin', avatar: 'BM',
    }))

    renderAuth()

    expect(screen.getByTestId('authenticated').textContent).toBe('true')
    expect(screen.getByTestId('user').textContent).toBe('Bryan')
  })

  it('updateProfile actualiza los datos del usuario', async () => {
    renderAuth((auth) => {
      if (!auth.user) { auth.login('bryan@innovatech.cl', '1234') }
      else { auth.updateProfile({ nombre: 'Bryan Updated' }) }
    })

    fireEvent.click(screen.getByText('accion'))
    await waitFor(() => expect(screen.getByTestId('user').textContent).toBe('Bryan'))

    act(() => fireEvent.click(screen.getByText('accion')))
    expect(screen.getByTestId('user').textContent).toBe('Bryan Updated')

    const stored = JSON.parse(sessionStorage.getItem('innovatech_user'))
    expect(stored.nombre).toBe('Bryan Updated')
  })

  it('useAuth fuera del provider lanza error', () => {
    const spy = vi.spyOn(console, 'error').mockImplementation(() => {})
    expect(() => render(<AuthConsumer action={() => {}} />)).toThrow()
    spy.mockRestore()
  })
})
