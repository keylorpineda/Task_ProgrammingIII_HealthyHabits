package task.healthyhabits.security.hash;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import task.healthyhabits.exceptions.InvalidPasswordException;
import task.healthyhabits.security.hash.DefaultPasswordPolicy;

class DefaultPasswordPolicyTest {

    private final DefaultPasswordPolicy policy = new DefaultPasswordPolicy();

    @Test
    void validateThrowsWhenPasswordNull() {
        assertThatThrownBy(() -> policy.validate(null))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessage("La contraseña no puede estar vacía.");
    }

    @Test
    void validateThrowsWhenPasswordBlank() {
        assertThatThrownBy(() -> policy.validate("   "))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessage("La contraseña no puede estar vacía.");
    }

    @Test
    void validateThrowsWhenTooShort() {
        assertThatThrownBy(() -> policy.validate("Abc12"))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessage("La contraseña debe tener al menos 8 caracteres.");
    }

    @Test
    void validateThrowsWhenMissingUppercase() {
        assertThatThrownBy(() -> policy.validate("abcdefg1"))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessage("Debe incluir al menos una letra mayúscula.");
    }

    @Test
    void validateThrowsWhenMissingLowercase() {
        assertThatThrownBy(() -> policy.validate("ABCDEFG1"))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessage("Debe incluir al menos una letra minúscula.");
    }

    @Test
    void validateThrowsWhenMissingDigit() {
        assertThatThrownBy(() -> policy.validate("Abcdefgh"))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessage("Debe incluir al menos un dígito.");
    }

    @Test
    void validateSucceedsForValidPassword() {
        assertThatCode(() -> policy.validate("Valid123")).doesNotThrowAnyException();
    }
}