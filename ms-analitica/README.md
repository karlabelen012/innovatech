# MS-Analítica — Microservicio de Reportes y KPIs

> **Innovatech Solutions** · DSY1106 Desarrollo Fullstack III · Sección 303D  
> Integrantes: Bryan Muñoz — Karla Herrera · Profesor: Víctor Andrade

---

## Descripción

Microservicio responsable de generar y gestionar reportes de proyectos e indicadores clave de rendimiento (KPIs) para el dashboard de Innovatech Solutions. Consume datos provenientes de ms-proyectos y ms-recursos a través del BFF para consolidar la analítica del sistema.

### Responsabilidades principales

| Módulo | Descripción |
|---|---|
| **KPI Métricas** | CRUD de indicadores por categoría con cálculo de promedios |
| **Reportes de Proyecto** | Seguimiento de avance, tareas y recursos por proyecto |
| **Dashboard** | Consolidación de métricas para la vista ejecutiva |

---

## Tecnologías

| Capa | Tecnología |
|---|---|
| Lenguaje | Java 17 |
| Framework | Spring Boot 3.4.x |
| Persistencia | Spring Data JPA + Hibernate |
| Base de datos | MySQL 8 (producción) / H2 (pruebas) |
| Documentación API | SpringDoc OpenAPI / Swagger UI |
| Testing | JUnit 5 + Mockito + MockMvc |
| Cobertura | JaCoCo 0.8.13 (mínimo 60%) |
| Build | Maven 3.9+ |

---

## Requisitos

- Java 17+
- Maven 3.9+
- MySQL 8 (solo para ejecución; las pruebas usan H2 en memoria)

---

## Instalación y ejecución

### 1. Clonar el repositorio

```bash
git clone <url-repositorio>
cd ms-analitica
```

### 2. Configurar base de datos MySQL

El esquema se genera automáticamente con `ddl-auto=update`. Ajustar credenciales en `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ms_analitica_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Santiago
spring.datasource.username=root
spring.datasource.password=
```

### 3. Compilar el proyecto

```bash
./mvnw clean install -DskipTests
```

### 4. Ejecutar el microservicio

```bash
./mvnw spring-boot:run
```

El servicio queda disponible en: `http://localhost:8083`

---

## Documentación API (Swagger UI)

Una vez iniciado el servicio, acceder a:

```
http://localhost:8083/swagger-ui.html
```

Especificación OpenAPI en JSON:
```
http://localhost:8083/api-docs
```

---

## Endpoints principales

### KPI Métricas — `/api/v1/kpis`

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/` | Listar todos los KPIs |
| `GET` | `/{id}` | Obtener KPI por ID |
| `GET` | `/categoria/{categoria}` | Filtrar por categoría |
| `GET` | `/promedios` | Promedios de KPIs por categoría |
| `POST` | `/` | Crear nuevo KPI |
| `PUT` | `/{id}` | Actualizar KPI existente |
| `DELETE` | `/{id}` | Eliminar KPI |

#### Ejemplo — Crear KPI (`POST /api/v1/kpis`)

```json
{
  "nombreKpi": "Velocidad de entrega",
  "categoria": "PROYECTOS",
  "valor": 85.5,
  "unidad": "%",
  "descripcion": "Porcentaje de tareas entregadas a tiempo"
}
```

### Reportes de Proyecto — `/api/v1/reportes`

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/` | Listar todos los reportes |
| `GET` | `/{id}` | Obtener reporte por ID |
| `GET` | `/externo/{proyectoIdExterno}` | Reporte por ID externo del proyecto |
| `GET` | `/estado/{estado}` | Filtrar por estado del proyecto |
| `GET` | `/promedio-avance` | Promedio de avance de proyectos activos |
| `POST` | `/` | Crear nuevo reporte |
| `PUT` | `/{id}` | Actualizar reporte existente |
| `DELETE` | `/{id}` | Eliminar reporte |

#### Ejemplo — Crear reporte (`POST /api/v1/reportes`)

```json
{
  "nombreProyecto": "Sistema CRM",
  "proyectoIdExterno": 1,
  "estado": "EN_PROGRESO",
  "porcentajeAvance": 65,
  "totalTareas": 20,
  "tareasCompletadas": 13,
  "tareasPendientes": 7,
  "recursosAsignados": 4,
  "fechaInicio": "2025-01-10",
  "fechaFinEstimada": "2025-06-30"
}
```

---

## Pruebas unitarias

### Ejecutar todas las pruebas

```bash
./mvnw test
```

### Ejecutar pruebas y generar reporte de cobertura JaCoCo

```bash
./mvnw clean test jacoco:report
```

El reporte HTML se genera en:
```
target/site/jacoco/index.html
```

### Estructura de pruebas

```
src/test/java/cl/duoc/innovatech/ms_analitica/
├── MsAnaliticaApplicationTests.java              # 1 prueba de contexto
├── repository/
│   └── ReporteProyectoRepositoryTest.java        # 6 pruebas con H2
├── service/
│   ├── KpiMetricaServiceTest.java                # 12 pruebas unitarias
│   └── ReporteProyectoServiceTest.java           # 13 pruebas unitarias
└── controller/
    ├── KpiMetricaControllerTest.java             # 8 pruebas con MockMvc
    └── ReporteProyectoControllerTest.java        # 8 pruebas con MockMvc
```

**Total: 48 pruebas · Cobertura: 68.9%**

---

## Persistencia de datos (JPA)

- `KpiMetrica` → tabla `kpi_metricas`: id, nombreKpi, categoria (enum), valor, unidad, descripcion, fechaCalculo
- `ReporteProyecto` → tabla `reportes_proyecto`: id, nombreProyecto, proyectoIdExterno, estado (enum), porcentajeAvance, totalTareas, tareasCompletadas, tareasPendientes, recursosAsignados, fechaInicio, fechaFinEstimada, fechaRegistro
- En producción: MySQL con `ddl-auto=update`
- En pruebas: H2 en memoria con `ddl-auto=create-drop`

---

## Estructura del proyecto

```
ms-analitica/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/cl/duoc/innovatech/ms_analitica/
    │   │   ├── MsAnaliticaApplication.java
    │   │   ├── config/         SwaggerConfig.java
    │   │   ├── controller/     KpiMetricaController, ReporteProyectoController, DashboardController
    │   │   ├── dto/            KpiMetricaDTO, ReporteProyectoDTO, DashboardDTO
    │   │   ├── exception/      GlobalExceptionHandler, RecursoNoEncontradoException
    │   │   ├── model/          KpiMetrica, ReporteProyecto + enums (CategoriaKpi, EstadoProyecto)
    │   │   ├── repository/     KpiMetricaRepository, ReporteProyectoRepository
    │   │   └── service/        Interfaces + impl/
    │   └── resources/
    │       └── application.properties
    └── test/
        └── java/...            (48 pruebas)
```

---

## Puerto y comunicación

- **Puerto:** `8083`
- El BFF (`bff-innovatech`) consume este servicio en `http://localhost:8083`
- Base de datos: `ms_analitica_db`
