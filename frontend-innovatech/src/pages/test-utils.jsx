import { render } from '@testing-library/react'
import { MemoryRouter, Routes, Route, Outlet } from 'react-router-dom'
import { AuthProvider } from '../context/AuthContext.jsx'
import { NotifProvider } from '../context/NotifContext.jsx'
import ToastContainer from '../atoms/Toast.jsx'

/**
 * Renders a page element inside an <Outlet/> so useOutletContext() works,
 * wrapped with the providers the app normally has (Auth, Notif, Router, Toasts).
 */
export function renderPage(element, { context = { refreshTick: 0, bffOk: true }, route = '/x' } = {}) {
  function Layout() {
    return (
      <>
        <Outlet context={context} />
        <ToastContainer />
      </>
    )
  }
  return render(
    <AuthProvider>
      <NotifProvider>
        <MemoryRouter initialEntries={[route]}>
          <Routes>
            <Route path={route} element={<Layout />}>
              <Route index element={element} />
            </Route>
          </Routes>
        </MemoryRouter>
      </NotifProvider>
    </AuthProvider>
  )
}
