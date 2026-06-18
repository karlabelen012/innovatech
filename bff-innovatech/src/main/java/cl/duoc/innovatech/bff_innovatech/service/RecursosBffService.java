package cl.duoc.innovatech.bff_innovatech.service;

import cl.duoc.innovatech.bff_innovatech.dto.request.AsignacionRequest;
import cl.duoc.innovatech.bff_innovatech.dto.request.EmpleadoRequest;
import cl.duoc.innovatech.bff_innovatech.dto.response.AsignacionResponse;
import cl.duoc.innovatech.bff_innovatech.dto.response.EmpleadoResponse;
import cl.duoc.innovatech.bff_innovatech.dto.response.ResumenRecursosResponse;

import java.util.List;

public interface RecursosBffService {
    List<EmpleadoResponse> listarEmpleados();
    EmpleadoResponse obtenerEmpleadoPorId(Long id);
    EmpleadoResponse crearEmpleado(EmpleadoRequest request);
    EmpleadoResponse actualizarEmpleado(Long id, EmpleadoRequest request);
    EmpleadoResponse actualizarDisponibilidad(Long id, String disponibilidad);
    void eliminarEmpleado(Long id);

    List<AsignacionResponse> listarAsignaciones();
    AsignacionResponse crearAsignacion(AsignacionRequest request);
    void desactivarAsignacion(Long id);
    ResumenRecursosResponse obtenerResumen();
}
