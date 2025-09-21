package task.healthyhabits.security;


import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;

import task.healthyhabits.security.JWT.JwtAuthFilter;

@SpringBootTest(properties = {
        "security.jwt.secret=QUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVo0MzIxMjM=",
        "security.jwt.expiration-ms=3600000",
        "security.password.pepper=test-pepper"
})
class SecurityConfigIntegrationTest {

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Test
    void securityFilterChainContainsJwtAuthFilter() {
        assertThat(securityFilterChain).isInstanceOf(DefaultSecurityFilterChain.class);
        DefaultSecurityFilterChain defaultChain = (DefaultSecurityFilterChain) securityFilterChain;
        assertThat(defaultChain.getFilters()).contains(jwtAuthFilter);
    }
}