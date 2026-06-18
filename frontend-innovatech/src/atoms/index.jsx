/* ============================================================
   ATOMS — Innovatech Design System
   ============================================================ */
import React from 'react'

// ── Button ────────────────────────────────────────────────
const btnBase = {
  display: 'inline-flex', alignItems: 'center', gap: '6px',
  fontFamily: 'var(--font-body)', fontWeight: 500, cursor: 'pointer',
  border: 'none', borderRadius: 'var(--radius-sm)', transition: 'var(--transition)',
  whiteSpace: 'nowrap',
}
const btnVariants = {
  primary: { background: 'var(--teal)', color: '#0a0d14', padding: '8px 18px', fontSize: '13px' },
  secondary: { background: 'var(--bg-elevated)', color: 'var(--text-primary)', padding: '8px 18px', fontSize: '13px', border: '1px solid var(--border)' },
  danger: { background: 'rgba(239,68,68,0.15)', color: 'var(--red)', padding: '8px 18px', fontSize: '13px', border: '1px solid rgba(239,68,68,0.3)' },
  ghost: { background: 'transparent', color: 'var(--text-secondary)', padding: '6px 12px', fontSize: '13px' },
  icon: { background: 'var(--bg-elevated)', color: 'var(--text-secondary)', padding: '8px', fontSize: '13px', border: '1px solid var(--border)', borderRadius: 'var(--radius-sm)' },
}

export function Btn({ variant = 'primary', children, onClick, disabled, style, type = 'button', title }) {
  const [hov, setHov] = React.useState(false)
  const v = btnVariants[variant] || btnVariants.primary
  return (
    <button type={type} onClick={onClick} disabled={disabled} title={title}
      onMouseEnter={() => setHov(true)} onMouseLeave={() => setHov(false)}
      style={{
        ...btnBase, ...v, opacity: disabled ? 0.45 : 1,
        filter: hov && !disabled ? 'brightness(1.12)' : 'none',
        transform: hov && !disabled ? 'translateY(-1px)' : 'none',
        ...style
      }}>
      {children}
    </button>
  )
}

// ── Badge ─────────────────────────────────────────────────
const badgeColors = {
  PENDIENTE:    { bg: 'rgba(245,158,11,0.15)',  color: '#f59e0b' },
  EN_PROGRESO:  { bg: 'rgba(77,124,254,0.15)',  color: '#4d7cfe' },
  FINALIZADO:   { bg: 'rgba(16,185,129,0.15)',  color: '#10b981' },
  DISPONIBLE:   { bg: 'rgba(16,185,129,0.15)',  color: '#10b981' },
  OCUPADO:      { bg: 'rgba(239,68,68,0.15)',   color: '#ef4444' },
  VACACIONES:   { bg: 'rgba(124,92,191,0.15)',  color: '#9d7fe8' },
  LICENCIA:     { bg: 'rgba(245,158,11,0.15)',  color: '#f59e0b' },
  ACTIVO:       { bg: 'rgba(16,185,129,0.15)',  color: '#10b981' },
  INACTIVO:     { bg: 'rgba(100,100,120,0.2)',  color: '#8892b0' },
  default:      { bg: 'rgba(100,100,120,0.2)',  color: '#8892b0' },
}

export function Badge({ label }) {
  const c = badgeColors[label] || badgeColors.default
  return (
    <span style={{
      display: 'inline-flex', alignItems: 'center', gap: '5px',
      padding: '3px 10px', borderRadius: '20px', fontSize: '11px', fontWeight: 600,
      letterSpacing: '0.03em', background: c.bg, color: c.color,
    }}>
      <span style={{ width: 5, height: 5, borderRadius: '50%', background: c.color, display: 'inline-block' }} />
      {label}
    </span>
  )
}

// ── Spinner ───────────────────────────────────────────────
export function Spinner({ size = 20, color = 'var(--teal)' }) {
  return (
    <span style={{
      display: 'inline-block', width: size, height: size,
      border: `2px solid rgba(255,255,255,0.1)`,
      borderTopColor: color, borderRadius: '50%',
      animation: 'spin 0.7s linear infinite',
    }} />
  )
}

// ── Input ─────────────────────────────────────────────────
export function Input({ label, value, onChange, placeholder, type = 'text', required, name, min, max }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 5 }}>
      {label && <label style={{ fontSize: 12, color: 'var(--text-secondary)', fontWeight: 500 }}>{label}{required && <span style={{ color: 'var(--teal)' }}> *</span>}</label>}
      <input
        name={name} type={type} value={value} onChange={onChange}
        placeholder={placeholder} required={required} min={min} max={max}
        style={{
          background: 'var(--bg-void)', border: '1px solid var(--border)',
          borderRadius: 'var(--radius-sm)', padding: '9px 12px',
          color: 'var(--text-primary)', fontSize: 13, outline: 'none',
          transition: 'var(--transition)', fontFamily: 'var(--font-body)',
          width: '100%',
        }}
        onFocus={e => e.target.style.borderColor = 'var(--teal)'}
        onBlur={e => e.target.style.borderColor = 'var(--border)'}
      />
    </div>
  )
}

