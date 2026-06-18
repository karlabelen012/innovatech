import { useState, useEffect } from 'react'
import { useOutletContext } from 'react-router-dom'
import { getKpis, getKpisByCategory } from '../services/api.js'
import { Card, Spinner, EmptyState, Badge } from '../atoms/index.jsx'
import { RadarChart, Radar, PolarGrid, PolarAngleAxis, ResponsiveContainer, BarChart, Bar, XAxis, YAxis, Tooltip, Cell } from 'recharts'
import { useNotif } from '../context/NotifContext.jsx'

const MOCK_KPIS = [
  { id:1, nombre:'Proyectos Completados', valor:85, unidad:'%', categoria:'PROYECTOS', descripcion:'Tasa de finalización en tiempo' },
  { id:2, nombre:'Satisfacción Cliente', valor:92, unidad:'%', categoria:'PROYECTOS', descripcion:'NPS promedio' },
  { id:3, nombre:'Utilización Recursos', valor:78, unidad:'%', categoria:'RECURSOS', descripcion:'Porcentaje de carga laboral' },
  { id:4, nombre:'Rotación Empleados', valor:12, unidad:'%', categoria:'RECURSOS', descripcion:'Tasa anual' },
  { id:5, nombre:'Velocidad Entrega', valor:91, unidad:'pts', categoria:'PRODUCTIVIDAD', descripcion:'Story points por sprint' },
  { id:6, nombre:'Defectos Producción', valor:3, unidad:'bugs', categoria:'PRODUCTIVIDAD', descripcion:'Promedio mensual' },
  { id:7, nombre:'ROI Proyectos', valor:124, unidad:'%', categoria:'FINANCIERO', descripcion:'Retorno sobre inversión' },
  { id:8, nombre:'Costo por Proyecto', valor:68, unidad:'k$', categoria:'FINANCIERO', descripcion:'Promedio mensual' },
]

const CATEGORIES = ['PROYECTOS','RECURSOS','PRODUCTIVIDAD','FINANCIERO']
const COLORS = { PROYECTOS:'#00d4b3', RECURSOS:'#4d7cfe', PRODUCTIVIDAD:'#10b981', FINANCIERO:'#f59e0b' }

export default function KpisPage() {
  const { refreshTick } = useOutletContext()
  const { push } = useNotif()
  const [kpis, setKpis] = useState([])
  const [loading, setLoading] = useState(true)
  const [activeCategory, setActiveCategory] = useState('ALL')

  useEffect(() => {
    setLoading(true)
    const fetch = activeCategory === 'ALL' ? getKpis() : getKpisByCategory(activeCategory)
    fetch.then(d => setKpis(d)).catch(() => { setKpis(MOCK_KPIS); push('Usando datos de ejemplo', 'warning') })
      .finally(() => setLoading(false))
  }, [refreshTick, activeCategory])

  const displayed = activeCategory === 'ALL' ? kpis : kpis.filter(k => k.categoria === activeCategory)
  const radarData = CATEGORIES.map(c => {
    const cat = kpis.filter(k => k.categoria === c)
    return { subject: c, valor: cat.length ? Math.round(cat.reduce((a,k)=>a+Math.min(k.valor,100),0)/cat.length) : 0 }
  })

  return (
    <div className="animate-fade-in">
      <div style={{ marginBottom:20 }}>
        <h2 style={{ fontFamily:'var(--font-display)', fontWeight:800, fontSize:22 }}>Indicadores KPI</h2>
        <p style={{ color:'var(--text-muted)', fontSize:12, marginTop:2 }}>Métricas de desempeño por categoría</p>
      </div>

      {/* Category filter */}
      <div style={{ display:'flex', gap:8, marginBottom:24, flexWrap:'wrap' }}>
        {['ALL',...CATEGORIES].map(c => (
          <button key={c} onClick={()=>setActiveCategory(c)} style={{
            padding:'7px 16px', borderRadius:20, border:'1px solid',
            borderColor: activeCategory===c ? (COLORS[c]||'var(--teal)') : 'var(--border)',
            background: activeCategory===c ? (COLORS[c]||'var(--teal)')+'22' : 'transparent',
            color: activeCategory===c ? (COLORS[c]||'var(--teal)') : 'var(--text-secondary)',
            cursor:'pointer', fontSize:12, fontWeight:activeCategory===c?600:400,
          }}>{c==='ALL'?'Todos':c}</button>
        ))}
      </div>

      <div style={{ display:'grid', gridTemplateColumns:'1fr 320px', gap:20, marginBottom:20 }}>
        {/* Bar chart */}
        <Card>
          <h3 style={{ fontFamily:'var(--font-display)', fontWeight:700, fontSize:14, marginBottom:16 }}>Valores por KPI</h3>
          {loading ? <div style={{textAlign:'center',padding:40}}><Spinner/></div> : (
            <ResponsiveContainer width="100%" height={220}>
              <BarChart data={displayed.slice(0,10)} layout="vertical">
                <XAxis type="number" tick={{ fill:'#8892b0', fontSize:11 }} axisLine={false} tickLine={false} />
                <YAxis type="category" dataKey="nombre" tick={{ fill:'#8892b0', fontSize:10 }} axisLine={false} tickLine={false} width={140} />
                <Tooltip contentStyle={{ background:'var(--bg-elevated)', border:'1px solid var(--border)', borderRadius:8, fontSize:12 }} />
                <Bar dataKey="valor" radius={[0,4,4,0]}>
                  {displayed.map((k,i) => <Cell key={i} fill={COLORS[k.categoria]||'#00d4b3'} />)}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          )}
        </Card>

        {/* Radar */}
        <Card>
          <h3 style={{ fontFamily:'var(--font-display)', fontWeight:700, fontSize:14, marginBottom:16 }}>Radar General</h3>
          <ResponsiveContainer width="100%" height={220}>
            <RadarChart data={radarData}>
              <PolarGrid stroke="rgba(255,255,255,0.07)" />
              <PolarAngleAxis dataKey="subject" tick={{ fill:'#8892b0', fontSize:10 }} />
              <Radar dataKey="valor" stroke="#00d4b3" fill="#00d4b3" fillOpacity={0.2} />
            </RadarChart>
          </ResponsiveContainer>
        </Card>
      </div>

      {/* KPI Cards */}
      {loading ? <div style={{textAlign:'center',padding:40}}><Spinner size={28}/></div> : (
        displayed.length === 0
          ? <EmptyState icon="📈" title="Sin KPIs" description="No hay indicadores para esta categoría." />
          : (
            <div style={{ display:'grid', gridTemplateColumns:'repeat(auto-fill,minmax(260px,1fr))', gap:16 }}>
              {displayed.map(k => (
                <Card key={k.id}>
                  <div style={{ display:'flex', justifyContent:'space-between', alignItems:'flex-start', marginBottom:12 }}>
                    <Badge label={k.categoria} />
                    <span style={{ fontFamily:'var(--font-display)', fontWeight:800, fontSize:28, color:COLORS[k.categoria]||'var(--teal)' }}>
                      {k.valor}<span style={{ fontSize:12, color:'var(--text-muted)', fontWeight:400 }}>{k.unidad}</span>
                    </span>
                  </div>
                  <div style={{ fontWeight:600, fontSize:13, color:'var(--text-primary)', marginBottom:4 }}>{k.nombre}</div>
                  <div style={{ fontSize:11, color:'var(--text-muted)' }}>{k.descripcion}</div>
                </Card>
              ))}
            </div>
          )
      )}
    </div>
  )
}
