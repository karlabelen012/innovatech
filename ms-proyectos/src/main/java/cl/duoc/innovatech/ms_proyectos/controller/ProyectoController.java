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
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.innovatech.ms_proyectos.model.Proyecto;
import cl.duoc.innovatech.ms_proyectos.service.ProyectoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/proyectos")
@RequiredArgsConstructor
public class ProyectoController {

    private final ProyectoService proyectoService;

    @GetMapping
    public ResponseEntity<List<Proyecto>> listar() {
        return ResponseEntity.ok(proyectoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Proyecto> obtenerPorId(@PathVariable Long id) {
        return proyectoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Proyecto>> listarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(proyectoService.listarPorEstado(estado));
    }

    @PostMapping
    public ResponseEntity<Proyecto> crear(@Valid @RequestBody Proyecto proyecto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(proyectoService.guardar(proyecto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Proyecto> actualizar(@PathVariable Long id,
                                               @Valid @RequestBody Proyecto proyecto) {
        return proyectoService.actualizar(id, proyecto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (proyectoService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}