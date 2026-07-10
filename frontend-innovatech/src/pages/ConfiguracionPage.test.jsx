import { describe, it, expect, beforeEach } from 'vitest'
import { screen, fireEvent } from '@testing-library/react'
import { renderPage } from './test-utils.jsx'
import ConfiguracionPage from './ConfiguracionPage.jsx'
import { NOTIF_PREF_KEY } from '../context/NotifContext.jsx'
import { POLL_INTERVAL_KEY } from '../utils/prefs.js'

describe('ConfiguracionPage', () => {
  beforeEach(() => localStorage.clear())

  it('inicia con notificaciones activadas y el intervalo por defecto', () => {
    renderPage(<ConfiguracionPage />)
    expect(screen.getByRole('switch')).toHaveAttribute('aria-checked', 'true')
    expect(screen.getByDisplayValue('15 segundos (recomendado)')).toBeInTheDocument()
  })

  it('guarda la preferencia de notificaciones desactivada', () => {
    renderPage(<ConfiguracionPage />)

    fireEvent.click(screen.getByRole('switch'))
    fireEvent.click(screen.getByText('Guardar Cambios'))

    expect(localStorage.getItem(NOTIF_PREF_KEY)).toBe('false')
  })

  it('guarda el intervalo de actualización seleccionado', () => {
    renderPage(<ConfiguracionPage />)

    const select = screen.getByDisplayValue('15 segundos (recomendado)')
    fireEvent.change(select, { target: { value: '30000' } })
    fireEvent.click(screen.getByText('Guardar Cambios'))

    expect(localStorage.getItem(POLL_INTERVAL_KEY)).toBe('30000')
  })

  it('muestra un toast de confirmación al guardar', async () => {
    renderPage(<ConfiguracionPage />)
    fireEvent.click(screen.getByText('Guardar Cambios'))
    expect(await screen.findByText('Configuración guardada correctamente')).toBeInTheDocument()
  })
})
