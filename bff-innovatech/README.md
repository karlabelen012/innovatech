# BFF – Innovatech Solutions

## Descripción

**Backend for Frontend (BFF)** de la plataforma Innovatech Solutions.  
Actúa como capa intermediaria y **gateway de seguridad JWT** entre el frontend React y los cuatro microservicios, exponiendo un API REST unificada, simplificada y orientada a las necesidades del cliente web.

```
Frontend React (5173)
        │  Authorization: Bearer <JWT>
        ▼
BFF – bff-innovatech  ← Puerto 8080
  ├── /api/bff/proyectos       → ms-proyectos   (8081)
  ├── /api/bff/tareas          → ms-proyectos   (8081)
  ├── /api/bff/empleados       → ms-recursos    (8084)
  ├── /api/bff/asignaciones    → ms-recursos    (8084)
  ├── /api/bff/recursos/resumen→ ms-recursos    (8084)
  ├── /api/bff/mensajes        → ms-mensajeria  (8085)
  └── /api/bff/dashboard       → ms-analitica (8083) + ms-proyectos (8081) + ms-recursos (8084)
```

## Patrones aplicados

| Patrón | Implementación |
|--------|----------------|
| **BFF (Backend for Frontend)** | `DashboardBffServiceImpl` agrega datos en vivo de ms-proyectos + ms-analitica + ms-recursos en un único endpoint `/api/bff/dashboard` |
| **Gateway de seguridad** | El BFF emite y valida el JWT (`JwtAuthFilter`, `JwtService`); los microservicios internos no son alcanzables desde el navegador |
| **Factory Method** | `RestTemplateConfig` centraliza la creación del `RestTemplate` |
| **Repository / Client** | Clientes HTTP (`MsProyectosClient`, `MsRecursosClient`, `MsAnaliticaClient`, `MsMensajeriaClient`) aíslan la comunicación HTTP |
| **Circuit Breaker declarativo** | `@CircuitBreaker` (Resilience4j) en cada método de cada `MsXClient`, con `fallbackMethod` propio; si un microservicio cae, el circuito se abre y lanza `MicroservicioNoDisponibleException` sin bloquear el resto del dashboard |

## Tecnologías

- Java 17
- Spring Boot 3.5.14
- Spring Web (RestTemplate)
- Spring Security + JJWT (autenticación JWT / gateway de seguridad)
- Resilience4j (Circuit Breaker declarativo)
- Spring Validation
- Springdoc OpenAPI 2.5.0 (Swagger UI)
- Lombok 1.18.40
- JaCoCo 0.8.12 (cobertura ≥ 60%)

## Requisitos previos

- Java 17+
- Maven 3.8+
- Los cuatro microservicios deben estar corriendo (ver puertos abajo) — o levantar todo junto con `docker compose up -d` desde la raíz del monorepo

## Instalación y ejecución

