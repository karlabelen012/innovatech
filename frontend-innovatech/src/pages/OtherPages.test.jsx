import { describe, it, expect, vi, beforeEach } from 'vitest'
import { screen, waitFor, fireEvent } from '@testing-library/react'
import { renderPage } from './test-utils.jsx'
import { ClientesPage, EquiposPage, PerfilPage, ReportesPage } from './OtherPages.jsx'
import * as api from '../services/api.js'

describe('ClientesPage', () => {
  beforeEach(() => vi.restoreAllMocks())

  it('muestra clientes agrupados a partir de los proyectos', async () => {
    vi.spyOn(api, 'getProyectos').mockResolvedValue([
      { id: 1, nombre: 'Portal Web', estado: 'EN_PROGRESO', avance: 40, responsable: 'Banco Andes' },
      { id: 2, nombre: 'App Móvil', estado: 'PENDIENTE', avance: 0, responsable: 'Banco Andes' },
      { id: 3, nombre: 'Migración Datos', estado: 'FINALIZADO', avance: 100, responsable: 'Retail Sur' },
    ])

    renderPage(<ClientesPage />)

    await waitFor(() => expect(screen.getByText('Banco Andes')).toBeInTheDocument())
    expect(screen.getByText('Retail Sur')).toBeInTheDocument()
    expect(screen.getByText('Portal Web')).toBeInTheDocument()
    expect(screen.getByText('2 proyectos')).toBeInTheDocument()
  })

  it('muestra estado vacío cuando no hay proyectos', async () => {
    vi.spyOn(api, 'getProyectos').mockResolvedValue([])
    renderPage(<ClientesPage />)
    await waitFor(() => expect(screen.getByText('Sin clientes')).toBeInTheDocument())
  })

  it('muestra error cuando falla la carga', async () => {
    vi.spyOn(api, 'getProyectos').mockRejectedValue(new Error('network'))
    renderPage(<ClientesPage />)
    await waitFor(() => expect(screen.getByText('No se pudo cargar la información')).toBeInTheDocument())
  })
})

describe('EquiposPage', () => {
  beforeEach(() => vi.restoreAllMocks())

  it('muestra equipos por proyecto activo con sus miembros', async () => {
    vi.spyOn(api, 'getProyectos').mockResolvedValue([
      { id: 1, nombre: 'Portal Web', estado: 'EN_PROGRESO', avance: 40, responsable: 'Banco Andes' },
      { id: 2, nombre: 'Migración Datos', estado: 'FINALIZADO', avance: 100, responsable: 'Retail Sur' },
    ])
    vi.spyOn(api, 'getEmpleados').mockResolvedValue([
      { id: 10, nombre: 'Ana', apellido: 'Pérez', rol: 'Desarrolladora' },
      { id: 11, nombre: 'Luis', apellido: 'Gómez', rol: 'QA' },
    ])
    vi.spyOn(api, 'getAsignaciones').mockResolvedValue([
      { id: 100, empleadoId: 10, proyectoId: 1, activo: true },
      { id: 101, empleadoId: 11, proyectoId: 1, activo: true },
      { id: 102, empleadoId: 10, proyectoId: 2, activo: true },
    ])

    renderPage(<EquiposPage />)

    await waitFor(() => expect(screen.getByText('Portal Web')).toBeInTheDocument())
    expect(screen.getByText('Ana Pérez')).toBeInTheDocument()
    expect(screen.getByText('Luis Gómez')).toBeInTheDocument()
    // Finalizado project excluded
    expect(screen.queryByText('Migración Datos')).not.toBeInTheDocument()
  })

  it('muestra estado vacío cuando no hay proyectos activos', async () => {
    vi.spyOn(api, 'getProyectos').mockResolvedValue([])
    vi.spyOn(api, 'getEmpleados').mockResolvedValue([])
    vi.spyOn(api, 'getAsignaciones').mockResolvedValue([])

    renderPage(<EquiposPage />)
    await waitFor(() => expect(screen.getByText('Sin equipos')).toBeInTheDocument())
  })

  it('muestra error cuando todas las llamadas fallan', async () => {
    vi.spyOn(api, 'getProyectos').mockRejectedValue(new Error('x'))
    vi.spyOn(api, 'getEmpleados').mockRejectedValue(new Error('x'))
    vi.spyOn(api, 'getAsignaciones').mockRejectedValue(new Error('x'))

    renderPage(<EquiposPage />)
    await waitFor(() => expect(screen.getByText('No se pudo cargar la información')).toBeInTheDocument())
  })
})

