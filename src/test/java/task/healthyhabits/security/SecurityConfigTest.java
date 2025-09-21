package task.healthyhabits.security;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.Filter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import task.healthyhabits.security.JWT.JwtAuthFilter;
import task.healthyhabits.security.SecurityConfig;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

    @Mock
    private JwtAuthFilter jwtAuthFilter;

    @Mock
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Mock
    private AccessDeniedHandler accessDeniedHandler;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @Mock
    private AuthenticationManager authenticationManager;

    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() throws Exception {
        securityConfig = new SecurityConfig(jwtAuthFilter, authenticationEntryPoint, accessDeniedHandler);
        when(authenticationConfiguration.getAuthenticationManager()).thenReturn(authenticationManager);
    }

    @Test
    void securityFilterChainAddsJwtFilterBeforeUsernamePassword() throws Exception {
        HttpSecurity http = buildHttpSecurity();

        DefaultSecurityFilterChain chain = (DefaultSecurityFilterChain) securityConfig.securityFilterChain(http);

        List<Filter> filters = chain.getFilters();
        long jwtInstances = filters.stream().filter(f -> f == jwtAuthFilter).count();
        assertThat(jwtInstances).isEqualTo(1);

        int jwtIndex = filters.indexOf(jwtAuthFilter);
        int usernameIndex = -1;
        for (int i = 0; i < filters.size(); i++) {
            if (filters.get(i) instanceof UsernamePasswordAuthenticationFilter) {
                usernameIndex = i;
                break;
            }
        }

        assertThat(usernameIndex).isGreaterThanOrEqualTo(0);
        assertThat(jwtIndex).isLessThan(usernameIndex);
        assertThat(http.getSharedObject(SessionCreationPolicy.class)).isEqualTo(SessionCreationPolicy.STATELESS);
    }

    @Test
    void authenticationManagerDelegatesToConfiguration() throws Exception {
        AuthenticationManager manager = securityConfig.authenticationManager(authenticationConfiguration);

        assertThat(manager).isSameAs(authenticationManager);
        verify(authenticationConfiguration, times(1)).getAuthenticationManager();
    }

    @Test
    void corsConfigurationSourceAllowsConfiguredValues() {
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();
        MockHttpServletRequest request = new MockHttpServletRequest(HttpMethod.GET.name(), "/any");

        CorsConfiguration configuration = source.getCorsConfiguration(request);
        assertThat(configuration).isNotNull();
        assertThat(configuration.getAllowedOriginPatterns()).containsExactly("*");
        assertThat(configuration.getAllowedMethods())
                .containsExactlyInAnyOrder("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
        assertThat(configuration.getAllowedHeaders()).containsExactly("*");
        assertThat(configuration.getExposedHeaders()).containsExactly("Authorization");
        assertThat(configuration.getAllowCredentials()).isTrue();
    }

    private HttpSecurity buildHttpSecurity() {
        ObjectPostProcessor<Object> opp = new ObjectPostProcessor<>() {
            @Override
            public <O> O postProcess(O object) {
                return object;
            }
        };
        AuthenticationManagerBuilder authBuilder = new AuthenticationManagerBuilder(opp);
        authBuilder.parentAuthenticationManager(authenticationManager);
        Map<Class<?>, Object> sharedObjects = new HashMap<>();
        sharedObjects.put(AuthenticationManager.class, authenticationManager);
        sharedObjects.put(AuthenticationManagerBuilder.class, authBuilder);
        return new HttpSecurity(opp, authBuilder, sharedObjects);
    }
}