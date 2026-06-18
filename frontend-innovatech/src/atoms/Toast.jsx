import { useNotif } from '../context/NotifContext'

const colors = {
  success: { bg: 'rgba(16,185,129,0.15)', border: 'rgba(16,185,129,0.4)', color: '#10b981', icon: '✓' },
  error:   { bg: 'rgba(239,68,68,0.15)',  border: 'rgba(239,68,68,0.4)',  color: '#ef4444', icon: '✕' },
  info:    { bg: 'rgba(77,124,254,0.15)', border: 'rgba(77,124,254,0.4)', color: '#4d7cfe', icon: 'ℹ' },
  warning: { bg: 'rgba(245,158,11,0.15)', border: 'rgba(245,158,11,0.4)', color: '#f59e0b', icon: '⚠' },
}

export default function ToastContainer() {
  const { toasts, remove } = useNotif()
  return (
    <div style={{
      position: 'fixed', bottom: 24, right: 24, zIndex: 9999,
      display: 'flex', flexDirection: 'column', gap: 8,
    }}>
      {toasts.map(t => {
        const c = colors[t.type] || colors.info
        return (
          <div key={t.id} className="animate-fade-in" onClick={() => remove(t.id)} style={{
            display: 'flex', alignItems: 'center', gap: 10,
            background: c.bg, border: `1px solid ${c.border}`,
            borderRadius: 10, padding: '12px 16px', cursor: 'pointer',
            backdropFilter: 'blur(8px)', maxWidth: 360, minWidth: 260,
            boxShadow: '0 4px 20px rgba(0,0,0,0.4)',
          }}>
            <span style={{ color: c.color, fontWeight: 700, fontSize: 14 }}>{c.icon}</span>
            <span style={{ color: 'var(--text-primary)', fontSize: 13, flex: 1 }}>{t.msg}</span>
            <span style={{ color: 'var(--text-muted)', fontSize: 16 }}>×</span>
          </div>
        )
      })}
    </div>
  )
}
