package cl.duoc.innovatech.bff_innovatech.service.impl;

import cl.duoc.innovatech.bff_innovatech.client.MsRecursosClient;
import cl.duoc.innovatech.bff_innovatech.dto.request.AsignacionRequest;
import cl.duoc.innovatech.bff_innovatech.dto.request.EmpleadoRequest;
import cl.duoc.innovatech.bff_innovatech.dto.response.AsignacionResponse;
import cl.duoc.innovatech.bff_innovatech.dto.response.EmpleadoResponse;
import cl.duoc.innovatech.bff_innovatech.dto.response.ResumenRecursosResponse;
import cl.duoc.innovatech.bff_innovatech.service.RecursosBffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecursosBffServiceImpl implements RecursosBffService {

    private final MsRecursosClient msRecursosClient;

    @Override
    public List<EmpleadoResponse> listarEmpleados() {
        log.info("BFF → ms-recursos: listar empleados");
        return msRecursosClient.listarEmpleados();
    }

    @Override
    public EmpleadoResponse obtenerEmpleadoPorId(Long id) {
        log.info("BFF → ms-recursos: obtener empleado id={}", id);
        return msRecursosClient.obtenerEmpleadoPorId(id);
    }

    @Override
    public EmpleadoResponse crearEmpleado(EmpleadoRequest request) {
        log.info("BFF → ms-recursos: crear empleado '{}'", request.getEmail());
        return msRecursosClient.crearEmpleado(request);
    }

    @Override
    public EmpleadoResponse actualizarEmpleado(Long id, EmpleadoRequest request) {
        log.info("BFF → ms-recursos: actualizar empleado id={}", id);
        return msRecursosClient.actualizarEmpleado(id, request);
    }

    @Override
    public EmpleadoResponse actualizarDisponibilidad(Long id, String disponibilidad) {
        log.info("BFF → ms-recursos: disponibilidad empleado id={} => {}", id, disponibilidad);
        return msRecursosClient.actualizarDisponibilidad(id, disponibilidad);
    }

    @Override
    public void eliminarEmpleado(Long id) {
        log.info("BFF → ms-recursos: eliminar empleado id={}", id);
        msRecursosClient.eliminarEmpleado(id);
    }

    @Override
    public List<AsignacionResponse> listarAsignaciones() {
        log.info("BFF → ms-recursos: listar asignaciones");
        return msRecursosClient.listarAsignaciones();
    }

    @Override
    public AsignacionResponse crearAsignacion(AsignacionRequest request) {
        log.info("BFF → ms-recursos: crear asignación empleado={} proyecto={}", 
                request.getEmpleadoId(), request.getProyectoId());
        return msRecursosClient.crearAsignacion(request);
    }

    @Override
    public void desactivarAsignacion(Long id) {
        log.info("BFF → ms-recursos: desactivar asignación id={}", id);
        msRecursosClient.desactivarAsignacion(id);
    }

    @Override
    public ResumenRecursosResponse obtenerResumen() {
        log.info("BFF → ms-recursos: resumen de recursos");
        return msRecursosClient.obtenerResumen();
    }
}
