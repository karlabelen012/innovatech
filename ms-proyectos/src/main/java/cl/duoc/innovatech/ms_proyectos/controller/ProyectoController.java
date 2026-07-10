package cl.duoc.innovatech.ms_proyectos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.innovatech.ms_proyectos.dto.ProyectoDTO;
import cl.duoc.innovatech.ms_proyectos.service.ProyectoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/proyectos")
@RequiredArgsConstructor
@Tag(name = "Proyectos", description = "Gestión de proyectos de Innovatech Solutions")
public class ProyectoController {

    private final ProyectoService proyectoService;

    @GetMapping
    @Operation(summary = "Listar proyectos",
            description = "Sin parámetros retorna todos. Acepta filtro: ?estado=PENDIENTE | EN_PROGRESO | FINALIZADO")
    public ResponseEntity<List<ProyectoDTO>> listar(
            @Parameter(description = "Filtrar por estado: PENDIENTE, EN_PROGRESO, FINALIZADO")
            @RequestParam(required = false) String estado) {
        if (estado != null) {
            return ResponseEntity.ok(proyectoService.listarPorEstado(estado));
        }
        return ResponseEntity.ok(proyectoService.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener proyecto por ID")
    public ResponseEntity<ProyectoDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(proyectoService.obtenerPorId(id));
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Listar proyectos por estado (vía path variable)")
    public ResponseEntity<List<ProyectoDTO>> listarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(proyectoService.listarPorEstado(estado));
    }

    @PostMapping
    @Operation(summary = "Crear proyecto")
    public ResponseEntity<ProyectoDTO> crear(@Valid @RequestBody ProyectoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(proyectoService.crear(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar proyecto")
    public ResponseEntity<ProyectoDTO> actualizar(@PathVariable Long id,
                                                   @Valid @RequestBody ProyectoDTO dto) {
        return ResponseEntity.ok(proyectoService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar proyecto")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        proyectoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
