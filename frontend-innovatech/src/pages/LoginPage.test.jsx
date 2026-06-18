import { describe, it, expect, vi } from 'vitest'
import { render, screen, waitFor, fireEvent } from '@testing-library/react'
import { MemoryRouter, Routes, Route } from 'react-router-dom'
import { AuthProvider } from '../context/AuthContext.jsx'
import { NotifProvider } from '../context/NotifContext.jsx'
import LoginPage from './LoginPage.jsx'

function renderLogin() {
  return render(
    <AuthProvider>
      <NotifProvider>
        <MemoryRouter initialEntries={['/login']}>
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/dashboard" element={<div>Dashboard OK</div>} />
          </Routes>
        </MemoryRouter>
      </NotifProvider>
    </AuthProvider>
  )
}

describe('LoginPage', () => {
  beforeEach(() => sessionStorage.clear())

  it('muestra error con credenciales incorrectas', async () => {
    renderLogin()

    fireEvent.change(screen.getByPlaceholderText('admin@innovatech.cl'), { target: { value: 'foo@bar.com' } })
    fireEvent.change(screen.getByPlaceholderText('••••••••'), { target: { value: 'wrong' } })
    fireEvent.click(screen.getByText('Iniciar Sesión'))

    await waitFor(() => expect(screen.getByText('Credenciales incorrectas')).toBeInTheDocument(), { timeout: 2000 })
  })

  it('inicia sesión y navega al dashboard con credenciales válidas', async () => {
    renderLogin()

    fireEvent.change(screen.getByPlaceholderText('admin@innovatech.cl'), { target: { value: 'admin@innovatech.cl' } })
    fireEvent.change(screen.getByPlaceholderText('••••••••'), { target: { value: '1234' } })
    fireEvent.click(screen.getByText('Iniciar Sesión'))

    await waitFor(() => expect(screen.getByText('Dashboard OK')).toBeInTheDocument(), { timeout: 2000 })
  })

  it('autocompleta credenciales de prueba al hacer click', () => {
    renderLogin()
    fireEvent.click(screen.getByText('admin@innovatech.cl'))
    expect(screen.getByPlaceholderText('admin@innovatech.cl').value).toBe('admin@innovatech.cl')
    expect(screen.getByPlaceholderText('••••••••').value).toBe('1234')
  })
})
