# MS-Mensajería — Microservicio de Mensajería Interna

> **Innovatech Solutions** · DSY1106 Desarrollo Fullstack III · Sección 303D
> Integrantes: Bryan Muñoz — Karla Herrera · Profesor: Víctor Andrade

---

## Descripción

Microservicio responsable del chat interno entre empleados de Innovatech Solutions. Permite enviar mensajes, consultar el historial de una conversación y la bandeja de entrada con el resumen de no leídos.

### Responsabilidades principales

| Módulo | Descripción |
|---|---|
| **Mensajes** | Envío y consulta de mensajes entre dos usuarios |
| **Conversación** | Historial completo entre dos direcciones de correo |
| **Inbox** | Bandeja de entrada agrupada por contacto, con conteo de no leídos |
| **Estado de lectura** | Marcado masivo de mensajes como leídos |

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
| Cobertura | JaCoCo (mínimo 60%) |
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
cd ms-mensajeria
```

### 2. Configurar base de datos MySQL

El esquema se genera automáticamente con `ddl-auto=update`. Ajustar credenciales en `src/main/resources/application.properties` si es necesario:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ms_mensajeria_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Santiago
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

El servicio queda disponible en: `http://localhost:8085`

> Al primer arranque, si la tabla `mensajes` está vacía, `MensajeriaDataSeeder` carga automáticamente una conversación de ejemplo entre los tres empleados de prueba (`bryan@innovatech.cl`, `karla@innovatech.cl`, `carlos@innovatech.cl`). Si ya hay datos, el seeder se omite.

---

## Documentación API (Swagger UI)

```
http://localhost:8085/swagger-ui.html
http://localhost:8085/api-docs
```

---

## Endpoints principales

### Mensajes — `/api/v1/mensajes`

| Método | Ruta | Descripción |
|---|---|---|
| `POST` | `/` | Enviar un mensaje |
| `GET` | `/{id}` | Obtener un mensaje por ID |
| `GET` | `/conversacion?email1=&email2=` | Historial completo entre dos usuarios |
| `GET` | `/inbox?email=` | Bandeja de entrada (resumen por contacto + no leídos) |
| `PATCH` | `/marcar-leidos?destinatario=&remitente=` | Marcar como leídos todos los mensajes de un remitente hacia un destinatario |

#### Ejemplo — Enviar mensaje (`POST /api/v1/mensajes`)

```json
{
  "remitenteEmail": "karla@innovatech.cl",
  "remitenteNombre": "Karla Herrera",
  "destinatarioEmail": "bryan@innovatech.cl",
  "destinatarioNombre": "Bryan Muñoz",
  "contenido": "¿Cómo va el avance del portal financiero?"
}
```

#### Respuesta exitosa (`201 Created`)

```json
{
  "id": 7,
  "remitenteEmail": "karla@innovatech.cl",
  "remitenteNombre": "Karla Herrera",
  "destinatarioEmail": "bryan@innovatech.cl",
  "destinatarioNombre": "Bryan Muñoz",
  "contenido": "¿Cómo va el avance del portal financiero?",
  "fechaEnvio": "2026-07-10T10:15:00",
  "leido": false
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
src/test/java/cl/duoc/innovatech/ms_mensajeria/
├── service/
│   └── MensajeServiceTest.java          # 6 pruebas unitarias
├── controller/
│   └── MensajeControllerTest.java       # 7 pruebas con MockMvc
└── config/
    └── MensajeriaDataSeederTest.java    # 2 pruebas unitarias del seed inicial
```

**Total: 15 pruebas · Cobertura: 81.2%**

---

## Persistencia de datos (JPA)

- `Mensaje` → tabla `mensajes`: id, remitenteEmail, remitenteNombre, destinatarioEmail, destinatarioNombre, contenido, fechaEnvio, leido
- Se utiliza el patrón **Repository** (`JpaRepository`) para el acceso a datos
- Validaciones a nivel de modelo con `jakarta.validation` (`@NotBlank`, `@Size`)
- En producción: MySQL con `ddl-auto=update`
- En pruebas: H2 en memoria con `ddl-auto=create-drop`

---

## Estructura del proyecto

```
ms-mensajeria/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/cl/duoc/innovatech/ms_mensajeria/
    │   │   ├── MsMensajeriaApplication.java
    │   │   ├── config/         SwaggerConfig.java, MensajeriaDataSeeder.java
    │   │   ├── controller/     MensajeController.java
    │   │   ├── dto/            MensajeDTO.java, ConversacionDTO.java
    │   │   ├── exception/      GlobalExceptionHandler.java, RecursoNoEncontradoException.java
    │   │   ├── model/          Mensaje.java
    │   │   ├── repository/     MensajeRepository.java
    │   │   └── service/        MensajeService.java + impl/
    │   └── resources/
    │       └── application.properties
    └── test/
        └── java/...            (15 pruebas)
```

---

## Puerto y comunicación

- **Puerto:** `8085`
- El BFF (`bff-innovatech`) consume este servicio en `http://localhost:8085`
- Base de datos: `ms_mensajeria_db`
