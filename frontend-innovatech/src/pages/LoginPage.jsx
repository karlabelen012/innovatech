import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext.jsx'
import { Btn, Input } from '../atoms/index.jsx'

export default function LoginPage() {
  const [form, setForm] = useState({ email: '', password: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const { login } = useAuth()
  const navigate = useNavigate()

  const handle = (e) => setForm(f => ({ ...f, [e.target.name]: e.target.value }))

  const submit = async (e) => {
    e.preventDefault()
    setLoading(true)
    setError('')
    setTimeout(() => {
      const res = login(form.email, form.password)
      if (res.ok) navigate('/dashboard')
      else setError(res.error)
      setLoading(false)
    }, 600)
  }

  return (
    <div style={{
      minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center',
      background: 'var(--bg-void)', padding: 16,
      backgroundImage: `radial-gradient(ellipse at 20% 50%, rgba(0,212,179,0.05) 0%, transparent 60%),
                        radial-gradient(ellipse at 80% 20%, rgba(77,124,254,0.06) 0%, transparent 60%)`,
    }}>
      <div className="animate-fade-in" style={{
        width: '100%', maxWidth: 400,
        background: 'var(--bg-card)', borderRadius: 'var(--radius-xl)',
        border: '1px solid var(--border)', padding: 40,
        boxShadow: '0 24px 64px rgba(0,0,0,0.6)',
      }}>
        {/* Logo */}
        <div style={{ textAlign: 'center', marginBottom: 32 }}>
          <div style={{
            width: 56, height: 56, borderRadius: 16, margin: '0 auto 16px',
            background: 'linear-gradient(135deg,var(--teal),var(--blue-accent))',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            fontFamily: 'var(--font-display)', fontWeight: 800, fontSize: 22, color: '#0a0d14',
          }}>IT</div>
          <h1 style={{ fontFamily: 'var(--font-display)', fontWeight: 800, fontSize: 24, color: 'var(--text-primary)' }}>
            Innovatech<span style={{ color: 'var(--teal)' }}>.</span>
          </h1>
          <p style={{ color: 'var(--text-muted)', fontSize: 13, marginTop: 4 }}>Solutions Platform</p>
        </div>

        <form onSubmit={submit} style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
          <Input label="Correo electrónico" name="email" type="email" value={form.email}
            onChange={handle} placeholder="admin@innovatech.cl" required />
          <Input label="Contraseña" name="password" type="password" value={form.password}
            onChange={handle} placeholder="••••••••" required />

          {error && (
            <div style={{
              background: 'rgba(239,68,68,0.1)', border: '1px solid rgba(239,68,68,0.3)',
              borderRadius: 8, padding: '10px 14px', color: 'var(--red)', fontSize: 13,
            }}>{error}</div>
          )}

          <Btn type="submit" disabled={loading} style={{ width: '100%', justifyContent: 'center', padding: '12px' }}>
            {loading ? '...' : 'Iniciar Sesión'}
          </Btn>
        </form>

        <div style={{
          marginTop: 24, padding: '14px', background: 'var(--bg-deep)',
          borderRadius: 10, border: '1px solid var(--border)',
        }}>
          <p style={{ fontSize: 11, color: 'var(--text-muted)', marginBottom: 8, fontWeight: 600 }}>CREDENCIALES DE PRUEBA</p>
          {[
            { email: 'admin@innovatech.cl', rol: 'Admin' },
            { email: 'bryan@innovatech.cl', rol: 'Gestor' },
          ].map(u => (
            <button key={u.email} onClick={() => setForm({ email: u.email, password: '1234' })}
              style={{ display: 'block', width: '100%', textAlign: 'left', background: 'none', border: 'none', cursor: 'pointer', padding: '3px 0' }}>
              <span style={{ color: 'var(--teal)', fontSize: 11 }}>{u.email}</span>
              <span style={{ color: 'var(--text-muted)', fontSize: 11 }}> · {u.rol} · pass: 1234</span>
            </button>
          ))}
        </div>
      </div>
    </div>
  )
}
