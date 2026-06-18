// ─── Profile Page ──────────────────────────────────────────
import { useState } from 'react'
import { useAuth } from '../context/AuthContext.jsx'
import { useNotif } from '../context/NotifContext.jsx'
import { Card, Btn, Input, Avatar } from '../atoms/index.jsx'

export function PerfilPage() {
  const { user, updateProfile } = useAuth()
  const { push } = useNotif()
  const [form, setForm] = useState({ nombre:user?.nombre||'', apellido:user?.apellido||'', email:user?.email||'', rol:user?.rol||'' })
  const [editing, setEditing] = useState(false)

  const handle = e => setForm(f => ({ ...f, [e.target.name]: e.target.value }))

  const save = () => {
    updateProfile(form)
    push('Perfil actualizado correctamente', 'success')
    setEditing(false)
  }

  return (
    <div className="animate-fade-in" style={{ maxWidth:600 }}>
      <h2 style={{ fontFamily:'var(--font-display)', fontWeight:800, fontSize:22, marginBottom:24 }}>Mi Perfil</h2>
      <Card>
        <div style={{ display:'flex', alignItems:'center', gap:20, marginBottom:28, paddingBottom:24, borderBottom:'1px solid var(--border)' }}>
          <Avatar initials={user?.avatar||'??'} size={64} />
          <div>
            <div style={{ fontFamily:'var(--font-display)', fontWeight:700, fontSize:20 }}>{user?.nombre} {user?.apellido}</div>
            <div style={{ color:'var(--teal)', fontSize:13, marginTop:2 }}>{user?.rol}</div>
            <div style={{ color:'var(--text-muted)', fontSize:12 }}>{user?.email}</div>
          </div>
        </div>

        <div style={{ display:'flex', flexDirection:'column', gap:14 }}>
          <div style={{ display:'grid', gridTemplateColumns:'1fr 1fr', gap:12 }}>
            <Input label="Nombre" name="nombre" value={form.nombre} onChange={handle} />
            <Input label="Apellido" name="apellido" value={form.apellido} onChange={handle} />
          </div>
          <Input label="Email" name="email" type="email" value={form.email} onChange={handle} />
          <Input label="Rol" name="rol" value={form.rol} onChange={handle} />
        </div>

        <div style={{ display:'flex', gap:10, marginTop:20 }}>
          <Btn onClick={save}>Guardar Cambios</Btn>
        </div>
      </Card>
    </div>
  )
}

// ─── Clientes & Equipos ─────────────────────────────────────
import { useEffect, useState as useStateOP } from 'react'
import { useOutletContext } from 'react-router-dom'
import { useNotif as useNotifOP } from '../context/NotifContext.jsx'
import { EmptyState, Badge, Spinner, ProgressBar } from '../atoms/index.jsx'
import { getProyectos, getEmpleados, getAsignaciones, getDashboard, getResumenRecursos } from '../services/api.js'

