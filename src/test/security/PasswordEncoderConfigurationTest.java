package task.healthyhabits.security;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

class PasswordEncoderConfigurationTest {

    private final PasswordEncoderConfiguration configuration = new PasswordEncoderConfiguration();

    @Test
    void passwordEncoderReturnsBCryptWithStrengthTen() {
        PasswordEncoder encoder = configuration.passwordEncoder();

        assertThat(encoder).isInstanceOf(BCryptPasswordEncoder.class);
        Object strength = ReflectionTestUtils.getField(encoder, "strength");
        assertThat(strength).isInstanceOf(Integer.class);
        assertThat((Integer) strength).isEqualTo(10);
        String hash = encoder.encode("password");
        assertThat(encoder.matches("password", hash)).isTrue();
    }
}