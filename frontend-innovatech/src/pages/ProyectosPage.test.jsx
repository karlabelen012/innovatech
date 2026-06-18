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
})
