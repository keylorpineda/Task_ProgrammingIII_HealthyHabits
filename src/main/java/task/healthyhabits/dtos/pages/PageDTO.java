package task.healthyhabits.dtos.pages;

import java.util.List;

public record PageDTO<T>(
        List<T> content,
        int totalPages,
        long totalElements,
        int size,
        int number) {
    public static <T> PageDTO<T> from(org.springframework.data.domain.Page<T> p) {
        return new PageDTO<>(p.getContent(), p.getTotalPages(), p.getTotalElements(), p.getSize(), p.getNumber());
    }
}
