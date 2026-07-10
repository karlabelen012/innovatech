import { useState, useRef, useEffect, useCallback } from 'react'
import { useAuth, TEAM_USERS } from '../context/AuthContext.jsx'
import { useNotif } from '../context/NotifContext.jsx'
import { usePolling } from '../hooks/usePolling.js'
import { getInbox, getConversacion, enviarMensaje, marcarMensajesLeidos } from '../services/api.js'
import { Avatar, Spinner, EmptyState } from '../atoms/index.jsx'

export default function ChatPage() {
  const { user } = useAuth()
  const { push } = useNotif()
  const contactos = TEAM_USERS.filter(u => u.email !== user.email)

  const [inbox, setInbox] = useState({})
  const [selected, setSelected] = useState(contactos[0] || null)
  const [mensajes, setMensajes] = useState([])
  const [texto, setTexto] = useState('')
  const [loadingMsgs, setLoadingMsgs] = useState(true)
  const [enviando, setEnviando] = useState(false)
  const prevUnreadTotal = useRef(0)
  const firstLoad = useRef(true)
  const bottomRef = useRef(null)

  const loadInbox = useCallback(async () => {
    try {
      const data = await getInbox(user.email)
      const map = {}
      data.forEach(c => { map[c.contactoEmail] = c })
      setInbox(map)

      const total = data.reduce((acc, c) => acc + (c.noLeidos || 0), 0)
      if (!firstLoad.current && total > prevUnreadTotal.current) {
        const conNuevos = data.find(c => c.noLeidos > 0 && c.contactoEmail !== selected?.email)
        if (conNuevos) push(`Nuevo mensaje de ${conNuevos.contactoNombre || conNuevos.contactoEmail}`, 'info')
      }
      prevUnreadTotal.current = total
      firstLoad.current = false
    } catch {
      // BFF/ms-mensajeria caído: no interrumpe el resto de la app
    }
  }, [user.email, push, selected])

  usePolling(loadInbox, 8000)

  const loadConversacion = useCallback(async () => {
    if (!selected) return
    try {
      const data = await getConversacion(user.email, selected.email)
      setMensajes(data)
      if (data.some(m => m.destinatarioEmail === user.email && !m.leido)) {
        await marcarMensajesLeidos(user.email, selected.email)
        loadInbox()
      }
    } catch {
      push('No se pudo cargar la conversación. Verifica ms-mensajeria.', 'error')
    } finally {
      setLoadingMsgs(false)
    }
  }, [selected, user.email, push, loadInbox])

  useEffect(() => { setLoadingMsgs(true); loadConversacion() }, [selected])
  usePolling(loadConversacion, 5000, !!selected)

  useEffect(() => { bottomRef.current?.scrollIntoView({ behavior: 'smooth' }) }, [mensajes])

  const handleEnviar = async (e) => {
    e.preventDefault()
    if (!texto.trim() || !selected || enviando) return
    setEnviando(true)
    try {
      await enviarMensaje({
        remitenteEmail: user.email,
        remitenteNombre: `${user.nombre} ${user.apellido}`,
        destinatarioEmail: selected.email,
        destinatarioNombre: `${selected.nombre} ${selected.apellido}`,
        contenido: texto.trim(),
      })
      setTexto('')
      await loadConversacion()
    } catch {
      push('No se pudo enviar el mensaje', 'error')
    } finally {
      setEnviando(false)
    }
  }

  return (
    <div className="animate-fade-in" style={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <h2 style={{ fontFamily: 'var(--font-display)', fontWeight: 800, fontSize: 22, marginBottom: 4 }}>Chat Interno</h2>
      <p style={{ color: 'var(--text-muted)', fontSize: 12, marginBottom: 20 }}>Mensajería en tiempo real entre el equipo de Innovatech</p>

      <div style={{
        flex: 1, display: 'grid', gridTemplateColumns: '260px 1fr',
        background: 'var(--bg-card)', border: '1px solid var(--border)',
        borderRadius: 'var(--radius-lg)', overflow: 'hidden', minHeight: 480,
      }}>
        {/* Contactos */}
        <div style={{ borderRight: '1px solid var(--border)', display: 'flex', flexDirection: 'column' }}>
          <div style={{ padding: '14px 16px', borderBottom: '1px solid var(--border)', fontSize: 11, fontWeight: 600, color: 'var(--text-muted)', letterSpacing: '0.05em' }}>
            CONTACTOS
          </div>
          <div style={{ overflowY: 'auto', flex: 1 }}>
            {contactos.map(c => {
              const info = inbox[c.email]
              const active = selected?.email === c.email
              return (
                <button key={c.email} onClick={() => setSelected(c)}
                  style={{
                    width: '100%', display: 'flex', alignItems: 'center', gap: 10,
                    padding: '12px 16px', background: active ? 'var(--teal-dim)' : 'transparent',
                    border: 'none', borderBottom: '1px solid var(--border)', cursor: 'pointer', textAlign: 'left',
                  }}>
                  <Avatar initials={c.avatar} size={36} />
                  <div style={{ flex: 1, minWidth: 0 }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                      <span style={{ fontSize: 13, fontWeight: 600, color: active ? 'var(--teal)' : 'var(--text-primary)' }}>
                        {c.nombre} {c.apellido}
                      </span>
                      {info?.noLeidos > 0 && (
                        <span style={{
                          background: 'var(--red)', color: '#fff', fontSize: 10, fontWeight: 700,
                          borderRadius: '50%', width: 18, height: 18, display: 'flex',
                          alignItems: 'center', justifyContent: 'center', flexShrink: 0,
                        }}>{info.noLeidos}</span>
                      )}
                    </div>
                    <div style={{ fontSize: 11, color: 'var(--text-muted)', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                      {info?.ultimoMensaje || 'Sin mensajes aún'}
                    </div>
                  </div>
                </button>
              )
            })}
          </div>
        </div>

        {/* Conversación */}
        <div style={{ display: 'flex', flexDirection: 'column' }}>
          {!selected ? (
            <div style={{ flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
              <EmptyState icon="💬" title="Selecciona un contacto" />
            </div>
          ) : (
            <>
              <div style={{ padding: '14px 16px', borderBottom: '1px solid var(--border)', display: 'flex', alignItems: 'center', gap: 10 }}>
                <Avatar initials={selected.avatar} size={32} />
                <div>
                  <div style={{ fontSize: 13, fontWeight: 600 }}>{selected.nombre} {selected.apellido}</div>
                  <div style={{ fontSize: 11, color: 'var(--text-muted)' }}>{selected.rol}</div>
                </div>
              </div>

              <div style={{ flex: 1, overflowY: 'auto', padding: 16, display: 'flex', flexDirection: 'column', gap: 8 }}>
                {loadingMsgs ? (
                  <div style={{ textAlign: 'center', padding: 40 }}><Spinner size={24} /></div>
                ) : mensajes.length === 0 ? (
                  <EmptyState icon="✉️" title="Sin mensajes" description="Envía el primer mensaje para iniciar la conversación." />
                ) : (
                  mensajes.map(m => {
                    const esMio = m.remitenteEmail === user.email
                    return (
                      <div key={m.id} style={{ display: 'flex', justifyContent: esMio ? 'flex-end' : 'flex-start' }}>
                        <div style={{
                          maxWidth: '70%', padding: '8px 12px', borderRadius: 'var(--radius-md)',
                          background: esMio ? 'var(--teal-dim)' : 'var(--bg-elevated)',
                          border: `1px solid ${esMio ? 'var(--border-bright)' : 'var(--border)'}`,
                        }}>
                          <div style={{ fontSize: 13, color: 'var(--text-primary)' }}>{m.contenido}</div>
                          <div style={{ fontSize: 10, color: 'var(--text-muted)', marginTop: 4, textAlign: 'right' }}>
                            {new Date(m.fechaEnvio).toLocaleTimeString('es-CL', { hour: '2-digit', minute: '2-digit' })}
                          </div>
                        </div>
                      </div>
                    )
                  })
                )}
                <div ref={bottomRef} />
              </div>

              <form onSubmit={handleEnviar} style={{ display: 'flex', gap: 10, padding: 16, borderTop: '1px solid var(--border)' }}>
                <input
                  value={texto} onChange={e => setTexto(e.target.value)}
                  placeholder="Escribe un mensaje..."
                  style={{
                    flex: 1, background: 'var(--bg-void)', border: '1px solid var(--border)',
                    borderRadius: 'var(--radius-sm)', padding: '10px 14px', color: 'var(--text-primary)',
                    fontSize: 13, outline: 'none', fontFamily: 'var(--font-body)',
                  }}
                />
                <button type="submit" disabled={!texto.trim() || enviando}
                  style={{
                    background: 'var(--teal)', color: '#0a0d14', border: 'none',
                    borderRadius: 'var(--radius-sm)', padding: '10px 20px', fontSize: 13,
                    fontWeight: 600, cursor: 'pointer', opacity: (!texto.trim() || enviando) ? 0.5 : 1,
                  }}>
                  {enviando ? <Spinner size={14} color="#0a0d14" /> : 'Enviar'}
                </button>
              </form>
            </>
          )}
        </div>
      </div>
    </div>
  )
}