describe('ReportesPage', () => {
  beforeEach(() => {
    vi.restoreAllMocks()
    sessionStorage.clear()
  })

  const DASH = {
    totalProyectos: 12, proyectosActivos: 5, proyectosFinalizados: 6,
    promedioAvance: 68, totalEmpleados: 24, empleadosDisponibles: 9,
  }

  it('muestra el resumen actual y permite guardar un reporte', async () => {
    vi.spyOn(api, 'getDashboard').mockResolvedValue(DASH)
    vi.spyOn(api, 'getResumenRecursos').mockResolvedValue({ totalEmpleados: 24, disponibles: 9 })

    renderPage(<ReportesPage />)

    await waitFor(() => expect(screen.getByText('Reporte actual')).toBeInTheDocument())
    expect(screen.getByText('12')).toBeInTheDocument() // total proyectos
    expect(screen.getByText('68%')).toBeInTheDocument() // avance promedio
    expect(screen.getByText('Sin reportes guardados')).toBeInTheDocument()

    fireEvent.click(screen.getByText('💾 Guardar Reporte'))

    await waitFor(() => expect(screen.queryByText('Sin reportes guardados')).not.toBeInTheDocument())
    // The saved row should now show the same stats
    const rows = screen.getAllByText('12')
    expect(rows.length).toBeGreaterThanOrEqual(2) // current summary + saved row

    // Persisted to sessionStorage
    const stored = JSON.parse(sessionStorage.getItem('innovatech_reportes'))
    expect(stored).toHaveLength(1)
    expect(stored[0].totalProyectos).toBe(12)
  })

  it('permite eliminar un reporte guardado', async () => {
    vi.spyOn(api, 'getDashboard').mockResolvedValue(DASH)
    vi.spyOn(api, 'getResumenRecursos').mockResolvedValue({ totalEmpleados: 24, disponibles: 9 })

    renderPage(<ReportesPage />)
    await waitFor(() => expect(screen.getByText('Reporte actual')).toBeInTheDocument())

    fireEvent.click(screen.getByText('💾 Guardar Reporte'))
    await waitFor(() => expect(screen.queryByText('Sin reportes guardados')).not.toBeInTheDocument())

    fireEvent.click(screen.getByTitle('Eliminar'))
    await waitFor(() => expect(screen.getByText('Sin reportes guardados')).toBeInTheDocument())

    const stored = JSON.parse(sessionStorage.getItem('innovatech_reportes'))
    expect(stored).toHaveLength(0)
  })

  it('carga reportes previamente guardados desde sessionStorage', async () => {
    sessionStorage.setItem('innovatech_reportes', JSON.stringify([{
      id: 1, fecha: '01/01/2026', totalProyectos: 7, proyectosActivos: 3,
      proyectosFinalizados: 4, promedioAvance: 50, totalEmpleados: 20, empleadosDisponibles: 5,
    }]))
    vi.spyOn(api, 'getDashboard').mockResolvedValue(DASH)
    vi.spyOn(api, 'getResumenRecursos').mockResolvedValue({ totalEmpleados: 24, disponibles: 9 })

    renderPage(<ReportesPage />)

    await waitFor(() => expect(screen.getByText('01/01/2026')).toBeInTheDocument())
  })

  it('muestra error cuando ambas llamadas fallan', async () => {
    vi.spyOn(api, 'getDashboard').mockRejectedValue(new Error('x'))
    vi.spyOn(api, 'getResumenRecursos').mockRejectedValue(new Error('x'))

    renderPage(<ReportesPage />)
    await waitFor(() => expect(screen.getByText('No se pudo cargar la información')).toBeInTheDocument())
  })
})

describe('PerfilPage', () => {
  beforeEach(() => {
    sessionStorage.setItem('innovatech_user', JSON.stringify({
      id: 1, nombre: 'Bryan', apellido: 'Muñoz', email: 'bryan@innovatech.cl', rol: 'Admin', avatar: 'BM',
    }))
  })

  it('renderiza y permite editar y guardar el perfil', async () => {
    renderPage(<PerfilPage />)
    expect(screen.getByText('Mi Perfil')).toBeInTheDocument()

    const nombreInput = screen.getByDisplayValue('Bryan')
    fireEvent.change(nombreInput, { target: { value: 'Nuevo Nombre' } })
    fireEvent.click(screen.getByText('Guardar Cambios'))

    await waitFor(() => expect(screen.getByText('Nuevo Nombre', { exact: false })).toBeInTheDocument())
  })
})
