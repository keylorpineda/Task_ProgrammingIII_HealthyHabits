package task.healthyhabits.security.hash;

import org.springframework.stereotype.Component;

@Component
public class DefaultPasswordPolicy implements PasswordPolicy {

    @Override
    public void validate(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }
        if (rawPassword.length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.");
        }
        if (!rawPassword.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Debe incluir al menos una letra mayúscula.");
        }
        if (!rawPassword.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Debe incluir al menos una letra minúscula.");
        }
        if (!rawPassword.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Debe incluir al menos un dígito.");
        }
        /*if (!rawPassword.matches(".*[^\\w\\s].*")) {
            throw new IllegalArgumentException("Debe incluir al menos un símbolo.");
        }*/

        // Si se ocupa mas seguridad se puede agregar la de los simbolos
    }
}
