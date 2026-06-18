import { describe, it, expect, vi, beforeEach } from 'vitest'
import { screen, waitFor } from '@testing-library/react'
import { renderPage } from './test-utils.jsx'
import KpisPage from './KpisPage.jsx'
import * as api from '../services/api.js'

const KPIS = [
  { id: 1, nombre: 'Proyectos Completados', valor: 85, unidad: '%', categoria: 'PROYECTOS', descripcion: 'Tasa de finalización' },
  { id: 2, nombre: 'Utilización Recursos', valor: 78, unidad: '%', categoria: 'RECURSOS', descripcion: 'Carga laboral' },
]

describe('KpisPage', () => {
  beforeEach(() => vi.restoreAllMocks())

  it('muestra los KPIs cargados desde la API', async () => {
    vi.spyOn(api, 'getKpis').mockResolvedValue(KPIS)
    renderPage(<KpisPage />)

    await waitFor(() => expect(screen.getByText('Proyectos Completados')).toBeInTheDocument())
    expect(screen.getByText('Utilización Recursos')).toBeInTheDocument()
  })

  it('usa datos de ejemplo y avisa cuando falla la API', async () => {
    vi.spyOn(api, 'getKpis').mockRejectedValue(new Error('x'))
    renderPage(<KpisPage />)

    await waitFor(() => expect(screen.getByText('Usando datos de ejemplo')).toBeInTheDocument())
  })

  it('filtra KPIs por categoría', async () => {
    vi.spyOn(api, 'getKpis').mockResolvedValue(KPIS)
    vi.spyOn(api, 'getKpisByCategory').mockResolvedValue([KPIS[1]])
    renderPage(<KpisPage />)

    await waitFor(() => expect(screen.getByText('Proyectos Completados')).toBeInTheDocument())

    // Click the filter button specifically (not other elements that may also say "RECURSOS")
    const buttons = screen.getAllByRole('button')
    const recursosBtn = buttons.find(btn => btn.textContent === 'RECURSOS')
    recursosBtn.click()

    await waitFor(() => expect(api.getKpisByCategory).toHaveBeenCalledWith('RECURSOS'))
  })
})
