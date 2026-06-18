DROP TABLE IF EXISTS reportes_proyecto;
DROP TABLE IF EXISTS kpi_metricas;

CREATE TABLE reportes_proyecto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_proyecto VARCHAR(255) NOT NULL,
    proyecto_id_externo BIGINT NOT NULL,
    estado VARCHAR(50) NOT NULL,
    porcentaje_avance INT,
    total_tareas INT,
    tareas_completadas INT,
    tareas_pendientes INT,
    recursos_asignados INT,
    fecha_inicio DATE,
    fecha_fin_estimada DATE,
    fecha_registro TIMESTAMP NOT NULL
);

CREATE TABLE kpi_metricas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_kpi VARCHAR(255) NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    valor DOUBLE NOT NULL,
    unidad VARCHAR(255),
    descripcion VARCHAR(255),
    fecha_calculo TIMESTAMP NOT NULL
);
