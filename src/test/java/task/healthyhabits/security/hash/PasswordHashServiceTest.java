package task.healthyhabits.security.hash;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import task.healthyhabits.security.hash.PasswordHashService;
import task.healthyhabits.security.hash.PasswordPolicy;

@ExtendWith(MockitoExtension.class)

class PasswordHashServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordPolicy passwordPolicy;

    private PasswordHashService service;

    @BeforeEach
    void setUp() {
        service = new PasswordHashService(passwordEncoder, passwordPolicy, "pep");
    }

    @Test
    void encodeValidatesPasswordAndAppendsPepper() {
        when(passwordEncoder.encode("secretpep")).thenReturn("encoded");

        String result = service.encode("secret");

        assertThat(result).isEqualTo("encoded");
        verify(passwordPolicy).validate("secret");
        verify(passwordEncoder).encode("secretpep");
    }

    @Test
    void matchesReturnsFalseWhenStoredPasswordNull() {
        boolean result = service.matches("secret", null);

        assertThat(result).isFalse();
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void matchesReturnsFalseWhenStoredPasswordBlank() {
        boolean result = service.matches("secret", " \t");

        assertThat(result).isFalse();
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void matchesDelegatesToPasswordEncoder() {
        when(passwordEncoder.matches("secretpep", "hash")).thenReturn(true);

        boolean result = service.matches("secret", "hash");

        assertThat(result).isTrue();
        verify(passwordEncoder).matches("secretpep", "hash");
    }
}