package cl.duoc.innovatech.ms_recursos.exception;

public class EmailDuplicadoException extends RuntimeException {
    public EmailDuplicadoException(String email) {
        super("Ya existe un empleado con el email: " + email);
    }
}
