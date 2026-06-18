
# Innovatech Solutions — Plataforma de Gestión de Proyectos Tecnológicos

> **DSY1106 Desarrollo Fullstack III** · Sección 303D  
> Integrantes: Bryan Muñoz — Karla Herrera  
> Profesor: Víctor Andrade  
> Evaluación Parcial N°3 — Integración de Arquitectura de Microservicios

---

## Descripción general

Plataforma web fullstack desarrollada para **Innovatech Solutions**, empresa de software a medida con más de 120 empleados. Resuelve la falta de visibilidad en proyectos, la dificultad de gestionar recursos humanos y la ausencia de indicadores de rendimiento (KPIs) en tiempo real.

La solución se construyó sobre una **arquitectura de microservicios** con un BFF (Backend for Frontend), dos microservicios independientes y un frontend en React, todos comunicados mediante una API REST.

---

## Arquitectura general

<img width="2000" height="2000" alt="diagrama_innovatech" src="https://github.com/user-attachments/assets/9527cd1c-e4f1-401e-a4af-ec1e3c7ee3ab" />

```
USUARIO
   │
   ▼
FRONTEND — React + Vite (puerto 5173)
   │
   ▼
BFF — bff-innovatech (puerto 8080)
   ├──► MS Proyectos  (puerto 8081)  ──► MySQL: ms_proyectos_db
   ├──► MS Recursos   (puerto 8082)  ──► MySQL: ms_recursos_db
   └──► MS Analítica  (puerto 8083)  ──► MySQL: ms_analitica_db
```

El BFF actúa como punto único de entrada: agrega, adapta y enruta las peticiones del frontend hacia los microservicios correspondientes.

---

## Repositorios