> Para levantar todo el stack (MySQL + 4 microservicios + BFF + frontend) de una vez, ver la guía de Docker en el [README raíz del monorepo](../README.md#guía-de-instalación-y-ejecución).

```bash
# Clonar / copiar el proyecto
cd bff-innovatech

# Compilar
./mvnw clean compile

# Ejecutar (con los 4 microservicios ya corriendo)
./mvnw spring-boot:run

# El BFF queda disponible en:
# http://localhost:8080
# http://localhost:8080/swagger-ui.html
```

## Ejecutar pruebas unitarias y cobertura

```bash
# Ejecutar tests
./mvnw test

# Generar reporte JaCoCo
./mvnw verify

# Ver reporte en:
# target/site/jacoco/index.html
```

## Endpoints principales

### Autenticación (público)
| Método | URL | Descripción |
|--------|-----|-------------|
| POST | `/api/auth/login` | Valida credenciales y retorna un JWT |

> Todos los endpoints `/api/bff/**` requieren el header `Authorization: Bearer <token>`.

### Proyectos y Tareas
| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/api/bff/proyectos` | Listar todos (filtro: `?estado=EN_PROGRESO`) |
| GET | `/api/bff/proyectos/{id}` | Obtener por ID |
| POST | `/api/bff/proyectos` | Crear proyecto |
| PUT | `/api/bff/proyectos/{id}` | Actualizar proyecto |
| DELETE | `/api/bff/proyectos/{id}` | Eliminar proyecto |
| GET | `/api/bff/tareas/proyecto/{proyectoId}` | Tareas de un proyecto |
| POST | `/api/bff/tareas` | Crear tarea |
| PUT | `/api/bff/tareas/{id}` | Actualizar tarea |
| DELETE | `/api/bff/tareas/{id}` | Eliminar tarea |

### Recursos Humanos
| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/api/bff/empleados` | Listar empleados |
| GET | `/api/bff/empleados/{id}` | Obtener empleado |
| POST | `/api/bff/empleados` | Crear empleado |
| PUT | `/api/bff/empleados/{id}` | Actualizar empleado |
| PATCH | `/api/bff/empleados/{id}/disponibilidad` | Cambiar disponibilidad |
| DELETE | `/api/bff/empleados/{id}` | Eliminar (baja lógica) |
| GET | `/api/bff/asignaciones` | Listar asignaciones |
| POST | `/api/bff/asignaciones` | Crear asignación |
| DELETE | `/api/bff/asignaciones/{id}` | Desactivar asignación |
| GET | `/api/bff/recursos/resumen` | Resumen de recursos |

### Mensajería interna (chat)
| Método | URL | Descripción |
|--------|-----|-------------|
| POST | `/api/bff/mensajes` | Enviar mensaje |
| GET | `/api/bff/mensajes/conversacion?email1=&email2=` | Historial entre dos usuarios |
| GET | `/api/bff/mensajes/inbox?email=` | Bandeja de entrada |
| PATCH | `/api/bff/mensajes/marcar-leidos?destinatario=&remitente=` | Marcar mensajes como leídos |

### Dashboard (BFF consolidado)
| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/api/bff/dashboard` | Dashboard consolidado (proyectos en vivo + analítica + recursos) |
| GET | `/api/bff/dashboard/kpis` | Todos los KPIs |
| GET | `/api/bff/dashboard/kpis/categoria?categoria=RECURSOS` | KPIs por categoría |
| GET | `/api/bff/circuit-breakers` | Estado de cada circuito (CLOSED/OPEN/HALF_OPEN) |

## Puertos de los servicios

| Servicio | Puerto |
|----------|--------|
| BFF | 8080 |
| ms-proyectos | 8081 |
| ms-analitica | 8083 |
| ms-recursos | 8084 |
| ms-mensajeria | 8085 |

## Estructura del proyecto

```
bff-innovatech/
├── src/
│   ├── main/java/cl/duoc/innovatech/bff_innovatech/
│   │   ├── BffInnovatechApplication.java
│   │   ├── config/
│   │   │   ├── RestTemplateConfig.java   ← Factory Method
│   │   │   ├── ResilienceConfig.java
│   │   │   ├── SwaggerConfig.java
│   │   │   └── CorsConfig.java
│   │   ├── security/
│   │   │   ├── SecurityConfig.java       ← usuarios en memoria + BCrypt
│   │   │   ├── JwtService.java
│   │   │   └── JwtAuthFilter.java
│   │   ├── client/
│   │   │   ├── MsProyectosClient.java
│   │   │   ├── MsRecursosClient.java
│   │   │   ├── MsAnaliticaClient.java
│   │   │   └── MsMensajeriaClient.java
│   │   ├── controller/
│   │   │   ├── AuthController.java
│   │   │   ├── ProyectoBffController.java
│   │   │   ├── TareaBffController.java
│   │   │   ├── RecursosBffController.java
│   │   │   ├── MensajeBffController.java
│   │   │   ├── DashboardBffController.java
│   │   │   └── CircuitBreakerStatusController.java
│   │   ├── service/
│   │   │   ├── ProyectoBffService.java
│   │   │   ├── TareaBffService.java
│   │   │   ├── RecursosBffService.java
│   │   │   ├── MensajeBffService.java
│   │   │   ├── DashboardBffService.java
│   │   │   └── impl/
│   │   │       ├── ProyectoBffServiceImpl.java
│   │   │       ├── TareaBffServiceImpl.java
│   │   │       ├── RecursosBffServiceImpl.java
│   │   │       ├── MensajeBffServiceImpl.java
│   │   │       └── DashboardBffServiceImpl.java  ← BFF pattern
│   │   ├── dto/
│   │   │   ├── request/
│   │   │   └── response/
│   │   └── exception/
│   │       ├── GlobalExceptionHandler.java
│   │       ├── MicroservicioNoDisponibleException.java
│   │       └── RecursoNoEncontradoException.java
│   └── test/
│       └── ... (118 pruebas con Mockito + MockMvc)
├── pom.xml
└── README.md
```
