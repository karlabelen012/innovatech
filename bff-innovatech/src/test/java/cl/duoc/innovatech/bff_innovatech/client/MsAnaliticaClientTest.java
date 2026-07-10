package cl.duoc.innovatech.bff_innovatech.client;

import cl.duoc.innovatech.bff_innovatech.dto.response.DashboardResponse;
import cl.duoc.innovatech.bff_innovatech.dto.response.KpiMetricaResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MsAnaliticaClient - Pruebas Unitarias")
class MsAnaliticaClientTest {

    @Mock
    private RestTemplate restTemplate;

    private MsAnaliticaClient client;

    @BeforeEach
    void setUp() {
        client = new MsAnaliticaClient(restTemplate);
        ReflectionTestUtils.setField(client, "baseUrl", "http://localhost:8083");
    }

    @Test
    @DisplayName("obtenerDashboard() - retorna el dashboard del microservicio")
    void obtenerDashboard_retornaDashboard() {
        DashboardResponse resp = DashboardResponse.builder().totalProyectos(12L).build();
        when(restTemplate.getForObject("http://localhost:8083/api/v1/dashboard", DashboardResponse.class))
                .thenReturn(resp);

        assertThat(client.obtenerDashboard().getTotalProyectos()).isEqualTo(12);
    }

    @Test
    @DisplayName("listarKpis() - retorna la lista de KPIs")
    void listarKpis_retornaLista() {
        KpiMetricaResponse kpi = KpiMetricaResponse.builder().id(1L).nombreKpi("Avance").build();
        when(restTemplate.exchange(eq("http://localhost:8083/api/v1/kpis"), eq(HttpMethod.GET), any(),
                any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(List.of(kpi)));

        assertThat(client.listarKpis()).hasSize(1);
    }

    @Test
    @DisplayName("listarKpisPorCategoria() - consulta la ruta por categoría")
    void listarKpisPorCategoria_retornaFiltrados() {
        KpiMetricaResponse kpi = KpiMetricaResponse.builder().id(1L).categoria("RECURSOS").build();
        when(restTemplate.exchange(eq("http://localhost:8083/api/v1/kpis/categoria/RECURSOS"), eq(HttpMethod.GET),
                any(), any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(List.of(kpi)));

        assertThat(client.listarKpisPorCategoria("RECURSOS")).hasSize(1);
    }
}
