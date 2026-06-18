import axios from 'axios'

const BASE = 'http://localhost:8080/api/bff'

const api = axios.create({ baseURL: BASE, timeout: 10000 })

// Health check
export const checkBffHealth = () =>
  api.get('/dashboard').then(() => true).catch(() => false)

// ── Dashboard ──────────────────────────────────────────────
export const getDashboard = () => api.get('/dashboard').then(r => r.data)
export const getKpis = () => api.get('/dashboard/kpis').then(r => r.data)
export const getKpisByCategory = (cat) => api.get(`/dashboard/kpis/categoria?categoria=${cat}`).then(r => r.data)

// ── Proyectos ──────────────────────────────────────────────
export const getProyectos = (estado) =>
  api.get('/proyectos', { params: estado ? { estado } : {} }).then(r => r.data)
export const getProyecto = (id) => api.get(`/proyectos/${id}`).then(r => r.data)
export const createProyecto = (data) => api.post('/proyectos', data).then(r => r.data)
export const updateProyecto = (id, data) => api.put(`/proyectos/${id}`, data).then(r => r.data)
export const deleteProyecto = (id) => api.delete(`/proyectos/${id}`)

// ── Empleados ─────────────────────────────────────────────
export const getEmpleados = () => api.get('/empleados').then(r => r.data)
export const getEmpleado = (id) => api.get(`/empleados/${id}`).then(r => r.data)
export const createEmpleado = (data) => api.post('/empleados', data).then(r => r.data)
export const updateEmpleado = (id, data) => api.put(`/empleados/${id}`, data).then(r => r.data)
export const deleteEmpleado = (id) => api.delete(`/empleados/${id}`)
export const updateDisponibilidad = (id, disponibilidad) =>
  api.patch(`/empleados/${id}/disponibilidad`, null, { params: { disponibilidad } }).then(r => r.data)

// ── Asignaciones ──────────────────────────────────────────
export const getAsignaciones = () => api.get('/asignaciones').then(r => r.data)
export const createAsignacion = (data) => api.post('/asignaciones', data).then(r => r.data)
export const deleteAsignacion = (id) => api.delete(`/asignaciones/${id}`)

// ── Resumen recursos ──────────────────────────────────────
export const getResumenRecursos = () => api.get('/recursos/resumen').then(r => r.data)
