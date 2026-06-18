import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider, useAuth } from './context/AuthContext.jsx'
import { NotifProvider } from './context/NotifContext.jsx'
import AppLayout from './organisms/AppLayout.jsx'
import LoginPage from './pages/LoginPage.jsx'
import DashboardPage from './pages/DashboardPage.jsx'
import ProyectosPage from './pages/ProyectosPage.jsx'
import RecursosPage from './pages/RecursosPage.jsx'
import KpisPage from './pages/KpisPage.jsx'
import { PerfilPage, ClientesPage, EquiposPage, ReportesPage } from './pages/OtherPages.jsx'
import ToastContainer from './atoms/Toast.jsx'

function PrivateRoute({ children }) {
  const { isAuthenticated } = useAuth()
  return isAuthenticated ? children : <Navigate to="/login" replace />
}

function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route path="/" element={<PrivateRoute><AppLayout /></PrivateRoute>}>
        <Route index element={<Navigate to="/dashboard" replace />} />
        <Route path="dashboard" element={<DashboardPage />} />
        <Route path="proyectos" element={<ProyectosPage />} />
        <Route path="clientes" element={<ClientesPage />} />
        <Route path="recursos" element={<RecursosPage />} />
        <Route path="equipos" element={<EquiposPage />} />
        <Route path="kpis" element={<KpisPage />} />
        <Route path="reportes" element={<ReportesPage />} />
        <Route path="perfil" element={<PerfilPage />} />
      </Route>
      <Route path="*" element={<Navigate to="/dashboard" replace />} />
    </Routes>
  )
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <NotifProvider>
          <AppRoutes />
          <ToastContainer />
        </NotifProvider>
      </AuthProvider>
    </BrowserRouter>
  )
}
