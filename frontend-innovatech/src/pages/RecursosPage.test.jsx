import { describe, it, expect, vi, beforeEach } from 'vitest'
import { screen, waitFor, fireEvent } from '@testing-library/react'
import { renderPage } from './test-utils.jsx'
import RecursosPage from './RecursosPage.jsx'
import * as api from '../services/api.js'

const EMPLEADOS = [
  { id: 1, nombre: 'Ana', apellido: 'Pérez', email: 'ana@x.cl', rol: 'Desarrolladora', disponibilidad: 'DISPONIBLE', horasSemanales: 40, habilidades: 'React' },
]
const ASIGNACIONES = [
  { id: 1, empleadoId: 1, proyectoId: 1, activo: true },
]
const PROYECTOS = [
  { id: 1, nombre: 'Portal Web', estado: 'EN_PROGRESO' },
]

describe('RecursosPage', () => {
  beforeEach(() => vi.restoreAllMocks())

  it('muestra la lista de empleados', async () => {
    vi.spyOn(api, 'getEmpleados').mockResolvedValue(EMPLEADOS)
    vi.spyOn(api, 'getAsignaciones').mockResolvedValue(ASIGNACIONES)
    vi.spyOn(api, 'getProyectos').mockResolvedValue(PROYECTOS)

    renderPage(<RecursosPage />)

    await waitFor(() => expect(screen.getByText('Ana Pérez')).toBeInTheDocument())
    expect(screen.getByText('Desarrolladora')).toBeInTheDocument()
  })

  it('cambia de pestaña a asignaciones', async () => {
    vi.spyOn(api, 'getEmpleados').mockResolvedValue(EMPLEADOS)
    vi.spyOn(api, 'getAsignaciones').mockResolvedValue(ASIGNACIONES)
    vi.spyOn(api, 'getProyectos').mockResolvedValue(PROYECTOS)

    renderPage(<RecursosPage />)
    await waitFor(() => expect(screen.getByText('Ana Pérez')).toBeInTheDocument())

    fireEvent.click(screen.getByText('🔗 Asignaciones'))
    await waitFor(() => expect(screen.getByText('Empleado #1')).toBeInTheDocument())
    expect(screen.getByText('Proyecto #1')).toBeInTheDocument()
  })

  it('muestra estado vacío cuando no hay empleados', async () => {
    vi.spyOn(api, 'getEmpleados').mockResolvedValue([])
    vi.spyOn(api, 'getAsignaciones').mockResolvedValue([])
    vi.spyOn(api, 'getProyectos').mockResolvedValue([])

    renderPage(<RecursosPage />)
    await waitFor(() => expect(screen.getByText('Sin empleados')).toBeInTheDocument())
  })

  it('actualiza disponibilidad de un empleado', async () => {
    vi.spyOn(api, 'getEmpleados').mockResolvedValue(EMPLEADOS)
    vi.spyOn(api, 'getAsignaciones').mockResolvedValue(ASIGNACIONES)
    vi.spyOn(api, 'getProyectos').mockResolvedValue(PROYECTOS)
    vi.spyOn(api, 'updateDisponibilidad').mockResolvedValue({})

    renderPage(<RecursosPage />)
    await waitFor(() => expect(screen.getByText('Ana Pérez')).toBeInTheDocument())

    const select = screen.getByDisplayValue('Disponible')
    fireEvent.change(select, { target: { value: 'OCUPADO' } })

    await waitFor(() => expect(api.updateDisponibilidad).toHaveBeenCalledWith(1, 'OCUPADO'))
  })
})