export function ClientesPage() {
  const { refreshTick } = useOutletContext?.() ?? {}
  const [proyectos, setProyectos] = useStateOP([])
  const [loading, setLoading] = useStateOP(true)
  const [error, setError] = useStateOP(false)

  useEffect(() => {
    let active = true
    setLoading(true); setError(false)
    getProyectos()
      .then(data => { if (active) setProyectos(data || []) })
      .catch(() => { if (active) setError(true) })
      .finally(() => { if (active) setLoading(false) })
    return () => { active = false }
  }, [refreshTick])

  // Agrupar proyectos por "responsable" (cliente)
  const clientes = {}
  proyectos.forEach(p => {
    const nombre = p.responsable?.trim() || 'Sin asignar'
    if (!clientes[nombre]) clientes[nombre] = []
    clientes[nombre].push(p)
  })
  const clienteList = Object.entries(clientes)

  return (
    <div className="animate-fade-in">
      <h2 style={{ fontFamily:'var(--font-display)', fontWeight:800, fontSize:22, marginBottom:4 }}>Clientes</h2>
      <p style={{ color:'var(--text-muted)', fontSize:12, marginBottom:20 }}>Clientes derivados de los proyectos registrados</p>

      {loading ? (
        <div style={{ textAlign:'center', padding:40 }}><Spinner size={28}/></div>
      ) : error ? (
        <EmptyState icon="⚠️" title="No se pudo cargar la información" description="Verifica la conexión con el servidor." />
      ) : clienteList.length === 0 ? (
        <EmptyState icon="🏢" title="Sin clientes" description="Aún no hay proyectos registrados con un responsable asignado." />
      ) : (
        <div style={{ display:'grid', gridTemplateColumns:'repeat(auto-fill, minmax(280px, 1fr))', gap:16 }}>
          {clienteList.map(([nombre, projs]) => {
            const initials = nombre.split(' ').map(w=>w[0]).slice(0,2).join('').toUpperCase()
            return (
              <Card key={nombre}>
                <div style={{ display:'flex', alignItems:'center', gap:14, marginBottom:14 }}>
                  <Avatar initials={initials || '🏢'} size={44} />
                  <div>
                    <div style={{ fontFamily:'var(--font-display)', fontWeight:700, fontSize:15 }}>{nombre}</div>
                    <div style={{ color:'var(--text-muted)', fontSize:12 }}>{projs.length} proyecto{projs.length!==1?'s':''}</div>
                  </div>
                </div>
                <div style={{ display:'flex', flexDirection:'column', gap:8 }}>
                  {projs.map(p => (
                    <div key={p.id} style={{ display:'flex', justifyContent:'space-between', alignItems:'center', gap:8 }}>
                      <span style={{ fontSize:12, color:'var(--text-secondary)' }}>{p.nombre}</span>
                      <Badge label={p.estado} />
                    </div>
                  ))}
                </div>
              </Card>
            )
          })}
        </div>
      )}
    </div>
  )
}

