import { useState, useEffect } from 'react'
import { useOutletContext } from 'react-router-dom'
import { getDashboard, getKpis } from '../services/api.js'
import { Card, Badge, Spinner, ProgressBar, EmptyState } from '../atoms/index.jsx'
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, PieChart, Pie, Cell, AreaChart, Area } from 'recharts'

const PALETTE = ['#00d4b3','#4d7cfe','#f59e0b','#ef4444','#10b981','#7c5cbf']

// Mock data fallback
const MOCK_DASH = {
  totalProyectos: 12, proyectosActivos: 5, proyectosFinalizados: 6, proyectosPendientes: 1,
  promedioAvance: 68, totalEmpleados: 24, empleadosDisponibles: 9,
  empleadosOcupados: 15, asignacionesActivas: 18,
  proyectosRecientes: [
    { id:1, nombre:'Portal Fintech', estado:'EN_PROGRESO', avance:75, responsable:'Bryan Muñoz' },
    { id:2, nombre:'App Retail', estado:'EN_PROGRESO', avance:40, responsable:'Karla Herrera' },
    { id:3, nombre:'Sistema ERP', estado:'PENDIENTE', avance:0, responsable:'Carlos Soto' },
    { id:4, nombre:'API Gateway v2', estado:'FINALIZADO', avance:100, responsable:'Ana Torres' },
  ],
  kpisPorCategoria: { PROYECTOS: 82, RECURSOS: 75, PRODUCTIVIDAD: 91, FINANCIERO: 68 },
}

const MOCK_KPI_AREA = [
  { mes:'Ene', proyectos:8, recursos:18 }, { mes:'Feb', proyectos:9, recursos:20 },
  { mes:'Mar', proyectos:10, recursos:22 }, { mes:'Abr', proyectos:11, recursos:21 },
  { mes:'May', proyectos:12, recursos:24 }, { mes:'Jun', proyectos:12, recursos:24 },
]

