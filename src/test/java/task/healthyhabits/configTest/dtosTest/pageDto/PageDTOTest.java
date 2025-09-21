package task.healthyhabits.dtosTest.pageDto;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import task.healthyhabits.dtos.pages.PageDTO;

import static org.assertj.core.api.Assertions.assertThat;

class PageDTOTest {

    @Test
    void fromShouldMirrorPageMetadata() {
        List<String> content = List.of("A", "B");
        PageImpl<String> page = new PageImpl<>(content, PageRequest.of(2, 5), 17);

        PageDTO<String> dto = PageDTO.from(page);

        assertThat(dto.content()).containsExactlyElementsOf(content);
        assertThat(dto.totalPages()).isEqualTo(page.getTotalPages());
        assertThat(dto.totalElements()).isEqualTo(page.getTotalElements());
        assertThat(dto.size()).isEqualTo(page.getSize());
        assertThat(dto.number()).isEqualTo(page.getNumber());
        assertThat(dto.hasNext()).isEqualTo(page.hasNext());
        assertThat(dto.hasPrevious()).isEqualTo(page.hasPrevious());
    }
}