package task.healthyhabits.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import task.healthyhabits.config.GraphQlConfig;
import task.healthyhabits.security.JWT.JwtAuthFilter;

@SpringBootTest(
        classes = { SecurityConfig.class, GraphQlConfig.class },
        properties = {
                "security.jwt.secret=QUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVo0MzIxMjM=",
                "security.jwt.expiration-ms=3600000",
                "security.password.pepper=test-pepper"
        }
)
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class,
        FlywayAutoConfiguration.class
})
class SecurityConfigIntegrationTest {

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private AuthenticationEntryPoint authenticationEntryPoint;

    @MockBean
    private AccessDeniedHandler accessDeniedHandler;

    @Test
    void securityFilterChainContainsJwtAuthFilter() {
        DefaultSecurityFilterChain chain = (DefaultSecurityFilterChain) securityFilterChain;
        assertThat(chain.getFilters()).contains(jwtAuthFilter);
    }
}
