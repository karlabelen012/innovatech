# Frontend — Innovatech Solutions Platform

## Tecnologías
- **React 18** + **Vite 5**
- **React Router DOM 6**
- **Axios** (HTTP client → BFF :8080)
- **Recharts** (gráficos: AreaChart, BarChart, PieChart, RadarChart)
- **Lucide React** (íconos)

## Estructura Atomic Design

```
src/
├── atoms/          # Btn, Badge, Spinner, Input, Select, Modal, Avatar, Card, ProgressBar, Toast
├── molecules/      # Sidebar, TopBar
├── organisms/      # AppLayout
├── pages/          # LoginPage, DashboardPage, ProyectosPage, RecursosPage, KpisPage, OtherPages
├── services/       # api.js → todas las llamadas al BFF
├── context/        # AuthContext, NotifContext
└── utils/          # (helpers)
```

## Instalación y ejecución

```bash
npm install
npm run dev        # inicia en http://localhost:5173
```

## Credenciales de prueba

| Email                     | Password | Rol   |
|---------------------------|----------|-------|
| admin@innovatech.cl       | 1234     | Admin |
| bryan@innovatech.cl       | 1234     | Admin |
| karla@innovatech.cl       | 1234     | Gestor|

## Funcionalidades

### Dashboard
- KPI cards (proyectos, empleados, asignaciones)
- AreaChart evolución mensual
- BarChart KPIs por categoría
- PieChart distribución de estados
- Tabla de proyectos recientes con avance
- Resumen recursos humanos
- Indicador de estado del BFF en tiempo real
- Botón Actualizar para re-fetch

### Proyectos (CRUD completo)
- Listar con filtro por estado
- Crear, Editar, Eliminar con confirmación
- Badge de estado coloreado
- Barra de progreso de avance

### Recursos (CRUD completo)
- Tab Empleados: listar, crear, editar, eliminar
- Cambio de disponibilidad inline
- Tab Asignaciones: listar, crear, desactivar

### KPIs
- Filtro por categoría
- BarChart horizontal
- RadarChart general
- Cards individuales por KPI

### Perfil
- Ver y editar nombre, apellido, email, rol
- Persiste en sessionStorage

### Navegación
- Sidebar con secciones
- Barra superior con: estado BFF, botón actualizar, campana notificaciones, menú usuario
- Cerrar sesión
- Rutas protegidas (redirige a /login si no autenticado)

## Conexión con Backend

El frontend apunta al BFF en `http://localhost:8080/api/bff`.

Si el BFF no está disponible, el Dashboard muestra datos de ejemplo con un aviso de warning. Las demás páginas mostrarán errores via toast.

Microservicios requeridos:
- BFF:        `localhost:8080`
- MS Proyectos: `localhost:8081`
- MS Recursos:  `localhost:8082`
- MS Analítica: `localhost:8083`

## Pruebas

```bash
npm test                  # modo watch
npm run test:coverage     # cobertura con vitest
```
