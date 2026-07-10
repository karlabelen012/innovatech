import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { useNotif } from '../context/NotifContext.jsx'
import { Avatar, Btn, Spinner } from '../atoms/index.jsx'

const TYPE_ICON = { success: '✅', error: '⚠️', warning: '⚠️', info: '📋' }

function relativeTime(date) {
  const diffMin = Math.floor((Date.now() - new Date(date).getTime()) / 60000)
  if (diffMin < 1) return 'ahora'
  if (diffMin < 60) return `hace ${diffMin}m`
  const diffH = Math.floor(diffMin / 60)
  if (diffH < 24) return `hace ${diffH}h`
  return `hace ${Math.floor(diffH / 24)}d`
}

export default function TopBar({ bffOk, onRefresh, refreshing, title }) {
  const { user, logout } = useAuth()
  const { historial, noLeidas, marcarHistorialLeido } = useNotif()
  const navigate = useNavigate()
  const [menuOpen, setMenuOpen] = useState(false)
  const [notifOpen, setNotifOpen] = useState(false)

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  const toggleNotif = () => {
    setNotifOpen(o => {
      const next = !o
      if (next) marcarHistorialLeido()
      return next
    })
    setMenuOpen(false)
  }

  return (
    <header style={{
      height: 60, background: 'var(--bg-deep)', borderBottom: '1px solid var(--border)',
      display: 'flex', alignItems: 'center', justifyContent: 'space-between',
      padding: '0 24px', position: 'sticky', top: 0, zIndex: 100, flexShrink: 0,
    }}>
      {/* Left: title */}
      <h1 style={{ fontFamily: 'var(--font-display)', fontWeight: 700, fontSize: 20, color: 'var(--text-primary)' }}>
        {title}
      </h1>

      {/* Right: status + actions */}
      <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
        {/* BFF Status */}
        <div style={{
          display: 'flex', alignItems: 'center', gap: 6,
          padding: '5px 12px', borderRadius: 20,
          background: bffOk ? 'rgba(16,185,129,0.1)' : 'rgba(239,68,68,0.1)',
          border: `1px solid ${bffOk ? 'rgba(16,185,129,0.3)' : 'rgba(239,68,68,0.3)'}`,
          fontSize: 11, fontWeight: 600,
          color: bffOk ? 'var(--green)' : 'var(--red)',
        }}>
          <span style={{
            width: 7, height: 7, borderRadius: '50%',
            background: bffOk ? 'var(--green)' : 'var(--red)',
            animation: bffOk ? 'pulse-teal 2s infinite' : 'none',
          }} />
          BFF :8080 {bffOk ? 'OK' : 'ERR'}
        </div>

        {/* Refresh */}
        <Btn variant="icon" onClick={onRefresh} disabled={refreshing} title="Actualizar datos">
          {refreshing ? <Spinner size={14} /> : <span style={{ fontSize: 14 }}>↻</span>}
          <span style={{ fontSize: 12 }}>Actualizar</span>
        </Btn>

        {/* Notifications */}
        <div style={{ position: 'relative' }}>
          <button onClick={toggleNotif}
            style={{
              background: 'var(--bg-elevated)', border: '1px solid var(--border)',
              borderRadius: 'var(--radius-sm)', padding: '7px 10px', cursor: 'pointer',
              color: 'var(--text-secondary)', fontSize: 16, position: 'relative', display: 'flex',
            }}>
            🔔
            {noLeidas > 0 && (
              <span style={{
                position: 'absolute', top: -4, right: -4,
                background: 'var(--red)', color: '#fff',
                fontSize: 9, fontWeight: 700, borderRadius: '50%',
                width: 16, height: 16, display: 'flex', alignItems: 'center', justifyContent: 'center',
              }}>{noLeidas}</span>
            )}
          </button>
          {notifOpen && (
            <div className="animate-fade-in" style={{
              position: 'absolute', right: 0, top: 44,
              background: 'var(--bg-card)', border: '1px solid var(--border)',
              borderRadius: 'var(--radius-md)', width: 300,
              boxShadow: 'var(--shadow-modal)', zIndex: 200, overflow: 'hidden',
            }}>
              <div style={{ padding: '12px 16px', borderBottom: '1px solid var(--border)', fontFamily: 'var(--font-display)', fontWeight: 700, fontSize: 13 }}>
                Notificaciones
              </div>
              <div style={{ padding: '8px 0', maxHeight: 320, overflowY: 'auto' }}>
                {historial.length === 0 ? (
                  <div style={{ padding: '20px 16px', textAlign: 'center', fontSize: 12, color: 'var(--text-muted)' }}>
                    Sin notificaciones todavía
                  </div>
                ) : (
                  historial.map((n, i) => (
                    <div key={n.id} style={{
                      display: 'flex', gap: 10, padding: '10px 16px', cursor: 'pointer',
                      borderBottom: i < historial.length - 1 ? '1px solid var(--border)' : 'none',
                    }}
                    onMouseEnter={e => e.currentTarget.style.background = 'var(--bg-elevated)'}
                    onMouseLeave={e => e.currentTarget.style.background = 'none'}>
                      <span>{TYPE_ICON[n.type] || '📋'}</span>
                      <div>
                        <div style={{ fontSize: 12, color: 'var(--text-primary)' }}>{n.msg}</div>
                        <div style={{ fontSize: 10, color: 'var(--text-muted)', marginTop: 2 }}>{relativeTime(n.fecha)}</div>
                      </div>
                    </div>
                  ))
                )}
              </div>
            </div>
          )}
        </div>

        {/* User avatar menu */}
        <div style={{ position: 'relative' }}>
          <button onClick={() => { setMenuOpen(o => !o); setNotifOpen(false) }}
            style={{
              background: 'none', border: 'none', cursor: 'pointer',
              display: 'flex', alignItems: 'center', gap: 8,
              padding: '4px 8px', borderRadius: 'var(--radius-sm)',
            }}>
            <Avatar initials={user?.avatar || '??'} size={34} />
            <div style={{ textAlign: 'left' }}>
              <div style={{ fontSize: 12, fontWeight: 600, color: 'var(--text-primary)' }}>{user?.nombre} {user?.apellido}</div>
              <div style={{ fontSize: 10, color: 'var(--text-muted)' }}>{user?.rol}</div>
            </div>
            <span style={{ color: 'var(--text-muted)', fontSize: 10 }}>▼</span>
          </button>

          {menuOpen && (
            <div className="animate-fade-in" style={{
              position: 'absolute', right: 0, top: 50,
              background: 'var(--bg-card)', border: '1px solid var(--border)',
              borderRadius: 'var(--radius-md)', width: 200,
              boxShadow: 'var(--shadow-modal)', zIndex: 200, overflow: 'hidden',
            }}>
              <MenuItem icon="👤" label="Mi Perfil" onClick={() => { navigate('/perfil'); setMenuOpen(false) }} />
              <MenuItem icon="⚙️" label="Configuración" onClick={() => { navigate('/configuracion'); setMenuOpen(false) }} />
              <div style={{ height: 1, background: 'var(--border)', margin: '4px 0' }} />
              <MenuItem icon="🚪" label="Cerrar Sesión" onClick={handleLogout} danger />
            </div>
          )}
        </div>
      </div>
    </header>
  )
}

function MenuItem({ icon, label, onClick, danger }) {
  return (
    <button onClick={onClick} style={{
      width: '100%', display: 'flex', alignItems: 'center', gap: 10,
      padding: '10px 16px', background: 'none', border: 'none',
      color: danger ? 'var(--red)' : 'var(--text-primary)',
      cursor: 'pointer', fontSize: 13, textAlign: 'left',
      fontFamily: 'var(--font-body)',
    }}
    onMouseEnter={e => e.currentTarget.style.background = 'var(--bg-elevated)'}
    onMouseLeave={e => e.currentTarget.style.background = 'none'}>
      <span>{icon}</span> {label}
    </button>
  )
}
