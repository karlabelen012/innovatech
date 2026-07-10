import { describe, it, expect, vi, beforeEach } from 'vitest'
import { screen, waitFor, fireEvent } from '@testing-library/react'
import { renderPage } from './test-utils.jsx'
import ChatPage from './ChatPage.jsx'
import * as api from '../services/api.js'

const BRYAN = {
  id: 1, nombre: 'Bryan', apellido: 'Muñoz', email: 'bryan@innovatech.cl', rol: 'Admin', avatar: 'BM',
}

const INBOX = [
  { contactoEmail: 'karla@innovatech.cl', contactoNombre: 'Karla Herrera', ultimoMensaje: 'Vamos al 80%', noLeidos: 2, ultimoEnviadoPorMi: false },
]

const MENSAJES = [
  { id: 1, remitenteEmail: 'karla@innovatech.cl', destinatarioEmail: 'bryan@innovatech.cl', contenido: 'Vamos al 80%', fechaEnvio: '2026-06-26T10:00:00', leido: false },
]

function loginComoBryan() {
  sessionStorage.setItem('innovatech_user', JSON.stringify(BRYAN))
}

describe('ChatPage', () => {
  beforeEach(() => {
    vi.restoreAllMocks()
    sessionStorage.clear()
    loginComoBryan()
  })

  it('muestra la lista de contactos del equipo (excluyendo al usuario actual)', async () => {
    vi.spyOn(api, 'getInbox').mockResolvedValue([])
    vi.spyOn(api, 'getConversacion').mockResolvedValue([])

    renderPage(<ChatPage />)

    await waitFor(() => expect(screen.getAllByText('Karla Herrera').length).toBeGreaterThan(0))
    expect(screen.getByText('Admin Sistema')).toBeInTheDocument()
    expect(screen.queryByText('Bryan Muñoz')).not.toBeInTheDocument()
  })

  it('carga y muestra la conversación del contacto seleccionado', async () => {
    vi.spyOn(api, 'getInbox').mockResolvedValue(INBOX)
    vi.spyOn(api, 'getConversacion').mockResolvedValue(MENSAJES)
    vi.spyOn(api, 'marcarMensajesLeidos').mockResolvedValue({ actualizados: 1 })

    renderPage(<ChatPage />)

    await waitFor(() => expect(screen.getAllByText('Vamos al 80%').length).toBeGreaterThan(0))
    await waitFor(() => expect(api.marcarMensajesLeidos).toHaveBeenCalledWith('bryan@innovatech.cl', 'karla@innovatech.cl'))
  })

  it('muestra el contador de no leídos en la lista de contactos', async () => {
    vi.spyOn(api, 'getInbox').mockResolvedValue(INBOX)
    vi.spyOn(api, 'getConversacion').mockResolvedValue([])
    vi.spyOn(api, 'marcarMensajesLeidos').mockResolvedValue({ actualizados: 0 })

    renderPage(<ChatPage />)

    await waitFor(() => expect(screen.getByText('2')).toBeInTheDocument())
  })

  it('envía un mensaje y recarga la conversación', async () => {
    vi.spyOn(api, 'getInbox').mockResolvedValue([])
    vi.spyOn(api, 'getConversacion').mockResolvedValue([])
    vi.spyOn(api, 'enviarMensaje').mockResolvedValue({ id: 99 })

    renderPage(<ChatPage />)

    await waitFor(() => expect(screen.getByText('Sin mensajes')).toBeInTheDocument())

    const input = screen.getByPlaceholderText('Escribe un mensaje...')
    fireEvent.change(input, { target: { value: 'Hola Karla' } })
    fireEvent.click(screen.getByText('Enviar'))

    await waitFor(() => expect(api.enviarMensaje).toHaveBeenCalledWith(expect.objectContaining({
      remitenteEmail: 'bryan@innovatech.cl',
      destinatarioEmail: 'karla@innovatech.cl',
      contenido: 'Hola Karla',
    })))
  })
})
