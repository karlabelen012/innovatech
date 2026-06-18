# BFF – Innovatech Solutions

## Descripción

**Backend for Frontend (BFF)** de la plataforma Innovatech Solutions.  
Actúa como capa intermediaria entre el frontend React y los tres microservicios, exponiendo un API REST unificada, simplificada y orientada a las necesidades del cliente web.

```
Frontend React (3000)
        │
        ▼
BFF – bff-innovatech  ← Puerto 8080
  ├── /api/bff/proyectos     → ms-proyectos  (8081)
  ├── /api/bff/empleados     → ms-recursos   (8082)
  ├── /api/bff/asignaciones  → ms-recursos   (8082)
  ├── /api/bff/recursos/resumen → ms-recursos (8082)
  └── /api/bff/dashboard     → ms-analitica  (8083) + ms-recursos (8082)
```

## Patrones aplicados

| Patrón | Implementación |
|--------|----------------|
| **BFF (Backend for Frontend)** | Agrega datos de ms-analitica + ms-recursos en un único endpoint `/api/bff/dashboard` |
| **Factory Method** | `RestTemplateConfig` centraliza la creación del `RestTemplate` |
| **Repository / Client** | Clientes HTTP (`MsProyectosClient`, `MsRecursosClient`, `MsAnaliticaClient`) aíslan la comunicación HTTP |
| **Circuit Breaker manual** | Captura `ResourceAccessException` y lanza `MicroservicioNoDisponibleException`, dashboard sigue respondiendo parcialmente |

## Tecnologías

- Java 17
- Spring Boot 3.5.14
- Spring Web (RestTemplate)
- Spring Validation
- Springdoc OpenAPI 2.5.0 (Swagger UI)
- Lombok 1.18.40
- JaCoCo 0.8.12 (cobertura ≥ 60%)

## Requisitos previos

- Java 17+
- Maven 3.8+
- Los microservicios deben estar corriendo (ver puertos abajo)

## Instalación y ejecución

```bash
# Clonar / copiar el proyecto
cd bff-innovatech

# Compilar
./mvnw clean compile

# Ejecutar
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

### Proyectos
| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/api/bff/proyectos` | Listar todos (filtro: `?estado=EN_PROGRESO`) |
| GET | `/api/bff/proyectos/{id}` | Obtener por ID |
| POST | `/api/bff/proyectos` | Crear proyecto |
| PUT | `/api/bff/proyectos/{id}` | Actualizar proyecto |
| DELETE | `/api/bff/proyectos/{id}` | Eliminar proyecto |

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

### Dashboard (BFF consolidado)
| Método | URL | Descripción |
|--------|-----|-------------|
| GET | `/api/bff/dashboard` | Dashboard consolidado (analítica + recursos) |
| GET | `/api/bff/dashboard/kpis` | Todos los KPIs |
| GET | `/api/bff/dashboard/kpis/categoria?categoria=RECURSOS` | KPIs por categoría |

## Puertos de los servicios

| Servicio | Puerto |
|----------|--------|
| BFF | 8080 |
| ms-proyectos | 8081 |
| ms-recursos | 8082 |
| ms-analitica | 8083 |

## Estructura del proyecto

```
bff-innovatech/
├── src/
│   ├── main/java/cl/duoc/innovatech/bff_innovatech/
│   │   ├── BffInnovatechApplication.java
│   │   ├── config/
│   │   │   ├── RestTemplateConfig.java   ← Factory Method
│   │   │   ├── SwaggerConfig.java
│   │   │   └── CorsConfig.java
│   │   ├── client/
│   │   │   ├── MsProyectosClient.java
│   │   │   ├── MsRecursosClient.java
│   │   │   └── MsAnaliticaClient.java
│   │   ├── controller/
│   │   │   ├── ProyectoBffController.java
│   │   │   ├── RecursosBffController.java
│   │   │   └── DashboardBffController.java
│   │   ├── service/
│   │   │   ├── ProyectoBffService.java
│   │   │   ├── RecursosBffService.java
│   │   │   ├── DashboardBffService.java
│   │   │   └── impl/
│   │   │       ├── ProyectoBffServiceImpl.java
│   │   │       ├── RecursosBffServiceImpl.java
│   │   │       └── DashboardBffServiceImpl.java  ← BFF pattern
│   │   ├── dto/
│   │   │   ├── request/
│   │   │   └── response/
│   │   └── exception/
│   │       ├── GlobalExceptionHandler.java
│   │       ├── MicroservicioNoDisponibleException.java
│   │       └── RecursoNoEncontradoException.java
│   └── test/
│       └── ... (pruebas unitarias con Mockito + MockMvc)
├── pom.xml
└── README.md
```