export default function DashboardPage() {
  const { refreshTick } = useOutletContext()
  const [data, setData] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(false)

  useEffect(() => {
    setLoading(true)
    getDashboard()
      .then(d => { setData(d); setError(false) })
      .catch(() => { setData(MOCK_DASH); setError(true) })
      .finally(() => setLoading(false))
  }, [refreshTick])

  if (loading) return <LoadingGrid />

  const d = data || MOCK_DASH

  const pieData = [
    { name: 'Activos', value: d.proyectosActivos || 0 },
    { name: 'Finalizados', value: d.proyectosFinalizados || 0 },
    { name: 'Pendientes', value: d.proyectosPendientes || 0 },
  ]

  const barData = Object.entries(d.kpisPorCategoria || MOCK_DASH.kpisPorCategoria).map(([k, v]) => ({
    categoria: k, valor: v
  }))

  return (
    <div className="animate-fade-in">
      {error && (
        <div style={{
          marginBottom: 20, padding: '12px 16px', background: 'rgba(245,158,11,0.1)',
          border: '1px solid rgba(245,158,11,0.3)', borderRadius: 10, fontSize: 13,
          color: '#f59e0b', display: 'flex', alignItems: 'center', gap: 8,
        }}>
          ⚠ No se pudo conectar al BFF. Mostrando datos de ejemplo. Verifica que el backend esté corriendo en localhost:8080
        </div>
      )}

      {/* KPI Cards Row */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4,1fr)', gap: 16, marginBottom: 24 }}>
        {[
          { label: 'Total Proyectos', value: d.totalProyectos, icon: '📋', color: 'var(--teal)', sub: 'registrados' },
          { label: 'Proyectos Activos', value: d.proyectosActivos, icon: '⚡', color: 'var(--blue-accent)', sub: 'en progreso' },
          { label: 'Total Empleados', value: d.totalEmpleados, icon: '👥', color: 'var(--purple)', sub: 'en plataforma' },
          { label: 'Asignaciones', value: d.asignacionesActivas, icon: '🔗', color: 'var(--orange)', sub: 'activas' },
        ].map(k => <KpiCard key={k.label} {...k} />)}
      </div>

      {/* Row 2: charts */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 320px', gap: 16, marginBottom: 24 }}>
        {/* Area chart */}
        <Card style={{ gridColumn: '1/2' }}>
          <SectionTitle>Evolución Mensual</SectionTitle>
          <ResponsiveContainer width="100%" height={200}>
            <AreaChart data={MOCK_KPI_AREA}>
              <defs>
                <linearGradient id="gProj" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#00d4b3" stopOpacity={0.3}/>
                  <stop offset="95%" stopColor="#00d4b3" stopOpacity={0}/>
                </linearGradient>
                <linearGradient id="gRec" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#4d7cfe" stopOpacity={0.3}/>
                  <stop offset="95%" stopColor="#4d7cfe" stopOpacity={0}/>
                </linearGradient>
              </defs>
              <XAxis dataKey="mes" tick={{ fill: '#8892b0', fontSize: 11 }} axisLine={false} tickLine={false} />
              <YAxis tick={{ fill: '#8892b0', fontSize: 11 }} axisLine={false} tickLine={false} />
              <Tooltip contentStyle={{ background:'var(--bg-elevated)', border:'1px solid var(--border)', borderRadius:8, fontSize:12 }} />
              <Area type="monotone" dataKey="proyectos" stroke="#00d4b3" fill="url(#gProj)" strokeWidth={2} name="Proyectos" />
              <Area type="monotone" dataKey="recursos" stroke="#4d7cfe" fill="url(#gRec)" strokeWidth={2} name="Recursos" />
            </AreaChart>
          </ResponsiveContainer>
          <Legend items={[{ color:'#00d4b3',label:'Proyectos'},{color:'#4d7cfe',label:'Recursos'}]} />
        </Card>

        {/* Bar chart KPIs */}
        <Card>
          <SectionTitle>KPIs por Categoría</SectionTitle>
          <ResponsiveContainer width="100%" height={200}>
            <BarChart data={barData} barCategoryGap="30%">
              <XAxis dataKey="categoria" tick={{ fill:'#8892b0', fontSize:10 }} axisLine={false} tickLine={false} />
              <YAxis domain={[0,100]} tick={{ fill:'#8892b0', fontSize:10 }} axisLine={false} tickLine={false} />
              <Tooltip contentStyle={{ background:'var(--bg-elevated)', border:'1px solid var(--border)', borderRadius:8, fontSize:12 }} />
              <Bar dataKey="valor" radius={[4,4,0,0]}>
                {barData.map((_, i) => <Cell key={i} fill={PALETTE[i % PALETTE.length]} />)}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </Card>

        {/* Pie chart */}
        <Card>
          <SectionTitle>Estado Proyectos</SectionTitle>
          <ResponsiveContainer width="100%" height={160}>
            <PieChart>
              <Pie data={pieData} cx="50%" cy="50%" innerRadius={45} outerRadius={70} dataKey="value" paddingAngle={3}>
                {pieData.map((_, i) => <Cell key={i} fill={PALETTE[i]} />)}
              </Pie>
              <Tooltip contentStyle={{ background:'var(--bg-elevated)', border:'1px solid var(--border)', borderRadius:8, fontSize:12 }} />
            </PieChart>
          </ResponsiveContainer>
          <Legend items={pieData.map((p,i) => ({ color: PALETTE[i], label: `${p.name}: ${p.value}` }))} />
        </Card>
      </div>

      {/* Row 3: recent projects + resource summary */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 320px', gap: 16 }}>
        <Card>
          <SectionTitle>Proyectos Recientes</SectionTitle>
          {(!d.proyectosRecientes || d.proyectosRecientes.length === 0)
            ? <EmptyState icon="📋" title="Sin proyectos" />
            : (
              <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                <thead>
                  <tr>{['Proyecto','Responsable','Estado','Avance'].map(h => (
                    <th key={h} style={{ textAlign:'left', padding:'8px 10px', fontSize:11, color:'var(--text-muted)', fontWeight:600, borderBottom:'1px solid var(--border)', letterSpacing:'0.05em' }}>{h}</th>
                  ))}</tr>
                </thead>
                <tbody>
                  {d.proyectosRecientes.map(p => (
                    <tr key={p.id} style={{ borderBottom:'1px solid var(--border)' }}
                      onMouseEnter={e => e.currentTarget.style.background = 'var(--bg-elevated)'}
                      onMouseLeave={e => e.currentTarget.style.background = 'none'}>
                      <td style={{ padding:'10px', fontSize:13, fontWeight:500, color:'var(--text-primary)' }}>{p.nombre}</td>
                      <td style={{ padding:'10px', fontSize:12, color:'var(--text-secondary)' }}>{p.responsable}</td>
                      <td style={{ padding:'10px' }}><Badge label={p.estado} /></td>
                      <td style={{ padding:'10px', minWidth:120 }}>
                        <div style={{ display:'flex', alignItems:'center', gap:8 }}>
                          <ProgressBar value={p.avance} />
                          <span style={{ fontSize:11, color:'var(--text-muted)', width:32, textAlign:'right' }}>{p.avance}%</span>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
        </Card>

        <Card>
          <SectionTitle>Recursos Humanos</SectionTitle>
          <div style={{ display:'flex', flexDirection:'column', gap:16, marginTop:8 }}>
            {[
              { label:'Total empleados', value:d.totalEmpleados, color:'var(--teal)' },
              { label:'Disponibles', value:d.empleadosDisponibles, color:'var(--green)' },
              { label:'Ocupados', value:d.empleadosOcupados, color:'var(--red)' },
              { label:'Asignaciones activas', value:d.asignacionesActivas, color:'var(--blue-accent)' },
            ].map(r => (
              <div key={r.label} style={{ display:'flex', justifyContent:'space-between', alignItems:'center' }}>
                <span style={{ fontSize:12, color:'var(--text-secondary)' }}>{r.label}</span>
                <span style={{ fontFamily:'var(--font-display)', fontWeight:700, fontSize:20, color:r.color }}>{r.value ?? '-'}</span>
              </div>
            ))}
            <div style={{ marginTop:4 }}>
              <div style={{ display:'flex', justifyContent:'space-between', marginBottom:6 }}>
                <span style={{ fontSize:11, color:'var(--text-muted)' }}>Ocupación</span>
                <span style={{ fontSize:11, color:'var(--text-muted)' }}>
                  {d.totalEmpleados ? Math.round((d.empleadosOcupados / d.totalEmpleados) * 100) : 0}%
                </span>
              </div>
              <ProgressBar value={d.empleadosOcupados || 0} max={d.totalEmpleados || 1} />
            </div>
          </div>
        </Card>
      </div>
    </div>
  )
}

function KpiCard({ label, value, icon, color, sub }) {
  return (
    <Card>
      <div style={{ display:'flex', justifyContent:'space-between', alignItems:'flex-start' }}>
        <div>
          <div style={{ fontSize:11, color:'var(--text-muted)', fontWeight:500, marginBottom:8 }}>{label}</div>
          <div style={{ fontFamily:'var(--font-display)', fontWeight:800, fontSize:32, color }}>{value ?? '-'}</div>
          <div style={{ fontSize:11, color:'var(--text-muted)', marginTop:4 }}>{sub}</div>
        </div>
        <div style={{ fontSize:28, opacity:0.7 }}>{icon}</div>
      </div>
    </Card>
  )
}

function SectionTitle({ children }) {
  return <h3 style={{ fontFamily:'var(--font-display)', fontWeight:700, fontSize:14, color:'var(--text-primary)', marginBottom:16 }}>{children}</h3>
}

function Legend({ items }) {
  return (
    <div style={{ display:'flex', gap:16, flexWrap:'wrap', marginTop:12 }}>
      {items.map(i => (
        <div key={i.label} style={{ display:'flex', alignItems:'center', gap:5 }}>
          <span style={{ width:8, height:8, borderRadius:'50%', background:i.color, display:'inline-block' }} />
          <span style={{ fontSize:11, color:'var(--text-secondary)' }}>{i.label}</span>
        </div>
      ))}
    </div>
  )
}

function LoadingGrid() {
  return (
    <div style={{ display:'flex', flexDirection:'column', gap:16 }}>
      <div style={{ display:'grid', gridTemplateColumns:'repeat(4,1fr)', gap:16 }}>
        {[1,2,3,4].map(i => <SkeletonCard key={i} height={100} />)}
      </div>
      <div style={{ display:'grid', gridTemplateColumns:'1fr 1fr 320px', gap:16 }}>
        {[1,2,3].map(i => <SkeletonCard key={i} height={240} />)}
      </div>
    </div>
  )
}

function SkeletonCard({ height }) {
  return (
    <div style={{
      height, borderRadius:'var(--radius-lg)', background:'var(--bg-card)',
      border:'1px solid var(--border)', overflow:'hidden', position:'relative',
    }}>
      <div style={{
        position:'absolute', inset:0,
        background:'linear-gradient(90deg,transparent 0%,rgba(255,255,255,0.04) 50%,transparent 100%)',
        backgroundSize:'200% 100%', animation:'shimmer 1.5s infinite',
      }} />
    </div>
  )
}
