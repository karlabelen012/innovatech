import { useState, useEffect, useCallback } from 'react'
import { Outlet, useLocation } from 'react-router-dom'
import Sidebar from '../molecules/Sidebar.jsx'
import TopBar from '../molecules/TopBar.jsx'
import { checkBffHealth } from '../services/api.js'

const PAGE_TITLES = {
  '/dashboard': 'Dashboard',
  '/proyectos': 'Proyectos',
  '/clientes': 'Clientes',
  '/recursos': 'Recursos',
  '/equipos': 'Equipos',
  '/kpis': 'KPIs',
  '/reportes': 'Reportes',
  '/perfil': 'Mi Perfil',
}

export default function AppLayout() {
  const { pathname } = useLocation()
  const [bffOk, setBffOk] = useState(null)
  const [refreshing, setRefreshing] = useState(false)
  const [refreshTick, setRefreshTick] = useState(0)

  const checkHealth = useCallback(async () => {
    const ok = await checkBffHealth()
    setBffOk(ok)
  }, [])

  useEffect(() => { checkHealth() }, [])

  const handleRefresh = async () => {
    setRefreshing(true)
    await checkHealth()
    setRefreshTick(t => t + 1)
    setTimeout(() => setRefreshing(false), 600)
  }

  const title = Object.entries(PAGE_TITLES).find(([k]) => pathname.startsWith(k))?.[1] || 'Dashboard'

  return (
    <div style={{ display: 'flex', height: '100vh', overflow: 'hidden' }}>
      <Sidebar bffOk={bffOk} />
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', overflow: 'hidden' }}>
        <TopBar bffOk={bffOk} onRefresh={handleRefresh} refreshing={refreshing} title={title} notifCount={3} />
        <main style={{ flex: 1, overflowY: 'auto', background: 'var(--bg-void)', padding: '24px' }}>
          <Outlet context={{ bffOk, refreshTick }} />
        </main>
      </div>
    </div>
  )
}
