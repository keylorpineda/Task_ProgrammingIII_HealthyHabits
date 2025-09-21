package task.healthyhabits.configTest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.assertj.core.api.Assertions.assertThat;

import graphql.scalars.ExtendedScalars;
import graphql.schema.idl.RuntimeWiring;
import org.junit.jupiter.api.Test;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import task.healthyhabits.config.GraphQlConfig;

class GraphQlConfigTest {

    @Test
    void runtimeWiringConfigurerRegistersExtendedScalars() {
        GraphQlConfig config = new GraphQlConfig();
        RuntimeWiringConfigurer configurer = config.runtimeWiringConfigurer();
        RuntimeWiring.Builder builder = RuntimeWiring.newRuntimeWiring();

        configurer.configure(builder);

        RuntimeWiring wiring = builder.build();
        assertThat(wiring.getScalars())
                .containsEntry(ExtendedScalars.DateTime.getName(), ExtendedScalars.DateTime)
                .containsEntry(ExtendedScalars.Date.getName(), ExtendedScalars.Date)
                .containsEntry(ExtendedScalars.LocalTime.getName(), ExtendedScalars.LocalTime)
                .containsEntry(ExtendedScalars.GraphQLLong.getName(), ExtendedScalars.GraphQLLong);
    }
}