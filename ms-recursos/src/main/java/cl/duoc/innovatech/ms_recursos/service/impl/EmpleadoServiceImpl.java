package cl.duoc.innovatech.ms_recursos.service.impl;

import cl.duoc.innovatech.ms_recursos.dto.EmpleadoDTO;
import cl.duoc.innovatech.ms_recursos.exception.EmailDuplicadoException;
import cl.duoc.innovatech.ms_recursos.exception.RecursoNoEncontradoException;
import cl.duoc.innovatech.ms_recursos.model.Empleado;
import cl.duoc.innovatech.ms_recursos.model.enums.DisponibilidadEstado;
import cl.duoc.innovatech.ms_recursos.model.enums.RolEmpleado;
import cl.duoc.innovatech.ms_recursos.repository.EmpleadoRepository;
import cl.duoc.innovatech.ms_recursos.service.EmpleadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EmpleadoServiceImpl implements EmpleadoService {

    private final EmpleadoRepository empleadoRepository;

    @Override
    public EmpleadoDTO crear(EmpleadoDTO dto) {
        if (empleadoRepository.existsByEmail(dto.getEmail())) {
            throw new EmailDuplicadoException(dto.getEmail());
        }
        Empleado empleado = toEntity(dto);
        return toDTO(empleadoRepository.save(empleado));
    }

    @Override
    @Transactional(readOnly = true)
    public EmpleadoDTO obtenerPorId(Long id) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Empleado no encontrado con id: " + id));
        return toDTO(empleado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoDTO> obtenerTodos() {
        return empleadoRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoDTO> obtenerActivos() {
        return empleadoRepository.findByActivoTrue().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EmpleadoDTO actualizar(Long id, EmpleadoDTO dto) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Empleado no encontrado con id: " + id));

        if (!empleado.getEmail().equals(dto.getEmail()) && empleadoRepository.existsByEmail(dto.getEmail())) {
            throw new EmailDuplicadoException(dto.getEmail());
        }

        empleado.setNombre(dto.getNombre());
        empleado.setApellido(dto.getApellido());
        empleado.setEmail(dto.getEmail());
        empleado.setTelefono(dto.getTelefono());
        empleado.setRol(dto.getRol());
        empleado.setDisponibilidad(dto.getDisponibilidad());
        empleado.setHorasSemanales(dto.getHorasSemanales());
        empleado.setHabilidades(dto.getHabilidades());

        return toDTO(empleadoRepository.save(empleado));
    }

    @Override
    public void eliminar(Long id) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Empleado no encontrado con id: " + id));
        empleado.setActivo(false);
        empleadoRepository.save(empleado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoDTO> obtenerPorDisponibilidad(DisponibilidadEstado estado) {
        return empleadoRepository.findByDisponibilidadAndActivoTrue(estado).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoDTO> obtenerPorRol(RolEmpleado rol) {
        return empleadoRepository.findByRolAndActivoTrue(rol).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EmpleadoDTO actualizarDisponibilidad(Long id, DisponibilidadEstado estado) {
        Empleado empleado = empleadoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Empleado no encontrado con id: " + id));
        empleado.setDisponibilidad(estado);
        return toDTO(empleadoRepository.save(empleado));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoDTO> buscarPorHabilidad(String habilidad) {
        return empleadoRepository.findByHabilidad(habilidad).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ========== Mappers ==========
    private EmpleadoDTO toDTO(Empleado e) {
        return EmpleadoDTO.builder()
                .id(e.getId())
                .nombre(e.getNombre())
                .apellido(e.getApellido())
                .email(e.getEmail())
                .telefono(e.getTelefono())
                .rol(e.getRol())
                .disponibilidad(e.getDisponibilidad())
                .horasSemanales(e.getHorasSemanales())
                .habilidades(e.getHabilidades())
                .fechaIngreso(e.getFechaIngreso())
                .activo(e.getActivo())
                .build();
    }

    private Empleado toEntity(EmpleadoDTO dto) {
        return Empleado.builder()
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .email(dto.getEmail())
                .telefono(dto.getTelefono())
                .rol(dto.getRol())
                .disponibilidad(dto.getDisponibilidad() != null ? dto.getDisponibilidad() : DisponibilidadEstado.DISPONIBLE)
                .horasSemanales(dto.getHorasSemanales())
                .habilidades(dto.getHabilidades())
                .fechaIngreso(dto.getFechaIngreso())
                .activo(true)
                .build();
    }
}
