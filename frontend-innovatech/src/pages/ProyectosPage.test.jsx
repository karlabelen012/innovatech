import { describe, it, expect, vi, beforeEach } from 'vitest'
import { screen, waitFor, fireEvent } from '@testing-library/react'
import { renderPage } from './test-utils.jsx'
import ProyectosPage from './ProyectosPage.jsx'
import * as api from '../services/api.js'

const PROYECTOS = [
  { id: 1, nombre: 'Portal Web', descripcion: 'Sitio corporativo', estado: 'EN_PROGRESO', avance: 40, responsable: 'Banco Andes' },
  { id: 2, nombre: 'App Móvil', descripcion: 'App de clientes', estado: 'PENDIENTE', avance: 0, responsable: 'Retail Sur' },
]

describe('ProyectosPage', () => {
  beforeEach(() => vi.restoreAllMocks())

  it('carga y muestra la lista de proyectos', async () => {
    vi.spyOn(api, 'getProyectos').mockResolvedValue(PROYECTOS)
    renderPage(<ProyectosPage />)

    await waitFor(() => expect(screen.getByText('Portal Web')).toBeInTheDocument())
    expect(screen.getByText('App Móvil')).toBeInTheDocument()
    expect(screen.getByText('Banco Andes')).toBeInTheDocument()
  })

  it('muestra estado vacío cuando no hay proyectos', async () => {
    vi.spyOn(api, 'getProyectos').mockResolvedValue([])
    renderPage(<ProyectosPage />)
    await waitFor(() => expect(screen.getByText('Sin proyectos')).toBeInTheDocument())
  })

  it('abre el modal de creación y valida nombre obligatorio', async () => {
    vi.spyOn(api, 'getProyectos').mockResolvedValue([])
    renderPage(<ProyectosPage />)
    await waitFor(() => expect(screen.getByText('Sin proyectos')).toBeInTheDocument())

    fireEvent.click(screen.getByText('+ Nuevo Proyecto'))
    expect(screen.getByRole('heading', { name: '+ Nuevo Proyecto' })).toBeInTheDocument()

    fireEvent.click(screen.getByText('Guardar'))
    await waitFor(() => expect(screen.getByText('El nombre es obligatorio')).toBeInTheDocument())
  })

  it('crea un proyecto correctamente', async () => {
    vi.spyOn(api, 'getProyectos').mockResolvedValue([])
    vi.spyOn(api, 'createProyecto').mockResolvedValue({ id: 3 })
    renderPage(<ProyectosPage />)
    await waitFor(() => expect(screen.getByText('Sin proyectos')).toBeInTheDocument())

    fireEvent.click(screen.getByText('+ Nuevo Proyecto'))
    const nombreInput = screen.getByPlaceholderText('Ej: Portal Fintech')
    fireEvent.change(nombreInput, { target: { value: 'Nuevo Proyecto X' } })
    fireEvent.click(screen.getByText('Guardar'))

    await waitFor(() => expect(api.createProyecto).toHaveBeenCalledWith(
      expect.objectContaining({ nombre: 'Nuevo Proyecto X' })
    ))
  })

  it('abre las tareas de un proyecto y muestra la lista', async () => {
    vi.spyOn(api, 'getProyectos').mockResolvedValue(PROYECTOS)
    vi.spyOn(api, 'getTareasPorProyecto').mockResolvedValue([
      { id: 1, titulo: 'Diseñar base de datos', estado: 'PENDIENTE', responsable: 'Bryan Muñoz', proyectoId: 1 },
    ])
    renderPage(<ProyectosPage />)
    await waitFor(() => expect(screen.getByText('Portal Web')).toBeInTheDocument())

    fireEvent.click(screen.getAllByTitle('Ver tareas')[0])

    await waitFor(() => expect(api.getTareasPorProyecto).toHaveBeenCalledWith(1))
    expect(await screen.findByText('Diseñar base de datos')).toBeInTheDocument()
  })

  it('crea una tarea dentro de un proyecto', async () => {
    vi.spyOn(api, 'getProyectos').mockResolvedValue(PROYECTOS)
    vi.spyOn(api, 'getTareasPorProyecto').mockResolvedValue([])
    vi.spyOn(api, 'createTarea').mockResolvedValue({ id: 1 })
    renderPage(<ProyectosPage />)
    await waitFor(() => expect(screen.getByText('Portal Web')).toBeInTheDocument())

    fireEvent.click(screen.getAllByTitle('Ver tareas')[0])
    await waitFor(() => expect(screen.getByText('Sin tareas')).toBeInTheDocument())

    fireEvent.click(screen.getByText('+ Nueva Tarea'))
    fireEvent.change(screen.getByPlaceholderText('Ej: Diseñar base de datos'), { target: { value: 'Implementar API' } })
    fireEvent.click(screen.getByText('Guardar'))

    await waitFor(() => expect(api.createTarea).toHaveBeenCalledWith(
      expect.objectContaining({ titulo: 'Implementar API', proyectoId: 1 })
    ))
  })
})
