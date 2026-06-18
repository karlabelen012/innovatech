package cl.duoc.innovatech.bff_innovatech.exception;

public class MicroservicioNoDisponibleException extends RuntimeException {
    public MicroservicioNoDisponibleException(String servicio, Throwable cause) {
        super("Microservicio no disponible: " + servicio, cause);
    }
}
