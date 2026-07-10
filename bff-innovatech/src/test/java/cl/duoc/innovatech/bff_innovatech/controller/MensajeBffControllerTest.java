package cl.duoc.innovatech.bff_innovatech.controller;

import cl.duoc.innovatech.bff_innovatech.dto.request.MensajeRequest;
import cl.duoc.innovatech.bff_innovatech.dto.response.ConversacionResponse;
import cl.duoc.innovatech.bff_innovatech.dto.response.MensajeResponse;
import cl.duoc.innovatech.bff_innovatech.service.MensajeBffService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MensajeBffController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("MensajeBffController - Pruebas Unitarias (MockMvc)")
class MensajeBffControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MensajeBffService mensajeBffService;

    @Autowired
    private ObjectMapper objectMapper;

    private MensajeResponse mensajeMock;

    @BeforeEach
    void setUp() {
        mensajeMock = MensajeResponse.builder()
                .id(1L).remitenteEmail("bryan@innovatech.cl").destinatarioEmail("karla@innovatech.cl")
                .contenido("Hola Karla").leido(false).build();
    }

    @Test
    @DisplayName("POST /api/bff/mensajes - retorna 201 al enviar")
    void enviar_retorna201() throws Exception {
        MensajeRequest request = MensajeRequest.builder()
                .remitenteEmail("bryan@innovatech.cl").destinatarioEmail("karla@innovatech.cl")
                .contenido("Hola Karla").build();
        when(mensajeBffService.enviar(any())).thenReturn(mensajeMock);

        mockMvc.perform(post("/api/bff/mensajes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.contenido").value("Hola Karla"));
    }

    @Test
    @DisplayName("POST /api/bff/mensajes - retorna 400 si falta contenido")
    void enviar_retorna400SiFaltaContenido() throws Exception {
        MensajeRequest request = MensajeRequest.builder()
                .remitenteEmail("bryan@innovatech.cl").destinatarioEmail("karla@innovatech.cl").build();

        mockMvc.perform(post("/api/bff/mensajes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/bff/mensajes/conversacion - retorna 200 con historial")
    void obtenerConversacion_retorna200() throws Exception {
        when(mensajeBffService.obtenerConversacion("bryan@innovatech.cl", "karla@innovatech.cl"))
                .thenReturn(List.of(mensajeMock));

        mockMvc.perform(get("/api/bff/mensajes/conversacion")
                        .param("email1", "bryan@innovatech.cl")
                        .param("email2", "karla@innovatech.cl"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].contenido").value("Hola Karla"));
    }

    @Test
    @DisplayName("GET /api/bff/mensajes/inbox - retorna 200 con resumen")
    void obtenerInbox_retorna200() throws Exception {
        ConversacionResponse conv = ConversacionResponse.builder()
                .contactoEmail("karla@innovatech.cl").ultimoMensaje("Hola Karla").noLeidos(1).build();
        when(mensajeBffService.obtenerInbox("bryan@innovatech.cl")).thenReturn(List.of(conv));

        mockMvc.perform(get("/api/bff/mensajes/inbox").param("email", "bryan@innovatech.cl"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].contactoEmail").value("karla@innovatech.cl"));
    }

    @Test
    @DisplayName("PATCH /api/bff/mensajes/marcar-leidos - retorna 200")
    void marcarComoLeidos_retorna200() throws Exception {
        when(mensajeBffService.marcarComoLeidos("bryan@innovatech.cl", "karla@innovatech.cl")).thenReturn(2);

        mockMvc.perform(patch("/api/bff/mensajes/marcar-leidos")
                        .param("destinatario", "bryan@innovatech.cl")
                        .param("remitente", "karla@innovatech.cl"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.actualizados").value(2));
    }
}