// ── Textarea ──────────────────────────────────────────────
export function Textarea({ label, value, onChange, placeholder, name, rows = 3 }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 5 }}>
      {label && <label style={{ fontSize: 12, color: 'var(--text-secondary)', fontWeight: 500 }}>{label}</label>}
      <textarea
        name={name} value={value} onChange={onChange} placeholder={placeholder} rows={rows}
        style={{
          background: 'var(--bg-void)', border: '1px solid var(--border)',
          borderRadius: 'var(--radius-sm)', padding: '9px 12px',
          color: 'var(--text-primary)', fontSize: 13, outline: 'none',
          transition: 'var(--transition)', fontFamily: 'var(--font-body)',
          resize: 'vertical', width: '100%',
        }}
        onFocus={e => e.target.style.borderColor = 'var(--teal)'}
        onBlur={e => e.target.style.borderColor = 'var(--border)'}
      />
    </div>
  )
}

// ── Select ────────────────────────────────────────────────
export function Select({ label, value, onChange, options, name, required }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 5 }}>
      {label && <label style={{ fontSize: 12, color: 'var(--text-secondary)', fontWeight: 500 }}>{label}{required && <span style={{ color: 'var(--teal)' }}> *</span>}</label>}
      <select
        name={name} value={value} onChange={onChange} required={required}
        style={{
          background: 'var(--bg-void)', border: '1px solid var(--border)',
          borderRadius: 'var(--radius-sm)', padding: '9px 12px',
          color: value ? 'var(--text-primary)' : 'var(--text-muted)',
          fontSize: 13, outline: 'none', width: '100%',
          fontFamily: 'var(--font-body)', cursor: 'pointer',
        }}
        onFocus={e => e.target.style.borderColor = 'var(--teal)'}
        onBlur={e => e.target.style.borderColor = 'var(--border)'}
      >
        <option value="">-- Seleccionar --</option>
        {options.map(o => <option key={o.value} value={o.value}>{o.label}</option>)}
      </select>
    </div>
  )
}

// ── Avatar ────────────────────────────────────────────────
export function Avatar({ initials, size = 36, color = 'var(--teal)', bg = 'var(--teal-dim)' }) {
  return (
    <div style={{
      width: size, height: size, borderRadius: '50%',
      background: bg, color, fontFamily: 'var(--font-display)',
      display: 'flex', alignItems: 'center', justifyContent: 'center',
      fontSize: size * 0.36, fontWeight: 700, flexShrink: 0,
      border: `1.5px solid ${color}40`,
    }}>
      {initials}
    </div>
  )
}

// ── Card ──────────────────────────────────────────────────
export function Card({ children, style, onClick }) {
  const [hov, setHov] = React.useState(false)
  return (
    <div onClick={onClick}
      onMouseEnter={() => setHov(true)} onMouseLeave={() => setHov(false)}
      style={{
        background: 'var(--bg-card)', border: '1px solid var(--border)',
        borderRadius: 'var(--radius-lg)', padding: '20px',
        transition: 'var(--transition)',
        boxShadow: hov ? 'var(--shadow-glow)' : 'var(--shadow-card)',
        borderColor: hov ? 'var(--border-bright)' : 'var(--border)',
        cursor: onClick ? 'pointer' : 'default',
        ...style
      }}>
      {children}
    </div>
  )
}

// ── Modal ─────────────────────────────────────────────────
export function Modal({ open, onClose, title, children, width = 520 }) {
  if (!open) return null
  return (
    <div onClick={onClose} style={{
      position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.7)',
      display: 'flex', alignItems: 'center', justifyContent: 'center',
      zIndex: 1000, padding: 16, backdropFilter: 'blur(4px)',
    }}>
      <div onClick={e => e.stopPropagation()} className="animate-fade-in" style={{
        background: 'var(--bg-card)', border: '1px solid var(--border)',
        borderRadius: 'var(--radius-xl)', width: '100%', maxWidth: width,
        maxHeight: '90vh', overflow: 'auto', boxShadow: 'var(--shadow-modal)',
      }}>
        <div style={{
          display: 'flex', alignItems: 'center', justifyContent: 'space-between',
          padding: '20px 24px 16px', borderBottom: '1px solid var(--border)',
        }}>
          <h3 style={{ fontFamily: 'var(--font-display)', fontSize: 17, fontWeight: 700, color: 'var(--text-primary)' }}>{title}</h3>
          <button onClick={onClose} style={{ background: 'none', border: 'none', color: 'var(--text-muted)', cursor: 'pointer', fontSize: 20, lineHeight: 1 }}>×</button>
        </div>
        <div style={{ padding: '20px 24px 24px' }}>{children}</div>
      </div>
    </div>
  )
}

// ── Progress Bar ──────────────────────────────────────────
export function ProgressBar({ value = 0, max = 100, color = 'var(--teal)' }) {
  const pct = Math.min(100, Math.max(0, (value / max) * 100))
  const c = pct >= 80 ? 'var(--green)' : pct >= 40 ? 'var(--blue-accent)' : 'var(--orange)'
  return (
    <div style={{ height: 6, background: 'var(--bg-void)', borderRadius: 3, overflow: 'hidden', width: '100%' }}>
      <div style={{ height: '100%', width: `${pct}%`, background: c, borderRadius: 3, transition: 'width 0.5s ease' }} />
    </div>
  )
}

// ── Empty State ───────────────────────────────────────────
export function EmptyState({ icon, title, description }) {
  return (
    <div style={{ textAlign: 'center', padding: '48px 24px', color: 'var(--text-muted)' }}>
      <div style={{ fontSize: 40, marginBottom: 12 }}>{icon}</div>
      <p style={{ fontFamily: 'var(--font-display)', fontWeight: 600, color: 'var(--text-secondary)', marginBottom: 6 }}>{title}</p>
      {description && <p style={{ fontSize: 12 }}>{description}</p>}
    </div>
  )
}
