package cl.duoc.innovatech.ms_recursos.service.impl;

import cl.duoc.innovatech.ms_recursos.dto.AsignacionDTO;
import cl.duoc.innovatech.ms_recursos.dto.DisponibilidadDTO;
import cl.duoc.innovatech.ms_recursos.dto.ResumenRecursosDTO;
import cl.duoc.innovatech.ms_recursos.exception.RecursoNoEncontradoException;
import cl.duoc.innovatech.ms_recursos.model.Asignacion;
import cl.duoc.innovatech.ms_recursos.model.Empleado;
import cl.duoc.innovatech.ms_recursos.model.enums.DisponibilidadEstado;
import cl.duoc.innovatech.ms_recursos.repository.AsignacionRepository;
import cl.duoc.innovatech.ms_recursos.repository.EmpleadoRepository;
import cl.duoc.innovatech.ms_recursos.service.AsignacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AsignacionServiceImpl implements AsignacionService {

    private final AsignacionRepository asignacionRepository;
    private final EmpleadoRepository empleadoRepository;

    @Override
    public AsignacionDTO crear(AsignacionDTO dto) {
        Empleado empleado = empleadoRepository.findById(dto.getEmpleadoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Empleado no encontrado con id: " + dto.getEmpleadoId()));

        Asignacion asignacion = Asignacion.builder()
                .empleado(empleado)
                .proyectoId(dto.getProyectoId())
                .nombreProyecto(dto.getNombreProyecto())
                .fechaInicio(dto.getFechaInicio())
                .fechaFin(dto.getFechaFin())
                .horasAsignadas(dto.getHorasAsignadas())
                .rolEnProyecto(dto.getRolEnProyecto())
                .activo(true)
                .build();

        // Actualizar disponibilidad del empleado automáticamente
        empleado.setDisponibilidad(DisponibilidadEstado.OCUPADO);
        empleadoRepository.save(empleado);

        return toDTO(asignacionRepository.save(asignacion));
    }

    @Override
    @Transactional(readOnly = true)
    public AsignacionDTO obtenerPorId(Long id) {
        return toDTO(asignacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Asignación no encontrada con id: " + id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsignacionDTO> obtenerTodas() {
        return asignacionRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsignacionDTO> obtenerPorEmpleado(Long empleadoId) {
        return asignacionRepository.findByEmpleadoId(empleadoId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AsignacionDTO> obtenerPorProyecto(Long proyectoId) {
        return asignacionRepository.findByProyectoId(proyectoId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AsignacionDTO actualizar(Long id, AsignacionDTO dto) {
        Asignacion asignacion = asignacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Asignación no encontrada con id: " + id));

        asignacion.setNombreProyecto(dto.getNombreProyecto());
        asignacion.setFechaInicio(dto.getFechaInicio());
        asignacion.setFechaFin(dto.getFechaFin());
        asignacion.setHorasAsignadas(dto.getHorasAsignadas());
        asignacion.setRolEnProyecto(dto.getRolEnProyecto());

        return toDTO(asignacionRepository.save(asignacion));
    }

    @Override
    public void desactivar(Long id) {
        Asignacion asignacion = asignacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Asignación no encontrada con id: " + id));
        asignacion.setActivo(false);
        asignacionRepository.save(asignacion);

        // Si el empleado no tiene más asignaciones activas, lo libera
        Long empleadoId = asignacion.getEmpleado().getId();
        List<Asignacion> activas = asignacionRepository.findByEmpleadoIdAndActivoTrue(empleadoId);
        if (activas.isEmpty()) {
            empleadoRepository.findById(empleadoId).ifPresent(emp -> {
                emp.setDisponibilidad(DisponibilidadEstado.DISPONIBLE);
                empleadoRepository.save(emp);
            });
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<DisponibilidadDTO> obtenerDisponibilidadEquipo() {
        return empleadoRepository.findByActivoTrue().stream().map(e -> {
            Integer horasAsignadas = asignacionRepository.sumHorasAsignadasByEmpleadoId(e.getId());
            if (horasAsignadas == null) horasAsignadas = 0;
            int horasSem = e.getHorasSemanales() != null ? e.getHorasSemanales() : 40;
            return DisponibilidadDTO.builder()
                    .empleadoId(e.getId())
                    .nombre(e.getNombre())
                    .apellido(e.getApellido())
                    .rol(e.getRol())
                    .disponibilidad(e.getDisponibilidad())
                    .horasSemanales(horasSem)
                    .horasAsignadas(horasAsignadas)
                    .horasLibres(Math.max(0, horasSem - horasAsignadas))
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ResumenRecursosDTO obtenerResumen() {
        return ResumenRecursosDTO.builder()
                .totalEmpleados(empleadoRepository.countByActivoTrue())
                .empleadosDisponibles(empleadoRepository.countByDisponibilidad(DisponibilidadEstado.DISPONIBLE))
                .empleadosOcupados(empleadoRepository.countByDisponibilidad(DisponibilidadEstado.OCUPADO))
                .empleadosEnVacaciones(empleadoRepository.countByDisponibilidad(DisponibilidadEstado.VACACIONES))
                .totalAsignacionesActivas(asignacionRepository.countByActivoTrue())
                .build();
    }

    private AsignacionDTO toDTO(Asignacion a) {
        return AsignacionDTO.builder()
                .id(a.getId())
                .empleadoId(a.getEmpleado().getId())
                .empleadoNombre(a.getEmpleado().getNombre() + " " + a.getEmpleado().getApellido())
                .proyectoId(a.getProyectoId())
                .nombreProyecto(a.getNombreProyecto())
                .fechaInicio(a.getFechaInicio())
                .fechaFin(a.getFechaFin())
                .horasAsignadas(a.getHorasAsignadas())
                .rolEnProyecto(a.getRolEnProyecto())
                .activo(a.getActivo())
                .build();
    }
}
