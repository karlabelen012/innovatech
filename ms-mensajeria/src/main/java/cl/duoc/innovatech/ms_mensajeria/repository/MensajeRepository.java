package cl.duoc.innovatech.ms_mensajeria.repository;

import cl.duoc.innovatech.ms_mensajeria.model.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List;


public interface MensajeRepository extends JpaRepository<Mensaje, Long> {

    @Query("SELECT m FROM Mensaje m WHERE " +
           "(m.remitenteEmail = :email1 AND m.destinatarioEmail = :email2) OR " +
           "(m.remitenteEmail = :email2 AND m.destinatarioEmail = :email1) " +
           "ORDER BY m.fechaEnvio ASC")
    List<Mensaje> findConversacion(@Param("email1") String email1, @Param("email2") String email2);

    List<Mensaje> findByRemitenteEmailOrDestinatarioEmailOrderByFechaEnvioDesc(String remitenteEmail, String destinatarioEmail);

    @Modifying
    @Query("UPDATE Mensaje m SET m.leido = true WHERE m.destinatarioEmail = :destinatario " +
           "AND m.remitenteEmail = :remitente AND m.leido = false")
    int marcarComoLeidos(@Param("destinatario") String destinatarioEmail, @Param("remitente") String remitenteEmail);
}
