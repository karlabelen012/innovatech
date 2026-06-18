import { useState } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { Avatar } from '../atoms/index.jsx'

const NAV = [
  { section: 'PRINCIPAL', items: [
    { path: '/dashboard', label: 'Dashboard', icon: '⊞' },
  ]},
  { section: 'GESTIÓN', items: [
    { path: '/proyectos', label: 'Proyectos', icon: '📋' },
    { path: '/clientes', label: 'Clientes', icon: '👥' },
    { path: '/recursos', label: 'Recursos', icon: '🧑‍💻' },
    { path: '/equipos', label: 'Equipos', icon: '👥' },
  ]},
  { section: 'ANALÍTICA', items: [
    { path: '/kpis', label: 'KPIs', icon: '📈' },
    { path: '/reportes', label: 'Reportes', icon: '📊' },
  ]},
]

export default function Sidebar({ bffOk }) {
  const { pathname } = useLocation()
  const navigate = useNavigate()
  const { user } = useAuth()

  return (
    <aside style={{
      width: 220, flexShrink: 0, background: 'var(--bg-deep)',
      borderRight: '1px solid var(--border)', display: 'flex',
      flexDirection: 'column', height: '100vh', position: 'sticky', top: 0,
    }}>
      {/* Logo */}
      <div style={{ padding: '20px 20px 16px', borderBottom: '1px solid var(--border)' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
          <div style={{
            width: 34, height: 34, borderRadius: 10,
            background: 'linear-gradient(135deg,var(--teal),var(--blue-accent))',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            fontFamily: 'var(--font-display)', fontWeight: 800, fontSize: 15, color: '#0a0d14',
          }}>IT</div>
          <div>
            <div style={{ fontFamily: 'var(--font-display)', fontWeight: 800, fontSize: 14, color: 'var(--text-primary)' }}>Innovatech<span style={{ color: 'var(--teal)' }}>.</span></div>
            <div style={{ fontSize: 10, color: 'var(--text-muted)', marginTop: -2 }}>Solutions Platform</div>
          </div>
        </div>
      </div>

      {/* Nav */}
      <nav style={{ flex: 1, overflowY: 'auto', padding: '12px 12px' }}>
        {NAV.map(({ section, items }) => (
          <div key={section} style={{ marginBottom: 20 }}>
            <div style={{ fontSize: 10, fontWeight: 600, color: 'var(--text-muted)', letterSpacing: '0.1em', padding: '0 8px', marginBottom: 6 }}>{section}</div>
            {items.map(({ path, label, icon }) => {
              const active = pathname === path || pathname.startsWith(path + '/')
              return (
                <button key={path} onClick={() => navigate(path)}
                  style={{
                    width: '100%', display: 'flex', alignItems: 'center', gap: 10,
                    padding: '9px 12px', borderRadius: 'var(--radius-sm)',
                    background: active ? 'var(--teal-dim)' : 'transparent',
                    color: active ? 'var(--teal)' : 'var(--text-secondary)',
                    border: active ? '1px solid var(--border-bright)' : '1px solid transparent',
                    cursor: 'pointer', fontFamily: 'var(--font-body)',
                    fontSize: 13, fontWeight: active ? 600 : 400,
                    textAlign: 'left', transition: 'var(--transition)',
                    marginBottom: 2,
                  }}
                  onMouseEnter={e => { if (!active) { e.currentTarget.style.background = 'var(--bg-elevated)'; e.currentTarget.style.color = 'var(--text-primary)' }}}
                  onMouseLeave={e => { if (!active) { e.currentTarget.style.background = 'transparent'; e.currentTarget.style.color = 'var(--text-secondary)' }}}
                >
                  <span style={{ fontSize: 15 }}>{icon}</span>
                  {label}
                </button>
              )
            })}
          </div>
        ))}
      </nav>

      {/* User footer */}
      <div style={{ padding: '12px 16px', borderTop: '1px solid var(--border)' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
          <Avatar initials={user?.avatar || '??'} size={32} />
          <div style={{ flex: 1, minWidth: 0 }}>
            <div style={{ fontSize: 12, fontWeight: 600, color: 'var(--text-primary)', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
              {user?.nombre} {user?.apellido}
            </div>
            <div style={{ fontSize: 10, color: 'var(--text-muted)' }}>{user?.rol}</div>
          </div>
        </div>
      </div>
    </aside>
  )
}
