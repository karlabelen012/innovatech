# MS-Recursos — Microservicio de Gestión de Recursos y Colaboración

> **Innovatech Solutions** · DSY1106 Desarrollo Fullstack III · Sección 303D  
> Integrantes: Bryan Muñoz — Karla Herrera · Profesor: Víctor Andrade

---

## Descripción

Microservicio responsable de la gestión del personal y la colaboración entre equipos de Innovatech Solutions. Permite registrar empleados, controlar su disponibilidad, asignarlos a proyectos y consultar métricas de carga laboral.

### Responsabilidades principales

| Módulo | Descripción |
|---|---|
| **Empleados** | CRUD completo, filtros por rol y disponibilidad |
| **Asignaciones** | Asociar empleados ↔ proyectos con horas y roles |
| **Disponibilidad** | Control automático de estado al asignar/desasignar |
| **Resumen** | Métricas de carga laboral para el módulo de analítica |

---

## Tecnologías

| Capa | Tecnología |
|---|---|
| Lenguaje | Java 17 |
| Framework | Spring Boot 3.2.5 |
| Persistencia | Spring Data JPA + Hibernate |
| Base de datos | MySQL 8 (producción) / H2 (pruebas) |
| Documentación API | SpringDoc OpenAPI / Swagger UI |
| Testing | JUnit 5 + Mockito + MockMvc |
| Cobertura | JaCoCo (mínimo 60%) |
| Build | Maven 3.9+ |

---

## Requisitos

- Java 17+
- Maven 3.9+
- MySQL 8 (solo para ejecución; pruebas usan H2 en memoria)

---

## Instalación y ejecución

### 1. Clonar el repositorio

```bash
git clone <url-repositorio>
cd ms-recursos
```

### 2. Configurar base de datos MySQL

Crear la base de datos (el esquema se genera automáticamente con `ddl-auto=update`):

```sql
CREATE DATABASE ms_recursos_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Ajustar credenciales en `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ms_recursos_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Santiago
spring.datasource.username=root
spring.datasource.password=root
```

### 3. Compilar el proyecto

```bash
mvn clean install -DskipTests
```

### 4. Ejecutar el microservicio

```bash
mvn spring-boot:run
```

El servicio queda disponible en: `http://localhost:8082`

---

## Documentación API (Swagger UI)

Una vez iniciado el servicio, acceder a:

```
http://localhost:8082/swagger-ui.html
```

Especificación OpenAPI en JSON:
```
http://localhost:8082/api-docs
```

---

## Endpoints principales

### Empleados — `/api/v1/empleados`

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/` | Crear empleado |
| `GET` | `/` | Listar todos |
| `GET` | `/?activo=true` | Solo activos |
| `GET` | `/?disponibilidad=DISPONIBLE` | Filtrar por disponibilidad |
| `GET` | `/?rol=DESARROLLADOR` | Filtrar por rol |
| `GET` | `/?habilidad=Java` | Buscar por habilidad |
| `GET` | `/{id}` | Obtener por ID |
| `PUT` | `/{id}` | Actualizar |
| `PATCH` | `/{id}?disponibilidad=OCUPADO` | Cambiar disponibilidad |
| `DELETE` | `/{id}` | Baja lógica |

### Asignaciones — `/api/v1/asignaciones`

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/` | Crear asignación |
| `GET` | `/` | Listar todas |
| `GET` | `/?empleadoId=1` | Por empleado |
| `GET` | `/?proyectoId=5` | Por proyecto |
| `GET` | `/{id}` | Obtener por ID |
| `PUT` | `/{id}` | Actualizar |
| `DELETE` | `/{id}` | Desactivar |
| `GET` | `/equipo` | Carga laboral del equipo |
| `GET` | `/resumen` | Métricas generales |

---

## Pruebas unitarias

### Ejecutar todas las pruebas

```bash
mvn test
```

### Ejecutar pruebas y generar reporte de cobertura JaCoCo

```bash
mvn clean test jacoco:report
```

El reporte HTML se genera en:
```
target/site/jacoco/index.html
```

### Estructura de pruebas

```
src/test/java/cl/duoc/innovatech/ms_recursos/
├── MsRecursosApplicationTests.java        # Carga del contexto
├── service/
│   ├── EmpleadoServiceTest.java           # 14 pruebas unitarias
│   └── AsignacionServiceTest.java         # 12 pruebas unitarias
├── controller/
│   └── EmpleadoControllerTest.java        # 8 pruebas con MockMvc
└── repository/
    └── EmpleadoRepositoryTest.java        # 7 pruebas de integración con H2
```

**Total: 42 pruebas · Cobertura objetivo: ≥ 60%**

---

## Persistencia de datos (JPA)

La persistencia se implementa mediante **Spring Data JPA + Hibernate**:

- `Empleado` y `Asignacion` son entidades anotadas con `@Entity`
- Se utiliza el patrón **Repository** (`JpaRepository`) para desacoplar acceso a datos
- Las relaciones `@OneToMany` / `@ManyToOne` están mapeadas entre entidades
- Transacciones gestionadas con `@Transactional`
- Validaciones a nivel de modelo con `jakarta.validation`
- En producción: MySQL con `ddl-auto=update`
- En pruebas: H2 en memoria con `ddl-auto=create-drop`

---

## Estructura del proyecto

```
ms-recursos/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/cl/duoc/innovatech/ms_recursos/
    │   │   ├── MsRecursosApplication.java
    │   │   ├── config/         SwaggerConfig.java
    │   │   ├── controller/     EmpleadoController, AsignacionController
    │   │   ├── dto/            EmpleadoDTO, AsignacionDTO, DisponibilidadDTO, ResumenRecursosDTO
    │   │   ├── exception/      GlobalExceptionHandler, excepciones custom
    │   │   ├── model/          Empleado, Asignacion + enums
    │   │   ├── repository/     EmpleadoRepository, AsignacionRepository
    │   │   └── service/        Interfaces + impl/
    │   └── resources/
    │       ├── application.properties
    │       └── application-test.properties
    └── test/
        └── java/...            (42 pruebas)
```

---

## Puerto y comunicación

- **Puerto:** `8082`
- El BFF (`bff-innovatech`) consume este servicio en `http://localhost:8082`
- **CORS:** habilitado para todos los orígenes (`@CrossOrigin(origins = "*")`)
