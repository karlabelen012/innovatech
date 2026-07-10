# MS-Proyectos — Microservicio de Gestión de Proyectos y Tareas

> **Innovatech Solutions** · DSY1106 Desarrollo Fullstack III · Sección 303D  
> Integrantes: Bryan Muñoz — Karla Herrera · Profesor: Víctor Andrade

---

## Descripción

Microservicio responsable de la gestión del ciclo de vida de proyectos y sus tareas asociadas en Innovatech Solutions. Permite crear, actualizar y controlar el estado y avance de proyectos, así como las tareas que los componen.

### Responsabilidades principales

| Módulo | Descripción |
|---|---|
| **Proyectos** | CRUD completo, filtros por estado y avance |
| **Tareas** | Creación y seguimiento de tareas vinculadas a un proyecto |
| **Estados** | Control de ciclo de vida: `PENDIENTE` → `EN_PROGRESO` → `FINALIZADO` |

---

## Tecnologías

| Capa | Tecnología |
|---|---|
| Lenguaje | Java 17 |
| Framework | Spring Boot 3.1.x |
| Persistencia | Spring Data JPA + Hibernate |
| Base de datos | MySQL 8 (producción) / H2 (pruebas) |
| Testing | JUnit 5 + Mockito |
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
cd ms-proyectos
```

### 2. Configurar base de datos MySQL

El esquema se genera automáticamente con `ddl-auto=update`. Solo es necesario ajustar las credenciales en `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ms_proyectos_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
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

El servicio queda disponible en: `http://localhost:8081`

> Al primer arranque, si las tablas `proyectos` y `tareas` están vacías, `ProyectosDataSeeder` carga automáticamente 3 proyectos de ejemplo con sus tareas. Si ya hay datos, el seeder se omite.

---

## Endpoints principales

### Proyectos — `/api/v1/proyectos`

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/` | Listar todos los proyectos |
| `GET` | `/{id}` | Obtener proyecto por ID |
| `GET` | `/estado/{estado}` | Filtrar proyectos por estado |
| `POST` | `/` | Crear nuevo proyecto |
| `PUT` | `/{id}` | Actualizar proyecto existente |
| `DELETE` | `/{id}` | Eliminar proyecto |

### Tareas — `/api/v1/tareas`

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/proyecto/{proyectoId}` | Listar tareas de un proyecto |
| `GET` | `/{id}` | Obtener tarea por ID |
| `POST` | `/` | Crear nueva tarea |
| `PUT` | `/{id}` | Actualizar tarea existente |
| `DELETE` | `/{id}` | Eliminar tarea |

#### Ejemplo — Crear proyecto (`POST /api/v1/proyectos`)

```json
{
  "nombre": "Sistema CRM",
  "descripcion": "Desarrollo del módulo de clientes",
  "estado": "EN_PROGRESO",
  "avance": 40,
  "responsable": "Juan Pérez"
}
```

#### Respuesta exitosa (`201 Created`)

```json
{
  "id": 1,
  "nombre": "Sistema CRM",
  "descripcion": "Desarrollo del módulo de clientes",
  "estado": "EN_PROGRESO",
  "avance": 40,
  "responsable": "Juan Pérez"
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
src/test/java/cl/duoc/innovatech/ms_proyectos/
├── service/
│   ├── ProyectoServiceTest.java       # 9 pruebas unitarias
│   └── TareaServiceTest.java          # 9 pruebas unitarias
├── controller/
│   ├── ProyectoControllerTest.java    # 10 pruebas con MockMvc
│   └── TareaControllerTest.java       # 8 pruebas con MockMvc
└── config/
    └── ProyectosDataSeederTest.java   # 2 pruebas unitarias del seed inicial
```

**Total: 38 pruebas · Cobertura: 85.6%**

---

## Persistencia de datos (JPA)

La persistencia se implementa mediante **Spring Data JPA + Hibernate**:

- `Proyecto` y `Tarea` son entidades anotadas con `@Entity`
- Se utiliza el patrón **Repository** (`JpaRepository`) para el acceso a datos
- Validaciones a nivel de modelo con `jakarta.validation` (`@NotBlank`, `@Column(nullable = false)`)
- En producción: MySQL con `ddl-auto=update` (tablas `proyectos` y `tareas`)
- En pruebas: H2 en memoria con `ddl-auto=create-drop`

---

## Estructura del proyecto

```
ms-proyectos/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/cl/duoc/innovatech/ms_proyectos/
    │   │   ├── MsProyectosApplication.java
    │   │   ├── config/         SwaggerConfig.java, ProyectosDataSeeder.java
    │   │   ├── controller/     ProyectoController.java, TareaController.java
    │   │   ├── dto/            ProyectoDTO.java, TareaDTO.java
    │   │   ├── exception/      GlobalExceptionHandler.java, RecursoNoEncontradoException.java
    │   │   ├── model/          Proyecto.java, Tarea.java
    │   │   ├── repository/     ProyectoRepository.java, TareaRepository.java
    │   │   └── service/        ProyectoService.java, TareaService.java + impl/
    │   └── resources/
    │       └── application.properties
    └── test/
        └── java/...            (38 pruebas)
```

---

## Puerto y comunicación

- **Puerto:** `8081`
- El BFF (`bff-innovatech`) consume este servicio en `http://localhost:8081`
- Base de datos: `ms_proyectos_db`
