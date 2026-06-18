package cl.duoc.innovatech.ms_analitica.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import cl.duoc.innovatech.ms_analitica.model.KpiMetrica;
import cl.duoc.innovatech.ms_analitica.model.enums.CategoriaKpi;

@Repository
public interface KpiMetricaRepository extends JpaRepository<KpiMetrica, Long> {

    List<KpiMetrica> findByCategoria(CategoriaKpi categoria);

    List<KpiMetrica> findTop5ByOrderByFechaCalculoDesc();

    @Query("SELECT k.categoria, AVG(k.valor) FROM KpiMetrica k GROUP BY k.categoria")
    List<Object[]> promedioValorPorCategoria();

    boolean existsByNombreKpi(String nombreKpi);
}