import { useState } from 'react'
import { useNotif, NOTIF_PREF_KEY } from '../context/NotifContext.jsx'
import { Card, Btn, Select } from '../atoms/index.jsx'
import { POLL_INTERVAL_KEY, getPollInterval } from '../utils/prefs.js'

const INTERVALOS = [
  { value: '5000', label: '5 segundos' },
  { value: '10000', label: '10 segundos' },
  { value: '15000', label: '15 segundos (recomendado)' },
  { value: '30000', label: '30 segundos' },
  { value: '60000', label: '60 segundos' },
  { value: '0', label: 'Desactivado (manual)' },
]

function getNotifPref() {
  try {
    const v = localStorage.getItem(NOTIF_PREF_KEY)
    return v === null ? true : v === 'true'
  } catch { return true }
}

export default function ConfiguracionPage() {
  const { push } = useNotif()
  const [notifEnabled, setNotifEnabled] = useState(getNotifPref())
  const [interval_, setInterval_] = useState(String(getPollInterval()))

  const handleGuardar = () => {
    try {
      localStorage.setItem(NOTIF_PREF_KEY, String(notifEnabled))
      localStorage.setItem(POLL_INTERVAL_KEY, interval_)
    } catch { /* noop */ }
    push('Configuración guardada correctamente', 'success')
  }

  return (
    <div className="animate-fade-in" style={{ maxWidth: 600 }}>
      <h2 style={{ fontFamily: 'var(--font-display)', fontWeight: 800, fontSize: 22, marginBottom: 4 }}>Configuración</h2>
      <p style={{ color: 'var(--text-muted)', fontSize: 12, marginBottom: 24 }}>Preferencias de la plataforma para este navegador</p>

      <Card style={{ marginBottom: 16 }}>
        <h3 style={{ fontFamily: 'var(--font-display)', fontWeight: 700, fontSize: 15, marginBottom: 14 }}>Notificaciones</h3>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div>
            <div style={{ fontSize: 13, color: 'var(--text-primary)' }}>Notificaciones emergentes (toasts)</div>
            <div style={{ fontSize: 11, color: 'var(--text-muted)', marginTop: 2 }}>
              Muestra una alerta temporal en pantalla ante cada evento (proyecto creado, mensaje nuevo, etc.).
              La campanita siempre guarda el historial, independiente de esta opción.
            </div>
          </div>
          <Toggle checked={notifEnabled} onChange={setNotifEnabled} />
        </div>
      </Card>

      <Card style={{ marginBottom: 16 }}>
        <h3 style={{ fontFamily: 'var(--font-display)', fontWeight: 700, fontSize: 15, marginBottom: 14 }}>Tiempo real</h3>
        <Select
          label="Intervalo de actualización automática (Dashboard y KPIs)"
          value={interval_}
          onChange={e => setInterval_(e.target.value)}
          options={INTERVALOS}
        />
      </Card>

      <Btn onClick={handleGuardar}>Guardar Cambios</Btn>
    </div>
  )
}

function Toggle({ checked, onChange }) {
  return (
    <button
      onClick={() => onChange(!checked)}
      role="switch"
      aria-checked={checked}
      style={{
        width: 44, height: 24, borderRadius: 12, border: 'none', cursor: 'pointer',
        background: checked ? 'var(--teal)' : 'var(--bg-elevated)',
        position: 'relative', flexShrink: 0, transition: 'var(--transition)',
      }}>
      <span style={{
        position: 'absolute', top: 3, left: checked ? 23 : 3,
        width: 18, height: 18, borderRadius: '50%', background: '#fff',
        transition: 'left 0.2s ease',
      }} />
    </button>
  )
}
