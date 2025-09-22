package task.healthyhabits.configTest.dtosTest.outputs;

import org.junit.jupiter.api.Test;
import task.healthyhabits.dtos.outputs.RoleOutputDTO;
import task.healthyhabits.models.Permission;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class RoleOutputDTOTest {

    @Test
    void shouldExposePermission() {
        RoleOutputDTO role = new RoleOutputDTO(1L, "Coach", Permission.ROUTINE_EDITOR);

        assertAll(
                () -> assertThat(role.getId()).isEqualTo(1L),
                () -> assertThat(role.getName()).isEqualTo("Coach"),
                () -> assertThat(role.getPermission()).isEqualTo(Permission.ROUTINE_EDITOR)
        );

        RoleOutputDTO same = new RoleOutputDTO(1L, "Coach", Permission.ROUTINE_EDITOR);
        RoleOutputDTO different = new RoleOutputDTO(2L, "Viewer", Permission.ROUTINE_READ);

        assertAll(
                () -> assertThat(role).isEqualTo(same),
                () -> assertThat(role).hasSameHashCodeAs(same),
                () -> assertThat(role).isNotEqualTo(different),
                () -> assertThat(role.toString()).contains("Coach", Permission.ROUTINE_EDITOR.name())
        );
    }
}