
# Innovatech Solutions — Plataforma de Gestión de Proyectos Tecnológicos

> **DSY1106 Desarrollo Fullstack III** · Sección 303D
> Integrantes: Bryan Muñoz — Karla Herrera
> Profesor: Víctor Andrade
> Evaluación Final Transversal — Arquitectura de Microservicios

---

## Descripción general

Plataforma web fullstack desarrollada para **Innovatech Solutions**, empresa de software a medida con más de 120 empleados. Resuelve la falta de visibilidad en proyectos, la dificultad de gestionar recursos humanos, la ausencia de indicadores de rendimiento (KPIs) en tiempo real y la falta de un canal de comunicación interna entre equipos.

La solución se construyó sobre una **arquitectura de microservicios** con un BFF (Backend for Frontend) que actúa además como **gateway de seguridad** (JWT), cuatro microservicios independientes con base de datos propia, y un frontend en React, todos comunicados mediante API REST.

---

## Arquitectura general

```
NAVEGADOR
   │
   ▼
FRONTEND — React + Vite (puerto 5173)
   │  Authorization: Bearer <JWT>
   ▼
BFF — bff-innovatech (puerto 8080)
   │  emite y valida el JWT · único punto de entrada autenticado
   ├──► MS Proyectos    (puerto 8081) ──► MySQL: ms_proyectos_db
   ├──► MS Analítica    (puerto 8083) ──► MySQL: ms_analitica_db
   ├──► MS Recursos     (puerto 8084) ──► MySQL: ms_recursos_db
   └──► MS Mensajería   (puerto 8085) ──► MySQL: ms_mensajeria_db
```

El BFF actúa como punto único de entrada: valida el JWT en cada request, agrega/adapta datos de varios microservicios en un solo endpoint (p. ej. el dashboard), y protege cada llamada saliente con Circuit Breaker (Resilience4j). Los microservicios internos no son alcanzables desde el navegador — solo se comunican dentro de la red de Docker / red interna.

---

## Estructura del monorepo

