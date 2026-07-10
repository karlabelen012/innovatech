package cl.duoc.innovatech.ms_mensajeria.controller;

import cl.duoc.innovatech.ms_mensajeria.dto.ConversacionDTO;
import cl.duoc.innovatech.ms_mensajeria.dto.MensajeDTO;
import cl.duoc.innovatech.ms_mensajeria.exception.RecursoNoEncontradoException;
import cl.duoc.innovatech.ms_mensajeria.service.MensajeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MensajeController.class)
class MensajeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MensajeService mensajeService;

    @Autowired
    private ObjectMapper objectMapper;

    private MensajeDTO dto;

    @BeforeEach
    void setUp() {
        dto = MensajeDTO.builder()
                .id(1L)
                .remitenteEmail("bryan@innovatech.cl")
                .remitenteNombre("Bryan Muñoz")
                .destinatarioEmail("karla@innovatech.cl")
                .destinatarioNombre("Karla Herrera")
                .contenido("Hola Karla")
                .fechaEnvio(LocalDateTime.now())
                .leido(false)
                .build();
    }

    @Test
    void enviar_valido_debeRetornar201() throws Exception {
        when(mensajeService.enviar(any(MensajeDTO.class))).thenReturn(dto);
        mockMvc.perform(post("/api/v1/mensajes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.contenido").value("Hola Karla"));
    }

    @Test
    void enviar_sinContenido_debeRetornar400() throws Exception {
        dto.setContenido(null);
        mockMvc.perform(post("/api/v1/mensajes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void obtenerPorId_existente_debeRetornar200() throws Exception {
        when(mensajeService.obtenerPorId(1L)).thenReturn(dto);
        mockMvc.perform(get("/api/v1/mensajes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contenido").value("Hola Karla"));
    }

    @Test
    void obtenerPorId_noExistente_debeRetornar404() throws Exception {
        when(mensajeService.obtenerPorId(99L)).thenThrow(new RecursoNoEncontradoException("Mensaje no encontrado"));
        mockMvc.perform(get("/api/v1/mensajes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void obtenerConversacion_debeRetornar200() throws Exception {
        when(mensajeService.obtenerConversacion("bryan@innovatech.cl", "karla@innovatech.cl"))
                .thenReturn(List.of(dto));
        mockMvc.perform(get("/api/v1/mensajes/conversacion")
                        .param("email1", "bryan@innovatech.cl")
                        .param("email2", "karla@innovatech.cl"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].contenido").value("Hola Karla"));
    }

    @Test
    void obtenerInbox_debeRetornar200() throws Exception {
        ConversacionDTO conv = ConversacionDTO.builder()
                .contactoEmail("karla@innovatech.cl")
                .contactoNombre("Karla Herrera")
                .ultimoMensaje("Hola Karla")
                .fechaUltimoMensaje(LocalDateTime.now())
                .noLeidos(1)
                .ultimoEnviadoPorMi(true)
                .build();
        when(mensajeService.obtenerInbox("bryan@innovatech.cl")).thenReturn(List.of(conv));
        mockMvc.perform(get("/api/v1/mensajes/inbox").param("email", "bryan@innovatech.cl"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].contactoEmail").value("karla@innovatech.cl"));
    }

    @Test
    void marcarComoLeidos_debeRetornar200() throws Exception {
        when(mensajeService.marcarComoLeidos("bryan@innovatech.cl", "karla@innovatech.cl")).thenReturn(2);
        mockMvc.perform(patch("/api/v1/mensajes/marcar-leidos")
                        .param("destinatario", "bryan@innovatech.cl")
                        .param("remitente", "karla@innovatech.cl"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actualizados").value(2));
    }
}