| Componente | Repositorio | Puerto |
|---|---|---|
| Repositorio Principal (este) | [innovatech-main](#) | — |
| Frontend | [frontend-innovatech](#) | 5173 |
| BFF | [bff-innovatech](#) | 8080 |
| Microservicio Proyectos | [ms-proyectos](#) | 8081 |
| Microservicio Recursos | [ms-recursos](#) | 8082 |
| Microservicio Analítica | [ms-analitica](#) | 8083 |

> Reemplaza los `#` con las URLs reales de GitHub de cada repositorio.

---

## Estructura del monorepo

```
innovatech/
├── frontend-innovatech/     # Aplicación web React + Vite
├── bff-innovatech/          # Backend for Frontend (Spring Boot)
├── ms-proyectos/            # Microservicio de proyectos y tareas
├── ms-recursos/             # Microservicio de recursos humanos
├── ms-analitica/            # Microservicio de KPIs y reportes
└── README.md                # Este archivo
```

---

## Stack tecnológico

### Backend
| Tecnología | Uso |
|---|---|
| Java 17 | Lenguaje principal de todos los servicios backend |
| Spring Boot 3.x | Framework base de los microservicios y el BFF |
| Spring Data JPA + Hibernate | ORM para la capa de persistencia |
| Spring Web (RestTemplate) | Comunicación HTTP entre BFF y microservicios |
| SpringDoc OpenAPI 2.x | Documentación Swagger automática |
| Lombok | Reducción de boilerplate en modelos y DTOs |
| JUnit 5 + Mockito | Pruebas unitarias y de controlador |
| JaCoCo | Métricas de cobertura de código (mínimo 60%) |
| Maven 3.9+ | Gestión de dependencias y build |

### Frontend
| Tecnología | Uso |
|---|---|
| React 18 + Vite 5 | Framework y bundler del frontend |
| React Router DOM 6 | Navegación y rutas protegidas |
| Axios | Cliente HTTP hacia el BFF |
| Recharts | Gráficos (AreaChart, BarChart, PieChart, RadarChart) |
| Lucide React | Librería de íconos |
| Vitest + Testing Library | Pruebas unitarias del frontend |

### Base de datos
| Servicio | Motor | Base de datos |
|---|---|---|
| ms-proyectos | MySQL 8 | `ms_proyectos_db` |
| ms-recursos | MySQL 8 | `ms_recursos_db` |
| ms-analitica | MySQL 8 | `ms_analitica_db` |
| Pruebas (todos) | H2 en memoria | `ddl-auto=create-drop` |

---

## Patrones de diseño aplicados

| Patrón | Categoría | Implementación |
|---|---|---|
| **BFF (Backend for Frontend)** | Arquitectura | `DashboardBffServiceImpl` agrega datos de ms-analítica + ms-recursos en un solo endpoint |
| **API Gateway** | Comunicación | El BFF centraliza autenticación, enrutamiento y CORS |
| **Repository** | Persistencia | `JpaRepository` en cada microservicio desacopla la lógica de negocio del acceso a datos |
| **Factory Method** | Creacional | `RestTemplateConfig` centraliza la creación del `RestTemplate` con timeouts configurados |
| **Circuit Breaker** | Resiliencia | Captura `ResourceAccessException` y lanza `MicroservicioNoDisponibleException`; el dashboard continúa respondiendo parcialmente |
| **API REST (Sync)** | Integración | Comunicación HTTP entre todos los servicios; el frontend usa Axios y el BFF usa RestTemplate |

---

## Módulos funcionales

### Gestión de Proyectos (`ms-proyectos`)
- Planificación, ejecución y seguimiento de proyectos
- Definición y asignación de tareas por proyecto
- Control de estados: `PENDIENTE` → `EN_PROGRESO` → `FINALIZADO`
- Filtros por estado y avance

### Gestión de Recursos y Colaboración (`ms-recursos`)
- CRUD de empleados con roles y habilidades
- Control de disponibilidad: `DISPONIBLE`, `OCUPADO`, `NO_DISPONIBLE`
- Asignación de empleados a proyectos con horas asignadas
- Métricas de carga laboral del equipo

### Monitoreo y Analítica (`ms-analitica`)
- Creación y consulta de KPIs por categoría
- Reportes de avance de proyectos con métricas agregadas
- Promedio de avance de proyectos activos
- Dashboard ejecutivo consolidado

### Frontend (`frontend-innovatech`)
- Autenticación con rutas protegidas
- Dashboard con gráficos en tiempo real
- CRUD completo de proyectos, empleados y asignaciones
- Visualización de KPIs con múltiples tipos de gráfico
- Indicador de estado del BFF en la barra superior

---

## Guía de instalación y ejecución

### Prerrequisitos

- Java 17+
- Maven 3.9+
- Node.js 18+ y npm
- MySQL 8 corriendo localmente

### 1. Base de datos

Crear las tres bases de datos en MySQL:

```sql
CREATE DATABASE ms_proyectos_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE ms_recursos_db   CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE ms_analitica_db  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Los esquemas se generan automáticamente al levantar cada servicio (`ddl-auto=update`).

### 2. Microservicio de Proyectos

```bash
cd ms-proyectos
./mvnw clean install -DskipTests
./mvnw spring-boot:run
# Disponible en http://localhost:8081
```

Configurar credenciales en `src/main/resources/application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=tu_password
```

### 3. Microservicio de Recursos

```bash
cd ms-recursos
./mvnw clean install -DskipTests
./mvnw spring-boot:run
# Disponible en http://localhost:8082
```

### 4. Microservicio de Analítica

```bash
cd ms-analitica
./mvnw clean install -DskipTests
./mvnw spring-boot:run
# Disponible en http://localhost:8083
```

### 5. BFF

```bash
cd bff-innovatech
./mvnw clean install -DskipTests
./mvnw spring-boot:run
# Disponible en http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html
```

### 6. Frontend

```bash
cd frontend-innovatech
npm install
npm run dev
# Disponible en http://localhost:5173
```

> **Orden recomendado:** Levanta primero los tres microservicios, luego el BFF, y por último el frontend.

---

## Credenciales de prueba (frontend)

| Email | Contraseña | Rol |
|---|---|---|
| admin@innovatech.cl | 1234 | Admin |
| bryan@innovatech.cl | 1234 | Admin |
| karla@innovatech.cl | 1234 | Gestor |

---

## Documentación de APIs (Swagger UI)

| Servicio | URL Swagger |
|---|---|
| BFF | http://localhost:8080/swagger-ui.html |
| MS Recursos | http://localhost:8082/swagger-ui.html |
| MS Analítica | http://localhost:8083/swagger-ui.html |

---

## Resumen de endpoints por servicio

### BFF — `http://localhost:8080/api/bff`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/proyectos` | Listar proyectos (filtro: `?estado=EN_PROGRESO`) |
| GET/POST/PUT/DELETE | `/proyectos/{id}` | CRUD de proyectos |
| GET | `/empleados` | Listar empleados |
| GET/POST/PUT/DELETE | `/empleados/{id}` | CRUD de empleados |
| PATCH | `/empleados/{id}/disponibilidad` | Cambiar disponibilidad |
| GET/POST/DELETE | `/asignaciones` | Gestión de asignaciones |
| GET | `/recursos/resumen` | Resumen de carga laboral |
| GET | `/dashboard` | Dashboard consolidado |
| GET | `/dashboard/kpis` | Todos los KPIs |
| GET | `/dashboard/kpis/categoria?categoria=RECURSOS` | KPIs por categoría |

### MS Proyectos — `http://localhost:8081/api/v1/proyectos`
CRUD completo + filtro por estado (`/estado/{estado}`)

### MS Recursos — `http://localhost:8082/api/v1`
- `/empleados` — CRUD + filtros por rol, disponibilidad, habilidad
- `/asignaciones` — CRUD + métricas de equipo (`/equipo`, `/resumen`)

### MS Analítica — `http://localhost:8083/api/v1`
- `/kpis` — CRUD + promedios por categoría (`/promedios`)
- `/reportes` — CRUD + promedio de avance + filtro por estado

---

## Pruebas unitarias

### Tecnologías usadas
- **JUnit 5** con `@ExtendWith(MockitoExtension.class)`, `@BeforeEach`, `@DisplayName`
- **Mockito** con `@Mock`, `@InjectMocks`, `when()`, `verify()`
- **AssertJ** para aserciones (`assertThat`, `assertThatThrownBy`)
- **MockMvc** para pruebas de controladores
- **H2 en memoria** para pruebas de repositorios
- **Vitest + Testing Library** para el frontend React

### Resumen de cobertura por componente

| Componente | Archivo de test | Tests |
|---|---|---|
| **BFF** | DashboardBffServiceImplTest | 6 |
| | ProyectoBffServiceImplTest | 8 |
| | RecursosBffServiceImplTest | 11 |
| | DashboardBffControllerTest | 3 |
| | ProyectoBffControllerTest | 8 |
| **ms-recursos** | EmpleadoServiceTest | 14 |
| | AsignacionServiceTest | 12 |
| | EmpleadoControllerTest | 10 |
| | EmpleadoRepositoryTest | 7 |
| **ms-proyectos** | ProyectoServiceTest | 9 |
| | TareaServiceTest | 9 |
| **ms-analitica** | KpiMetricaServiceTest | 12 |
| | ReporteProyectoServiceTest | 13 |
| | KpiMetricaControllerTest | 8 |
| | ReporteProyectoControllerTest | 8 |
| **Frontend** | DashboardPage, KpisPage, LoginPage, etc. | ~30 |
| **Total backend** | | **138 tests** |

### Ejecutar pruebas y generar reporte de cobertura

```bash
# En cada microservicio o BFF:
./mvnw test                        # Solo ejecutar pruebas
./mvnw clean test jacoco:report    # Ejecutar + generar reporte HTML

# El reporte se genera en:
# target/site/jacoco/index.html

# Frontend:
npm test                  # modo watch
npm run test:coverage     # cobertura con Vitest
```

---

## Persistencia de datos (JPA)

Cada microservicio gestiona su propia base de datos, implementando el patrón **Database per Service**:

- Las entidades están anotadas con `@Entity`, `@Table`, `@Column`
- El acceso a datos usa `JpaRepository` (patrón Repository)
- Las relaciones `@OneToMany` / `@ManyToOne` están mapeadas entre entidades del mismo servicio
- Las transacciones se gestionan con `@Transactional`
- Validaciones declarativas con `jakarta.validation` (`@NotBlank`, `@NotNull`, `@Min`, `@Max`)
- En producción: MySQL con `spring.jpa.hibernate.ddl-auto=update`
- En pruebas: H2 en memoria con `ddl-auto=create-drop`

No hay acceso cruzado directo entre bases de datos. Los datos de otros servicios se consultan a través del BFF mediante llamadas HTTP.

---

## Seguridad

| Mecanismo | Detalle |
|---|---|
| **CORS** | Configurado en el BFF para `localhost:3000` y `localhost:5173` |
| **Timeouts** | Connect: 5 000 ms · Read: 10 000 ms (configurados en RestTemplate) |
| **Autenticación** (diseño) | JWT + autenticación por roles (diseñado para producción) |
| **Mínimo privilegio** | Acceso restringido por servicio |
| **Cifrado** | HTTPS/TLS en entornos de producción |

---

## Equipo

| Nombre | Rol |
|---|---|
| Bryan Muñoz | Desarrollo fullstack, arquitectura, pruebas |
| Karla Herrera | Desarrollo fullstack, frontend, documentación |

**Profesor:** Víctor Andrade  
**Institución:** Duoc UC · Sección 303D  
**Asignatura:** DSY1106 — Desarrollo Fullstack III
