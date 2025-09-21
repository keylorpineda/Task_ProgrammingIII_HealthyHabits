package task.healthyhabits.security;


import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

import jakarta.servlet.ServletException;

import org.springframework.security.access.AccessDeniedException;
import task.healthyhabits.security.SecurityHandlersConfig;

class SecurityHandlersConfigTest {

    private final SecurityHandlersConfig config = new SecurityHandlersConfig();

    @Test
    void authenticationEntryPointWritesUnauthorizedJson() throws IOException, ServletException {
        var entryPoint = config.authenticationEntryPoint();
        MockHttpServletResponse response = new MockHttpServletResponse();

        entryPoint.commence(new MockHttpServletRequest(), response, new AuthenticationCredentialsNotFoundException(""));

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(response.getContentAsString()).isEqualTo("{\"error\":\"unauthorized\"}");
    }

    @Test
    void accessDeniedHandlerWritesForbiddenJson() throws Exception {
        var handler = config.accessDeniedHandler();
        MockHttpServletResponse response = new MockHttpServletResponse();

        handler.handle(new MockHttpServletRequest(), response, new AccessDeniedException(""));

        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(response.getContentAsString()).isEqualTo("{\"error\":\"forbidden\"}");
    }
}