```
innovatech/
├── frontend-innovatech/     # Aplicación web React + Vite
├── bff-innovatech/          # Backend for Frontend + gateway de seguridad JWT (Spring Boot)
├── ms-proyectos/            # Microservicio de proyectos y tareas
├── ms-recursos/             # Microservicio de recursos humanos y asignaciones
├── ms-analitica/            # Microservicio de KPIs y reportes
├── ms-mensajeria/           # Microservicio de mensajería interna (chat)
├── docker-compose.yml       # Orquesta MySQL + los 5 servicios backend + frontend
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
| Spring Security + JJWT | Autenticación JWT en el BFF (gateway de seguridad) |
| Resilience4j | Circuit Breaker declarativo del BFF hacia cada microservicio |
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
| Axios | Cliente HTTP hacia el BFF (con interceptor de Authorization) |
| Recharts | Gráficos (AreaChart, BarChart, PieChart, RadarChart) |
| Lucide React | Librería de íconos |
| Vitest + Testing Library | Pruebas unitarias del frontend |

### Infraestructura
| Tecnología | Uso |
|---|---|
| Docker + Docker Compose | Orquestación de MySQL + 5 servicios backend + frontend con un solo comando |
| MySQL 8 | Motor de base de datos (una base por microservicio — *Database per Service*) |

### Base de datos
| Servicio | Motor | Base de datos |
|---|---|---|
| ms-proyectos | MySQL 8 | `ms_proyectos_db` |
| ms-recursos | MySQL 8 | `ms_recursos_db` |
| ms-analitica | MySQL 8 | `ms_analitica_db` |
| ms-mensajeria | MySQL 8 | `ms_mensajeria_db` |
| Pruebas (todos) | H2 en memoria | `ddl-auto=create-drop` |

---

## Patrones de diseño aplicados

| Patrón | Categoría | Implementación |
|---|---|---|
| **BFF (Backend for Frontend)** | Arquitectura | `DashboardBffServiceImpl` agrega datos de ms-analítica + ms-recursos + ms-proyectos en un solo endpoint |
| **Gateway de seguridad** | Arquitectura | El BFF centraliza la autenticación JWT; los microservicios internos confían en la red interna |
| **Repository** | Persistencia | `JpaRepository` en cada microservicio desacopla la lógica de negocio del acceso a datos |
| **DTO (Data Transfer Object)** | Integración | Cada servicio expone DTOs propios (`ProyectoDTO`, `EmpleadoRequest`/`Response`, etc.), nunca entidades JPA |
| **Builder** | Creacional | Lombok `@Builder` en entidades y DTOs para construir objetos inmutables de forma legible |
| **Circuit Breaker** | Resiliencia | `@CircuitBreaker` (Resilience4j) en cada `MsXClient` del BFF; si un microservicio cae, el circuito se abre y falla rápido sin bloquear el resto del dashboard |
| **Chain of exception handling** | Manejo de errores | `@RestControllerAdvice` centralizado por servicio, con handlers específicos por tipo de excepción (`RecursoNoEncontrado`, `DataIntegrityViolation`, validación, etc.) |
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
- Control de disponibilidad: `DISPONIBLE`, `OCUPADO`, `VACACIONES`, `LICENCIA`
- Asignación de empleados a proyectos con horas asignadas
- Métricas de carga laboral del equipo

### Monitoreo y Analítica (`ms-analitica`)
- Creación y consulta de KPIs por categoría
- Reportes de avance de proyectos con métricas agregadas
- Promedio de avance de proyectos activos
- Dashboard ejecutivo consolidado

### Mensajería Interna (`ms-mensajeria`)
- Envío de mensajes entre empleados
- Historial de conversación entre dos usuarios
- Bandeja de entrada con resumen de no leídos
- Marcado de mensajes como leídos

### Frontend (`frontend-innovatech`)
- Login real contra el BFF (JWT), con rutas protegidas
- Dashboard con gráficos en tiempo real
- CRUD completo de proyectos, empleados y asignaciones
- Chat interno entre miembros del equipo
- Visualización de KPIs con múltiples tipos de gráfico
- Indicador de estado del BFF y de cada circuit breaker en la barra superior

---

## Guía de instalación y ejecución

### Opción A — Docker (recomendada)

Requiere solo **Docker Desktop**. Levanta MySQL, los 4 microservicios, el BFF y el frontend con un solo comando.

```bash
docker compose build
docker compose up -d
```

| Servicio | URL |
|---|---|
| Frontend | http://localhost:5173 |
| BFF | http://localhost:8080 |
| MS Proyectos | http://localhost:8081 |
| MS Analítica | http://localhost:8083 |
| MS Recursos | http://localhost:8084 |
| MS Mensajería | http://localhost:8085 |
| MySQL | localhost:3307 → 3306 dentro del contenedor |

Las 4 bases de datos se crean automáticamente al levantar cada servicio. Para bajar el stack: `docker compose down` (agregar `-v` para borrar también los datos de MySQL).

> Nota: si ya tienes un MySQL corriendo en el host en el puerto 3306, el `docker-compose.yml` publica el MySQL del contenedor en el **3307** para no chocar — no afecta a los servicios, que se comunican entre sí por la red interna de Docker.

#### Verificación rápida

```bash
docker compose ps                     # los 7 contenedores deben quedar "Up"/"healthy"
curl http://localhost:8080/api/bff/dashboard \
  -H "Authorization: Bearer $(curl -s -X POST http://localhost:8080/api/auth/login \
      -H 'Content-Type: application/json' \
      -d '{"email":"karla@innovatech.cl","password":"1234"}' | grep -o '\"token\":\"[^\"]*\"' | cut -d'\"' -f4)"
```

Si responde con JSON (totales de proyectos, KPIs, etc.), el stack quedó arriba correctamente.

### Datos de ejemplo (seed automático)

Cada microservicio incluye un `CommandLineRunner` (`*DataSeeder`) que, **solo si su base de datos está vacía**, carga datos de ejemplo al arrancar: 3 proyectos con tareas (`ms-proyectos`), 3 empleados con asignaciones (`ms-recursos`), 3 reportes de proyecto y 4 KPIs (`ms-analitica`), y una conversación de ejemplo entre los 3 empleados (`ms-mensajeria`). Si la base ya tiene datos (por ejecuciones previas), el seeder se omite y no duplica nada — se puede ver el mensaje correspondiente (`Seed ... creado` u `... omite el seed`) en el log de cada servicio al iniciar.

Para forzar un reseed completo desde cero: `docker compose down -v` (borra los volúmenes de MySQL) y luego `docker compose up -d` de nuevo.

### Opción B — Manual (sin Docker)

#### Prerrequisitos
- Java 17+, Maven 3.9+, Node.js 18+ y npm, MySQL 8 corriendo localmente

#### 1. Base de datos

```sql
CREATE DATABASE ms_proyectos_db   CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE ms_recursos_db    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE ms_analitica_db   CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE ms_mensajeria_db  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Los esquemas se generan automáticamente al levantar cada servicio (`ddl-auto=update`).

