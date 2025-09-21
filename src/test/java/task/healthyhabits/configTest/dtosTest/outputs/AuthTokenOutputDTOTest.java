package task.healthyhabits.dtosTest.outputs;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import task.healthyhabits.dtos.outputs.AuthTokenOutputDTO;
import task.healthyhabits.dtos.outputs.UserOutputDTO;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class AuthTokenOutputDTOTest {

    @Test
    void shouldExposeTokenData() {
        UserOutputDTO user = new UserOutputDTO();
        OffsetDateTime expires = OffsetDateTime.parse("2024-01-01T10:15:30Z");
        AuthTokenOutputDTO token = new AuthTokenOutputDTO("token", expires, user);

        assertAll(
                () -> assertThat(token.getToken()).isEqualTo("token"),
                () -> assertThat(token.getExpiresAt()).isEqualTo(expires),
                () -> assertThat(token.getUser()).isSameAs(user)
        );

        AuthTokenOutputDTO same = new AuthTokenOutputDTO("token", expires, user);
        AuthTokenOutputDTO different = new AuthTokenOutputDTO("other", expires, user);

        assertAll(
                () -> assertThat(token).isEqualTo(same),
                () -> assertThat(token).hasSameHashCodeAs(same),
                () -> assertThat(token).isNotEqualTo(different),
                () -> assertThat(token.toString()).contains("token")
        );
    }
}