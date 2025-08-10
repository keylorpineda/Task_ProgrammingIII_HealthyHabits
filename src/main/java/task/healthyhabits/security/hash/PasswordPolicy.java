package task.healthyhabits.security.hash;

//Contrato: es para validar contraseñas en texto plano

public interface PasswordPolicy {
    void validate(String rawPassword);
}