#### 2. Microservicios (cualquier orden entre ellos)

```bash
cd ms-proyectos && ./mvnw spring-boot:run     # http://localhost:8081
cd ms-analitica && ./mvnw spring-boot:run     # http://localhost:8083
cd ms-recursos  && ./mvnw spring-boot:run     # http://localhost:8084
cd ms-mensajeria && ./mvnw spring-boot:run    # http://localhost:8085
```

#### 3. BFF (después de los microservicios)

```bash
cd bff-innovatech
./mvnw spring-boot:run
# http://localhost:8080 · Swagger: http://localhost:8080/swagger-ui.html
```

#### 4. Frontend (al final)

```bash
cd frontend-innovatech
npm install
npm run dev
# http://localhost:5173
```

---

## Credenciales de prueba

El login es real: el frontend llama a `POST /api/auth/login` en el BFF, que valida contra usuarios en memoria (BCrypt) y devuelve un JWT.

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
| MS Proyectos | http://localhost:8081/swagger-ui.html |
| MS Analítica | http://localhost:8083/swagger-ui.html |
| MS Recursos | http://localhost:8084/swagger-ui.html |
| MS Mensajería | http://localhost:8085/swagger-ui.html |

---

## Resumen de endpoints por servicio

### Autenticación — `http://localhost:8080/api/auth` (público)

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/login` | Valida credenciales y retorna un JWT |

### BFF — `http://localhost:8080/api/bff` (requiere `Authorization: Bearer <token>`)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/proyectos` | Listar proyectos (filtro: `?estado=EN_PROGRESO`) |
| GET/POST/PUT/DELETE | `/proyectos/{id}` | CRUD de proyectos |
| GET | `/tareas/proyecto/{proyectoId}` | Tareas de un proyecto |
| POST/PUT/DELETE | `/tareas/{id}` | CRUD de tareas |
| GET | `/empleados` | Listar empleados |
| GET/POST/PUT/DELETE | `/empleados/{id}` | CRUD de empleados |
| PATCH | `/empleados/{id}/disponibilidad` | Cambiar disponibilidad |
| GET/POST/DELETE | `/asignaciones` | Gestión de asignaciones |
| GET | `/recursos/resumen` | Resumen de carga laboral |
| GET | `/dashboard` | Dashboard consolidado |
| GET | `/dashboard/kpis` | Todos los KPIs |
| GET | `/dashboard/kpis/categoria?categoria=RECURSOS` | KPIs por categoría |
| POST | `/mensajes` | Enviar mensaje |
| GET | `/mensajes/conversacion?email1=&email2=` | Historial entre dos usuarios |
| GET | `/mensajes/inbox?email=` | Bandeja de entrada |
| PATCH | `/mensajes/marcar-leidos?destinatario=&remitente=` | Marcar mensajes como leídos |
| GET | `/circuit-breakers` | Estado de cada circuito (CLOSED/OPEN/HALF_OPEN) |

### MS Proyectos — `http://localhost:8081/api/v1`
- `/proyectos` — CRUD + filtro por estado (`/estado/{estado}`)
- `/tareas` — CRUD + filtro por proyecto (`/proyecto/{proyectoId}`)

### MS Recursos — `http://localhost:8084/api/v1`
- `/empleados` — CRUD + filtros por rol, disponibilidad, habilidad
- `/asignaciones` — CRUD + métricas de equipo (`/equipo`, `/resumen`)

### MS Analítica — `http://localhost:8083/api/v1`
- `/kpis` — CRUD + filtro por categoría + promedios (`/promedios`)
- `/reportes` — CRUD + promedio de avance (`/promedio-avance`) + filtros
- `/dashboard` — Resumen ejecutivo

### MS Mensajería — `http://localhost:8085/api/v1/mensajes`
- `POST /` — Enviar mensaje
- `GET /conversacion?email1=&email2=` — Historial entre dos usuarios
- `GET /inbox?email=` — Bandeja de entrada con resumen de no leídos
- `PATCH /marcar-leidos?destinatario=&remitente=` — Marcar como leídos