export function EquiposPage() {
  const { refreshTick } = useOutletContext?.() ?? {}
  const [proyectos, setProyectos] = useStateOP([])
  const [empleados, setEmpleados] = useStateOP([])
  const [asignaciones, setAsignaciones] = useStateOP([])
  const [loading, setLoading] = useStateOP(true)
  const [error, setError] = useStateOP(false)

  useEffect(() => {
    let active = true
    setLoading(true); setError(false)
    Promise.allSettled([getProyectos(), getEmpleados(), getAsignaciones()])
      .then(([projs, emps, asigs]) => {
        if (!active) return
        if (projs.status === 'rejected' && emps.status === 'rejected' && asigs.status === 'rejected') {
          setError(true); return
        }
        setProyectos(projs.value || [])
        setEmpleados(emps.value || [])
        setAsignaciones(asigs.value || [])
      })
      .finally(() => { if (active) setLoading(false) })
    return () => { active = false }
  }, [refreshTick])

  const empleadosPorId = Object.fromEntries(empleados.map(e => [e.id, e]))
  const equipos = proyectos
    .filter(p => p.estado !== 'FINALIZADO')
    .map(p => ({
      proyecto: p,
      miembros: asignaciones
        .filter(a => a.proyectoId === p.id && a.activo !== false)
        .map(a => empleadosPorId[a.empleadoId])
        .filter(Boolean),
    }))

  return (
    <div className="animate-fade-in">
      <h2 style={{ fontFamily:'var(--font-display)', fontWeight:800, fontSize:22, marginBottom:4 }}>Equipos</h2>
      <p style={{ color:'var(--text-muted)', fontSize:12, marginBottom:20 }}>Equipos de trabajo por proyecto activo</p>

      {loading ? (
        <div style={{ textAlign:'center', padding:40 }}><Spinner size={28}/></div>
      ) : error ? (
        <EmptyState icon="⚠️" title="No se pudo cargar la información" description="Verifica la conexión con el servidor." />
      ) : equipos.length === 0 ? (
        <EmptyState icon="👥" title="Sin equipos" description="No hay proyectos activos con equipos asignados." />
      ) : (
        <div style={{ display:'grid', gridTemplateColumns:'repeat(auto-fill, minmax(300px, 1fr))', gap:16 }}>
          {equipos.map(({ proyecto, miembros }) => (
            <Card key={proyecto.id}>
              <div style={{ display:'flex', justifyContent:'space-between', alignItems:'flex-start', marginBottom:10 }}>
                <div>
                  <div style={{ fontFamily:'var(--font-display)', fontWeight:700, fontSize:15 }}>{proyecto.nombre}</div>
                  <div style={{ color:'var(--text-muted)', fontSize:12, marginTop:2 }}>{proyecto.responsable || 'Sin cliente asignado'}</div>
                </div>
                <Badge label={proyecto.estado} />
              </div>
              <div style={{ marginBottom:14 }}>
                <ProgressBar value={proyecto.avance || 0} />
                <div style={{ fontSize:11, color:'var(--text-muted)', marginTop:4 }}>{proyecto.avance || 0}% de avance</div>
              </div>
              {miembros.length === 0 ? (
                <p style={{ fontSize:12, color:'var(--text-muted)' }}>Sin miembros asignados.</p>
              ) : (
                <div style={{ display:'flex', flexWrap:'wrap', gap:10 }}>
                  {miembros.map(m => (
                    <div key={m.id} style={{ display:'flex', alignItems:'center', gap:8 }} title={`${m.nombre} ${m.apellido} — ${m.rol}`}>
                      <Avatar initials={`${m.nombre?.[0]||''}${m.apellido?.[0]||''}`.toUpperCase()} size={32} />
                      <div>
                        <div style={{ fontSize:12, fontWeight:600 }}>{m.nombre} {m.apellido}</div>
                        <div style={{ fontSize:10, color:'var(--text-muted)' }}>{m.rol}</div>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </Card>
          ))}
        </div>
      )}
    </div>
  )
}

export function ReportesPage() {
  const { refreshTick } = useOutletContext?.() ?? {}
  const { push } = useNotifOP()
  const [dashboard, setDashboard] = useStateOP(null)
  const [resumen, setResumen] = useStateOP(null)
  const [loading, setLoading] = useStateOP(true)
  const [error, setError] = useStateOP(false)
  const [saved, setSaved] = useStateOP(() => {
    try {
      const raw = sessionStorage.getItem('innovatech_reportes')
      return raw ? JSON.parse(raw) : []
    } catch { return [] }
  })

  useEffect(() => {
    let active = true
    setLoading(true); setError(false)
    Promise.allSettled([getDashboard(), getResumenRecursos()])
      .then(([dash, res]) => {
        if (!active) return
        if (dash.status === 'rejected' && res.status === 'rejected') { setError(true); return }
        setDashboard(dash.value || null)
        setResumen(res.value || null)
      })
      .finally(() => { if (active) setLoading(false) })
    return () => { active = false }
  }, [refreshTick])

  const buildSnapshot = () => ({
    id: Date.now(),
    fecha: new Date().toLocaleString('es-CL'),
    totalProyectos: dashboard?.totalProyectos ?? 0,
    proyectosActivos: dashboard?.proyectosActivos ?? 0,
    proyectosFinalizados: dashboard?.proyectosFinalizados ?? 0,
    promedioAvance: dashboard?.promedioAvance ?? 0,
    totalEmpleados: dashboard?.totalEmpleados ?? resumen?.totalEmpleados ?? 0,
    empleadosDisponibles: dashboard?.empleadosDisponibles ?? resumen?.disponibles ?? 0,
  })

  const handleGuardar = () => {
    const snapshot = buildSnapshot()
    const updated = [snapshot, ...saved].slice(0, 10)
    setSaved(updated)
    try { sessionStorage.setItem('innovatech_reportes', JSON.stringify(updated)) } catch {}
    push('Reporte guardado', 'success')
  }

  const handleEliminar = (id) => {
    const updated = saved.filter(s => s.id !== id)
    setSaved(updated)
    try { sessionStorage.setItem('innovatech_reportes', JSON.stringify(updated)) } catch {}
  }

  const current = buildSnapshot()

  return (
    <div className="animate-fade-in">
      <div style={{ display:'flex', justifyContent:'space-between', alignItems:'center', marginBottom:20 }}>
        <div>
          <h2 style={{ fontFamily:'var(--font-display)', fontWeight:800, fontSize:22 }}>Reportes</h2>
          <p style={{ color:'var(--text-muted)', fontSize:12, marginTop:2 }}>Resumen general del estado de la organización</p>
        </div>
        <Btn onClick={handleGuardar} disabled={loading}>💾 Guardar Reporte</Btn>
      </div>

      {loading ? (
        <div style={{ textAlign:'center', padding:40 }}><Spinner size={28}/></div>
      ) : error ? (
        <EmptyState icon="⚠️" title="No se pudo cargar la información" description="Verifica la conexión con el servidor." />
      ) : (
        <>
          <Card style={{ marginBottom:24 }}>
            <h3 style={{ fontFamily:'var(--font-display)', fontWeight:700, fontSize:15, marginBottom:16 }}>Reporte actual</h3>
            <div style={{ display:'grid', gridTemplateColumns:'repeat(auto-fit, minmax(140px, 1fr))', gap:16 }}>
              <ReporteStat label="Proyectos totales" value={current.totalProyectos} />
              <ReporteStat label="Proyectos activos" value={current.proyectosActivos} />
              <ReporteStat label="Proyectos finalizados" value={current.proyectosFinalizados} />
              <ReporteStat label="Avance promedio" value={`${current.promedioAvance}%`} />
              <ReporteStat label="Empleados totales" value={current.totalEmpleados} />
              <ReporteStat label="Empleados disponibles" value={current.empleadosDisponibles} />
            </div>
          </Card>

          <h3 style={{ fontFamily:'var(--font-display)', fontWeight:700, fontSize:15, marginBottom:12 }}>Reportes guardados</h3>
          {saved.length === 0 ? (
            <EmptyState icon="📊" title="Sin reportes guardados" description="Usa 'Guardar Reporte' para almacenar una instantánea del estado actual." />
          ) : (
            <Card style={{ padding:0, overflow:'hidden' }}>
              <table style={{ width:'100%', borderCollapse:'collapse' }}>
                <thead style={{ background:'var(--bg-deep)' }}>
                  <tr>{['Fecha','Proyectos','Activos','Finalizados','Avance','Empleados','Disponibles',''].map(h=>(
                    <th key={h} style={{ textAlign:'left', padding:'12px 16px', fontSize:11, color:'var(--text-muted)', fontWeight:600 }}>{h}</th>
                  ))}</tr>
                </thead>
                <tbody>
                  {saved.map(r => (
                    <tr key={r.id} style={{ borderTop:'1px solid var(--border)' }}>
                      <td style={{ padding:'12px 16px', fontSize:12, color:'var(--text-secondary)' }}>{r.fecha}</td>
                      <td style={{ padding:'12px 16px', fontSize:13 }}>{r.totalProyectos}</td>
                      <td style={{ padding:'12px 16px', fontSize:13 }}>{r.proyectosActivos}</td>
                      <td style={{ padding:'12px 16px', fontSize:13 }}>{r.proyectosFinalizados}</td>
                      <td style={{ padding:'12px 16px', fontSize:13 }}>{r.promedioAvance}%</td>
                      <td style={{ padding:'12px 16px', fontSize:13 }}>{r.totalEmpleados}</td>
                      <td style={{ padding:'12px 16px', fontSize:13 }}>{r.empleadosDisponibles}</td>
                      <td style={{ padding:'12px 16px' }}>
                        <Btn variant="danger" style={{padding:'6px 8px'}} onClick={()=>handleEliminar(r.id)} title="Eliminar">🗑</Btn>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </Card>
          )}
        </>
      )}
    </div>
  )
}

function ReporteStat({ label, value }) {
  return (
    <div>
      <div style={{ fontFamily:'var(--font-display)', fontWeight:800, fontSize:24, color:'var(--teal)' }}>{value}</div>
      <div style={{ fontSize:12, color:'var(--text-muted)', marginTop:2 }}>{label}</div>
    </div>
  )
}
