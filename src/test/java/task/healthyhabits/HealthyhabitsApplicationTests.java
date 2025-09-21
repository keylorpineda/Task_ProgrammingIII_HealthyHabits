package task.healthyhabits;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

class HealthyhabitsApplicationTests {

    @Test
    void contextLoads() {
        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
            mocked.when(() -> SpringApplication.run(HealthyhabitsApplication.class, new String[]{}))
                    .thenReturn(null);
            HealthyhabitsApplication.main(new String[]{});
            mocked.verify(() -> SpringApplication.run(HealthyhabitsApplication.class, new String[]{}));
        }
    }

    @Test
    void mainDoesNotThrow() {
        try (MockedStatic<SpringApplication> mocked = mockStatic(SpringApplication.class)) {
            mocked.when(() -> SpringApplication.run(HealthyhabitsApplication.class, new String[]{"--test"}))
                    .thenReturn(null);
            assertDoesNotThrow(() -> HealthyhabitsApplication.main(new String[]{"--test"}));
        }
    }
}