> Nota: en desarrollo manual (sin Docker) todos los microservicios exponen sus rutas directamente en su puerto; en producción/Docker el navegador solo debería hablar con el BFF (puerto 8080).

---

## Pruebas unitarias

### Tecnologías usadas
- **JUnit 5** con `@ExtendWith(MockitoExtension.class)`, `@BeforeEach`, `@DisplayName`
- **Mockito** con `@Mock`, `@InjectMocks`, `when()`, `verify()`
- **AssertJ** para aserciones (`assertThat`, `assertThatThrownBy`)
- **MockMvc** (`@WebMvcTest`) para pruebas de controladores, incluida una suite dedicada a la cadena de seguridad JWT (login válido/inválido, acceso protegido con/sin token)
- **H2 en memoria** para pruebas de repositorios
- **Vitest + Testing Library** para el frontend React

### Cobertura real por servicio (JaCoCo / Vitest coverage)

| Servicio | Tests | Cobertura de líneas | Umbral mínimo |
|---|---|---|---|
| **bff-innovatech** | 118 | 90.3% | 60% ✅ |
| **ms-analitica** | 48 | 69.1% | 60% ✅ |
| **ms-proyectos** | 38 | 85.6% | 60% ✅ |
| **ms-recursos** | 45 | 89.7% | 60% ✅ |
| **ms-mensajeria** | 15 | 81.2% | 60% ✅ |
| **Total backend** | **264 tests, 0 fallos** | | |
| **Frontend (Vitest)** | 56 tests, 0 fallos | 81.2% statements / 77.6% branch | — |

> Cifras verificadas ejecutando `./mvnw test jacoco:report` en cada servicio y `npx vitest run` en el frontend.

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
- Validaciones declarativas con `jakarta.validation` (`@NotBlank`, `@NotNull`, `@Min`, `@Max`), consistentes con las restricciones `nullable = false` de cada entidad
- En producción: MySQL con `spring.jpa.hibernate.ddl-auto=update`
- En pruebas: H2 en memoria con `ddl-auto=create-drop`

No hay acceso cruzado directo entre bases de datos. Los datos de otros servicios se consultan a través del BFF mediante llamadas HTTP.

---

## Seguridad

| Mecanismo | Detalle |
|---|---|
| **Autenticación JWT** | El BFF emite y valida JWT (HMAC-SHA512, expiración configurable). `/api/bff/**` requiere `Authorization: Bearer <token>`; `/api/auth/**` y Swagger quedan públicos |
| **Gateway de seguridad** | El BFF es el único punto que valida el token; los microservicios internos confían en la red privada de Docker y no son alcanzables desde el navegador |
| **Usuarios y contraseñas** | En memoria dentro del proceso del BFF (`InMemoryUserDetailsManager`), contraseñas hasheadas con **BCrypt** — no se persisten en MySQL |
| **Sesión** | Stateless (`SessionCreationPolicy.STATELESS`); no hay `HttpSession`, cada request se autentica por su propio token |
| **CORS** | Configurado en el BFF para `localhost:3000` y `localhost:5173`, cubriendo tanto `/api/bff/**` como `/api/auth/**` |
| **Timeouts** | Connect: 5 000 ms · Read: 10 000 ms (configurados en RestTemplate) |
| **Circuit Breaker** | Resilience4j en cada cliente del BFF: si un microservicio cae, el circuito se abre y las llamadas fallan rápido sin bloquear al resto |
| **Validación de entrada** | Bean Validation (`@NotBlank`, `@NotNull`, `@Email`, `@Pattern`) en los DTO, más manejo explícito de `DataIntegrityViolationException` y `HttpMessageNotReadableException` para que datos inválidos devuelvan 400 en vez de 500 |
| **Mínimo privilegio** | Acceso restringido por servicio; el frontend nunca llama directo a los microservicios, solo al BFF |
| **Cifrado** | HTTPS/TLS recomendado en entornos de producción (no configurado en este entorno de desarrollo/demo) |

---

## Equipo

| Nombre | Rol |
|---|---|
| Bryan Muñoz | Desarrollo fullstack, arquitectura, pruebas |
| Karla Herrera | Desarrollo fullstack, frontend, documentación |

**Profesor:** Víctor Andrade
**Institución:** Duoc UC · Sección 303D
**Asignatura:** DSY1106 — Desarrollo Fullstack III
