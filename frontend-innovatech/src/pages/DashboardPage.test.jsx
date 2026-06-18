import { describe, it, expect, vi, beforeEach } from 'vitest'
import { screen, waitFor } from '@testing-library/react'
import { renderPage } from './test-utils.jsx'
import DashboardPage from './DashboardPage.jsx'
import * as api from '../services/api.js'

const DASH = {
  totalProyectos: 9, proyectosActivos: 4, proyectosFinalizados: 5, proyectosPendientes: 0,
  promedioAvance: 70, totalEmpleados: 15, empleadosDisponibles: 6,
  empleadosOcupados: 9, asignacionesActivas: 11,
  proyectosRecientes: [
    { id: 1, nombre: 'Portal Web', estado: 'EN_PROGRESO', avance: 40, responsable: 'Banco Andes' },
  ],
  kpisPorCategoria: { PROYECTOS: 80, RECURSOS: 70, PRODUCTIVIDAD: 90, FINANCIERO: 60 },
}

describe('DashboardPage', () => {
  beforeEach(() => vi.restoreAllMocks())

  it('muestra los datos del dashboard cuando el BFF responde', async () => {
    vi.spyOn(api, 'getDashboard').mockResolvedValue(DASH)
    renderPage(<DashboardPage />)

    // Use getAllByText since '11' appears in multiple places (KpiCard + resource stats)
    await waitFor(() => expect(screen.getAllByText('11').length).toBeGreaterThan(0))
    expect(screen.getByText('Portal Web')).toBeInTheDocument()
    expect(screen.queryByText(/No se pudo conectar al BFF/)).not.toBeInTheDocument()
  })

  it('muestra datos de ejemplo y aviso cuando falla el BFF', async () => {
    vi.spyOn(api, 'getDashboard').mockRejectedValue(new Error('x'))
    renderPage(<DashboardPage />)

    await waitFor(() => expect(screen.getByText(/No se pudo conectar al BFF/)).toBeInTheDocument())
  })
})
